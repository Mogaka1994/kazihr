package com.projectx.pay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
public class PayApplication {

	@Autowired
	Environment env;

	public static void main(String[] args) {
		SpringApplication.run(PayApplication.class, args);
	}


//	@Bean
//	public Docket api() {
//		env.getRequiredProperty("swagger.enable");
//		boolean swaggerswitch = env.getRequiredProperty("swagger.enable").equals("true");
//		return new Docket(DocumentationType.SWAGGER_2)
//				.select()
//				.apis(RequestHandlerSelectors.basePackage("com.projectx.pay.controller"))
//				.paths(PathSelectors.any())
//				.build();
//				.apiInfo(apiInfo())
//				.apiInfo(apiInfo()).enable(swaggerswitch);

//	}

//	@Bean
//	private ApiInfo apiInfo() {
//		return new ApiInfo(
//				"Backend",
//				"Microservice",
//				"v1.0.0",
//				"Terms of service",
//				"polycarpmogaka16@gmail.com",
//				"License of API",
//				"#");
//	}



}
