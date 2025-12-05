package com.peelie.onboarding.infra;

import com.openai.springboot.OpenAIClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {
    @Bean
    public OpenAIClientCustomizer customizer() {
        return builder -> builder.maxRetries(2);
    }
}
