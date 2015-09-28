package sample.jc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sample.MessageService;

@Configuration
class aConfig {
	@Bean
	public MessageService messageService() {
		return new MessageService("a");
	}
}