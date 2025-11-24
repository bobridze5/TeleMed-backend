package com.devops_labs.userService.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

//    @Value("")
//    private String email;
//    private String password;


//    @Bean
//    public JavaMailSender getJavaMailSender(){
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//
//        mailSender.setUsername("dima101203@gmail.com");
//        mailSender.setPassword("password");
//
//        Properties properties = mailSender.getJavaMailProperties();
//        properties.put()
//    }
}
