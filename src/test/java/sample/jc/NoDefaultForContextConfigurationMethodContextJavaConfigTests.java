package sample.jc;

import static org.assertj.core.api.StrictAssertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.MethodContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.MessageService;

/**
 * How should we prevent
 * DependencyInjectionTestExecutionListener.prepareTestInstance from being
 * executed? One option is to provide our own TestExecutionListener, but there
 * is no easy way to exclude DependencyInjectionTestExecutionListener.
 *
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@MethodContextConfiguration
public class NoDefaultForContextConfigurationMethodContextJavaConfigTests {
	@Autowired
	MessageService messageService;

	@MethodContextConfiguration(classes = aConfig.class)
	@Test
	public void a() {
		assertThat(messageService.getMessage()).isEqualTo("a");
	}

	@MethodContextConfiguration(classes = bConfig.class)
	@Test
	public void b() {
		assertThat(messageService.getMessage()).isEqualTo("b");
	}
}