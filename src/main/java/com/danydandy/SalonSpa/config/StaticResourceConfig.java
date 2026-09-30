package com.danydandy.SalonSpa.config;

import com.danydandy.SalonSpa.config.properties.UploadProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.nio.file.Path;

@Configuration
public class StaticResourceConfig {

    @Bean
    public RouterFunction<ServerResponse> uploadsRouter(UploadProperties uploadProperties) {
        Path uploadsDir = Path.of(uploadProperties.dir()).toAbsolutePath().normalize();
        return RouterFunctions.resources("/uploads/**", new FileSystemResource(uploadsDir.toFile() + "/"));
    }
}
