package io.github.pavelshe11.networkingmicro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicUserApi() {
        return GroupedOpenApi.builder()
                .group("Account")
                .pathsToMatch("/networking/v1/account/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi(
            @Value("Networking API") String appDescription,
            @Value("$v1") String appVersion) {

        return new OpenAPI()
                .info(new Info()
                        .title("Networking Microservice API")
                        .version(appVersion)
                        .description(appDescription)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                        .contact(new Contact().name("email").email("test@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost/networking").description("Local Dev"),
                        new Server().url("https://api.example.com").description("Production")
                ));
    }
}
