package org.springframework.test.context.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;
import org.springframework.test.context.CacheAwareContextLoaderDelegate;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.MethodContextConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@SuppressWarnings("serial")
public final class DefaultMethodTestContext extends DefaultTestContext {

	private MergedContextConfiguration parentContextConfiguration;
	private CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate;
	private MergedContextConfiguration mergedContextConfiguration;

	public DefaultMethodTestContext(Class<?> testClass, MergedContextConfiguration mergedContextConfiguration,
			CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate) {
		super(testClass, mergedContextConfiguration, cacheAwareContextLoaderDelegate);
		this.parentContextConfiguration = mergedContextConfiguration;
		this.cacheAwareContextLoaderDelegate = cacheAwareContextLoaderDelegate;
	}

	@Override
	public Object getAttribute(String name) {
		if(DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE.equals(name)) {
			return true;
		}
		return super.getAttribute(name);
	}

	@Override
	public ApplicationContext getApplicationContext() {
		Method testMethod = getTestMethod();
		if(testMethod == null) {
			return super.getApplicationContext();
		}
		MethodContextConfiguration configuration = AnnotationUtils.findAnnotation(testMethod, MethodContextConfiguration.class);
		Class<?>[] classes = configuration.classes();
		if(classes.length == 0) {
			classes = getDefaultConfigClasses(getTestClass(), testMethod);
		}
		this.mergedContextConfiguration = new MergedContextConfiguration(
				this.parentContextConfiguration.getTestClass(),
				this.parentContextConfiguration.getLocations(),
				classes,
				this.parentContextConfiguration.getContextInitializerClasses(),
				this.parentContextConfiguration.getActiveProfiles(),
				this.parentContextConfiguration.getPropertySourceLocations(),
				this.parentContextConfiguration.getPropertySourceProperties(),
				this.parentContextConfiguration.getContextLoader(),
				this.cacheAwareContextLoaderDelegate,
				null);
		ApplicationContext context = this.cacheAwareContextLoaderDelegate.loadContext(this.mergedContextConfiguration);
		if (context instanceof ConfigurableApplicationContext) {
			@SuppressWarnings("resource")
			ConfigurableApplicationContext cac = (ConfigurableApplicationContext) context;
			Assert.state(cac.isActive(), "The ApplicationContext loaded for [" + parentContextConfiguration
					+ "] is not active. Ensure that the context has not been closed programmatically.");
		}
		return context;
	}

	@Override
	public void markApplicationContextDirty(HierarchyMode hierarchyMode) {
		MergedContextConfiguration toClose = this.mergedContextConfiguration;
		if(toClose == null) {
			toClose = this.parentContextConfiguration;
		}
		this.cacheAwareContextLoaderDelegate.closeContext(toClose, hierarchyMode);
	}

	private static Class<?>[] getDefaultConfigClasses(Class<?> declaringClass, Method testMethod) {
		Assert.notNull(declaringClass, "Declaring class must not be null");

		List<Class<?>> configClasses = new ArrayList<Class<?>>();

		for (Class<?> candidate : declaringClass.getDeclaredClasses()) {
			if (isDefaultConfigurationClassCandidate(testMethod, candidate)) {
				configClasses.add(candidate);
			}
		}

		return configClasses.toArray(new Class<?>[configClasses.size()]);
	}

	/**
	 * Determine if the supplied {@link Class} meets the criteria for being
	 * considered a <em>default configuration class</em> candidate.
	 * <p>Specifically, such candidates:
	 * <ul>
	 * <li>must not be {@code null}</li>
	 * <li>must not be {@code private}</li>
	 * <li>must not be {@code final}</li>
	 * <li>must be {@code static}</li>
	 * <li>must be annotated or meta-annotated with {@code @Configuration}</li>
	 * </ul>
	 * @param testMethod the test method to check for
	 * @param clazz the class to check
	 * @return {@code true} if the supplied class meets the candidate criteria
	 */
	private static boolean isDefaultConfigurationClassCandidate(Method testMethod, Class<?> clazz) {
		return (clazz != null && isStaticNonPrivateAndNonFinal(clazz) &&
				(AnnotationUtils.findAnnotation(clazz, Configuration.class) != null) &&
				clazz.getSimpleName().equals(testMethod.getName() + "Config"));
	}

	private static boolean isStaticNonPrivateAndNonFinal(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		int modifiers = clazz.getModifiers();
		return (Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers) && !Modifier.isFinal(modifiers));
	}
}