package com.jina.proj;

import java.io.IOException;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.jina.proj")
@EnableJpaRepositories(basePackages = "com.jina.proj")
public class ProjApplication {

	public static void main(String[] args) throws IOException {

		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		String classpath = resourceLoader.getResource("classpath:/").getURL().getPath();
		System.out.println("Classpath: " + classpath);
		System.out.println("혹시 여길 고치면..??");

   		SpringApplication springApplication = new SpringApplication(ProjApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        //springApplication.setLogStartupInfo(false);
        springApplication.run(args);


		//SpringApplication.run(ProjApplication.class, args);
	}

}
