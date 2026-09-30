package com.danydandy.SalonSpa.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.uploads")
public record UploadProperties(String dir) {
}
