package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.model.Info;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example"})
public class FriendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FriendApplication.class, args);
	}
}
