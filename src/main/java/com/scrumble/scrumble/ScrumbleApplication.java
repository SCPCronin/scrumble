package com.scrumble.scrumble;

import com.scrumble.scrumble.cinemaScraper.CinemaScraper;
import discord.OmniplexScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ScrumbleApplication {
	public static void main(String[] args) {
		SpringApplication.run(ScrumbleApplication.class, args);
	}

	@Bean
	public CinemaScraper cinemaScraper() {
		return new CinemaScraper();
	}

	@Bean
	public OmniplexScanner omniplexScanner() {
		return new OmniplexScanner();
	}
}
