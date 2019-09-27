package com;

import com.config.Swagger2Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@EnableAsync
@SpringBootApplication
public class SingalLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingalLoginApplication.class,args);
    }
}
