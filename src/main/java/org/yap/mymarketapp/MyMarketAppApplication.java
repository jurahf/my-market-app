package org.yap.mymarketapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyMarketAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMarketAppApplication.class, args);
	}


	@Bean
	public CommandLineRunner checkThymeleaf() {
		return args -> {
			try {
				Class.forName("org.thymeleaf.spring6.SpringTemplateEngine");
				System.out.println("✅ Thymeleaf is in classpath!");
			} catch (ClassNotFoundException e) {
				System.out.println("❌ Thymeleaf NOT found in classpath!");
			}
		};
	}
}
