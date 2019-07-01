package com.example.demoopenid;

import org.springframework.boot.autoconfigure.security.oauth2.client.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;

@EnableWebSecurity
/*
 * @EnableOAuth2 tells Spring Boot to read the endpoint in configuration
 * declared with spring.security.oauth2.resource.*
 * @EnableOAuth2 contains @EnableOAuth2Client which use security.oauth2.client.*
 * https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-security.html
 */
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("SSO")
public class AnnotationEnableOAuth2SsoSecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            .authorizeRequests()
              // In
              .antMatchers("/", "/login**").permitAll()
              // Any request must be authenticated
              .anyRequest().authenticated();
    ;

  }
}
