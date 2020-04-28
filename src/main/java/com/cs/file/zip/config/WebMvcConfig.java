package com.cs.file.zip.config;

import java.io.File;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 静态资源处理
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File file = new File("/root/springboot/");
        if (!file.exists()) {
            file.mkdirs();
        }
        registry.addResourceHandler("/springboot/**").addResourceLocations("file:/root/springboot/**");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}