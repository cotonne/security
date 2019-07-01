package com.example.demoopenid;

import org.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("!SSO")
public class OAuth2LoginSecurityConfiguration extends WebSecurityConfigurerAdapter {
  private final static Logger LOG = LoggerFactory.getLogger(OAuth2LoginSecurityConfiguration.class);

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            // Any request must be authenticated
            .authorizeRequests().anyRequest().authenticated()
            .and()
            .oauth2Login();
  }

}
