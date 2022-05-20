package com.home.logger.config;

import com.home.logger.LogClassBeanPostProcessor;
import com.home.logger.LogMethodBeanPostProcessor;
import com.home.logger.wrappedclass.LogClassWrappedImpl;
import com.home.logger.wrappedclass.LogMethodWrappedImpl;
import com.home.logger.wrappedclass.LogWrapped;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import({LogClassBeanPostProcessor.class, LogMethodBeanPostProcessor.class})
public class TestConfig {

    @Bean
    public LogWrapped logClassWrappedImpl() {
        return new LogClassWrappedImpl();
    }

    @Bean
    public LogWrapped logMethodWrappedImpl() {
        return new LogMethodWrappedImpl();
    }
}
