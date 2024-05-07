package br.com.controle.financeiro.controllers.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;


@TestConfiguration
public class ControllerUserTestConfig {


    @Bean
    @Primary
    public UserDetailsService userDetailsService() {

        return new CustomUserDetailsService();
    }

    @Bean
    ServletWebServerFactory servletWebServerFactory(){
        return new TomcatServletWebServerFactory();
    }
}
