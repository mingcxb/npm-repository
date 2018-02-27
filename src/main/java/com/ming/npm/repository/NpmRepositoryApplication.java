package com.ming.npm.repository;

import com.ming.npm.repository.filter.MyFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NpmRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(NpmRepositoryApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean testFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MyFilter());
        registration.addUrlPatterns("/*");
        registration.addUrlPatterns("/@*");
        registration.setName("MyFilter");
        registration.setOrder(1);
        return registration;
    }

}
