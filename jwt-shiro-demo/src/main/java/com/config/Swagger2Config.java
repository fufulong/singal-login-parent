package com.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class Swagger2Config {

    public ApiInfo apiInfo() {

        ApiInfo apiInfo = new ApiInfoBuilder().title("single-login 接口")
                .description("单点登录demo项目swagger2接口文档")
                .termsOfServiceUrl("http://localhost:8082/")
                .version("1.0")
                .build();
        return apiInfo;
    }

    @Bean
    public Docket createSwagger() {
        System.out.println("swagger2初始化>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Docket build = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sys"))
                .paths(PathSelectors.any())
                .build();
//                .securitySchemes(security());
        return build;
    }

//    private List<ApiKey> security() {
//        ArrayList<ApiKey> keys = new ArrayList<>();
//        keys.add(new ApiKey("token","token","header"));
//        return keys;
//    }
}
