package com.danydandy.SalonSpa.config;

import com.danydandy.SalonSpa.config.properties.CorsProperties;
import com.danydandy.SalonSpa.config.properties.JwtProperties;
import com.danydandy.SalonSpa.config.properties.SubscriptionProperties;
import com.danydandy.SalonSpa.config.properties.UploadProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class, UploadProperties.class, SubscriptionProperties.class})
public class AppPropertiesConfig {
}
