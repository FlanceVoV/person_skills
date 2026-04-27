package com.flance.framework.modules.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "flance.file")
public class FlanceFileConfig {

    private String uploadPath;

}
