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


/*
Сделать классы по последнему сообщению(режиму)
 -при отправке пользователем сообщения, проверяем его режим(последнее сообщение) и отправляем в обработчик для этого режима,
 	где обрабатываем.
 */