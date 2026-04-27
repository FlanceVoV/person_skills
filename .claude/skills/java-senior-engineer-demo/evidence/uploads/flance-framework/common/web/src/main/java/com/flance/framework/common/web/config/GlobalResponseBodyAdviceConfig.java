package com.flance.framework.common.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "flance.response.advice")
public class GlobalResponseBodyAdviceConfig {

    private List<String> ignoreUrls = Arrays.asList("/actuator/**", "/error","/model-talent-assessment/**");

    private boolean enable;

}
