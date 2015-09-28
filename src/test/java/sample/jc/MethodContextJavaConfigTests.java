package sample.jc;

import static org.assertj.core.api.StrictAssertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.MethodContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.MessageService;

@RunWith(SpringJUnit4ClassRunner.class)
@MethodContextConfiguration
public class MethodContextJavaConfigTests {
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

	@MethodContextConfiguration
	@Test
	public void defaultconfig() {
		assertThat(messageService.getMessage()).isEqualTo("defaultconfig");
	}

	@Configuration
	static class aConfig {
		@Bean
		public MessageService messageService() {
			return new MessageService("a");
		}
	}

	@Configuration
	static class defaultconfigConfig {
		@Bean
		public MessageService messageService() {
			return new MessageService("defaultconfig");
		}
	}

	@Configuration
	static class bConfig {
		@Bean
		public MessageService messageService() {
			return new MessageService("b");
		}
	}
}