package com.hotel.packcheck;

import com.hotel.packcheck.config.MqttConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MqttConfig.class)
public class PackcheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackcheckApplication.class, args);
	}

}
