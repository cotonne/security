package com.example.demoacl;

import net.sf.ehcache.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.ehcache.*;
import org.springframework.context.annotation.*;
import org.springframework.security.access.*;
import org.springframework.security.access.expression.method.*;
import org.springframework.security.acls.*;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.*;
import org.springframework.security.acls.model.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.cache.*;
import org.springframework.security.provisioning.*;
import org.springframework.util.*;

import javax.sql.*;
import java.util.*;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize/PostAuthorize
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            .authorizeRequests()
            .anyRequest().authenticated()
            .and().formLogin().and().httpBasic();
  }

  @Bean
  public MethodSecurityExpressionHandler securityExpressionHandler() {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    PermissionEvaluator evaluator = new AclPermissionEvaluator(dbAclService());
    // By default, the permission evaluator is DenyAllPermissionEvaluator (secure by default)
    // To enable ACL from DB, we need a new Evaluator
    handler.setPermissionEvaluator(evaluator);
    return handler;
  }

  private AclService dbAclService() {
    return new JdbcAclService(dataSource, lookupStrategy());
  }

  private LookupStrategy lookupStrategy() {
    return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), auditLogger());
  }

  private AclCache aclCache() {
    return new EhCacheBasedAclCache(ehCache(), permissionGrantingStrategy(), aclAuthorizationStrategy());
  }

  private AclAuthorizationStrategy aclAuthorizationStrategy() {
    return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  private PermissionGrantingStrategy permissionGrantingStrategy() {
    return new DefaultPermissionGrantingStrategy(auditLogger());
  }

  private Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);


  private AuditLogger auditLogger() {
    return (granted, ace) -> {
      Assert.notNull(ace, "AccessControlEntry required");
      if (ace instanceof AuditableAccessControlEntry) {
        AuditableAccessControlEntry auditableAce = (AuditableAccessControlEntry) ace;
        if (granted && auditableAce.isAuditSuccess()) {
          logger.warn("GRANTED due to ACE: " + ace);
        } else if (!granted && auditableAce.isAuditFailure()) {
          logger.warn("DENIED due to ACE: " + ace);
        }
      }
    };
  }

  private Ehcache ehCache() {
    EhCacheFactoryBean factory = new EhCacheFactoryBean();
    factory.setCacheName("aclCache");
    factory.setCacheManager(new EhCacheManagerFactoryBean().getObject());
    factory.afterPropertiesSet();
    return Objects.requireNonNull(factory.getObject());
  }


}
