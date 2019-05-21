package com.ghostflow;

import com.ghostflow.http.security.Constants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties({Constants.Security.class})
public class Main {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Main.class)
			.properties("spring.config.location:${HOME}/")
			.build()
			.run(args);
	}
}
