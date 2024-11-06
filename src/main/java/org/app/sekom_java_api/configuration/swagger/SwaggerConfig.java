package org.app.sekom_java_api.configuration.swagger;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI defineOpenApi() {
        Server local = new Server();
        local.setUrl("http://localhost:8082");
        local.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Atakan Bayrak");
        myContact.setEmail("atakanbayrak5548@gmail.com");

        Info information = new Info()
                .title("Sekom Bank API")
                .version("1.0")
                .description("This API exposes endpoints to manage sekom-bank-application.")
                .contact(myContact);
        return new OpenAPI()
                .info(information)
                .servers(List.of(local))
                .components(new io.swagger.v3.oas.models.Components());
    }

}
