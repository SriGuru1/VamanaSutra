package com.urlShortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlShortenerApplication {

	public static void main(String[] args) {
		String mysqlUrl = System.getenv("MYSQL_URL");
		if (mysqlUrl != null && mysqlUrl.startsWith("mysql://")) {
			System.setProperty("spring.datasource.url", "jdbc:" + mysqlUrl);
		}

		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl != null) {
			if (databaseUrl.startsWith("postgres://")) {
				System.setProperty("spring.datasource.url", "jdbc:postgresql://" + databaseUrl.substring("postgres://".length()));
			} else if (databaseUrl.startsWith("mysql://")) {
				System.setProperty("spring.datasource.url", "jdbc:" + databaseUrl);
			}
		}

		SpringApplication.run(UrlShortenerApplication.class, args);
	}

}
