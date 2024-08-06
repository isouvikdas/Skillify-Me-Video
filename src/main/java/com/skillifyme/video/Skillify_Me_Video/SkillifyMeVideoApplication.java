package com.skillifyme.video.Skillify_Me_Video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SkillifyMeVideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillifyMeVideoApplication.class, args);
	}

}
