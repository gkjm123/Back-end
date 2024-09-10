package com.onedrinktoday.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BackEndApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackEndApplication.class, args);
  }

}
