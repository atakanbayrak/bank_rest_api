package org.app.super_app.configuration.swagger;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI defineOpenApi() {
        Server local = new Server();
        local.setUrl("http://localhost:8082");
        local.setDescription("Development");

        Server remote = new Server();
        remote.setUrl("http://10.101.55.94:8082");
        remote.setDescription("Production");


        Contact myContact = new Contact();
        myContact.setName("Atakan Bayrak");
        myContact.setEmail("atakanbayrak5548@gmail.com");

        Info information = new Info()
                .title("Super App System API")
                .version("1.0")
                .description("This API exposes endpoints to manage superapp.")
                .contact(myContact);
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        // Security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        // Return OpenAPI definition
        return new OpenAPI()
                .info(information)
                .servers(List.of(local,remote))
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("Authorization", securityScheme));
    }

}
