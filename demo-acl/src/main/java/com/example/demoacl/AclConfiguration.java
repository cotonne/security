package com.example.demoacl;

import net.sf.ehcache.Ehcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.AuditableAccessControlEntry;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
public class AclConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean()
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
