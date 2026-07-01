package com.urlShortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.net.URI;

@SpringBootApplication
public class UrlShortenerApplication {

	public static void main(String[] args) {
		configureDatabaseProperties("DATABASE_URL");
		configureDatabaseProperties("MYSQL_URL");

		SpringApplication.run(UrlShortenerApplication.class, args);
	}

	private static void configureDatabaseProperties(String envVarName) {
		String connectionUrl = System.getenv(envVarName);
		if (connectionUrl == null || connectionUrl.trim().isEmpty()) {
			return;
		}

		try {
			if (!connectionUrl.contains("://")) {
				return;
			}

			String workingUrl = connectionUrl;
			if (workingUrl.startsWith("postgres://")) {
				workingUrl = "postgresql://" + workingUrl.substring("postgres://".length());
			}

			URI uri = new URI(workingUrl);
			String scheme = uri.getScheme();
			String host = uri.getHost();
			int port = uri.getPort();
			String path = uri.getPath();

			String userInfo = uri.getUserInfo();
			String username = null;
			String password = null;

			if (userInfo != null && userInfo.contains(":")) {
				int colonIndex = userInfo.indexOf(":");
				username = userInfo.substring(0, colonIndex);
				password = userInfo.substring(colonIndex + 1);
			} else if (userInfo != null) {
				username = userInfo;
			}

			String dbName = (path != null && path.length() > 1) ? path.substring(1) : "";
			String jdbcProtocol = scheme;

			String jdbcUrl = "jdbc:" + jdbcProtocol + "://" + host + (port != -1 ? ":" + port : "") + "/" + dbName;

			System.setProperty("spring.datasource.url", jdbcUrl);
			if (username != null) {
				System.setProperty("spring.datasource.username", username);
			}
			if (password != null) {
				System.setProperty("spring.datasource.password", password);
			}

			System.out.println("Configured datasource properties from environment variable " + envVarName);
		} catch (Exception e) {
			System.err.println("Error parsing database connection string: " + e.getMessage());
			if (connectionUrl.startsWith("mysql://")) {
				System.setProperty("spring.datasource.url", "jdbc:" + connectionUrl);
			}
		}
	}

}
