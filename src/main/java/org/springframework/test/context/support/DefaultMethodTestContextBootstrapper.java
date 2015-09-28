package org.springframework.test.context.support;

import org.springframework.test.context.TestContext;

public class DefaultMethodTestContextBootstrapper extends DefaultTestContextBootstrapper {

	@Override
	public TestContext buildTestContext() {
		Class<?> testClass = getBootstrapContext().getTestClass();
		return new DefaultMethodTestContext(testClass, buildMergedContextConfiguration(),
				getCacheAwareContextLoaderDelegate());
	}

}
