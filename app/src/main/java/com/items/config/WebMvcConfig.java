package com.items.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@PropertySource("classpath:/resources/application.properties")
public class WebMvcConfig implements WebMvcConfigurer {

    // ���� ������ ��ġ
	@Value("${file.connectPath}")
	private String connectPath;
	
	@Value("${file.resourcePath}")
	private String resourcePath;
    // web root�� �ƴ� �ܺ� ��ο� �ִ� ���ҽ��� url�� �ҷ��� �� �ֵ��� ����
    // ���� localhost:8080/resources/user/1234.jpg
    // resourcePath�� �������� ����������ġ�� �������ݴϴ�.
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(connectPath)
                .addResourceLocations(resourcePath);
    }
    
}
