package ru.AliceTelegramBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class AliceTelegramBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AliceTelegramBotApplication.class, args);
	}

}
