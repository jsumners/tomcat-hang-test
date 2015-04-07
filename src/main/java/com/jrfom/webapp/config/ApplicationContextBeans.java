package com.jrfom.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrfom.webapp.dao.PersonRedisStore;
import com.jrfom.webapp.dao.RedisStore;
import com.jrfom.webapp.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool.factory.PoolingContextSource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@PropertySources({
  @PropertySource("classpath:application-${spring.profiles.active}.properties")
})
public class ApplicationContextBeans {
  @Autowired
  private Environment env;

  private final String DEV_PROFILE_NAME = "dev";

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean(destroyMethod = "destroy")
  public JedisPool jedisPool() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(Runtime.getRuntime().availableProcessors());
    poolConfig.setTestOnBorrow(true);

    JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);
    return jedisPool;
  }

  @Bean
  public RedisStore<Person> personRedisStore() {
    return new PersonRedisStore();
  }

  @Bean
  public PoolingContextSource poolingContextSource() {
    LdapContextSource ldapContextSource = new LdapContextSource();
    ldapContextSource.setUrl("ldap://localhost:389");
    ldapContextSource.setBase("");
    ldapContextSource.setUserDn("cn=user,dc=some,dc=ldap,dc=crap");
    ldapContextSource.setPassword("none");
    ldapContextSource.setPooled(false);
    ldapContextSource.afterPropertiesSet();

    PoolingContextSource poolingContextSource = new PoolingContextSource();
    poolingContextSource.setContextSource(ldapContextSource);
    poolingContextSource.setMaxTotal(Runtime.getRuntime().availableProcessors());
    poolingContextSource.setMaxActive(Runtime.getRuntime().availableProcessors());
    poolingContextSource.setTestOnBorrow(true);

    return poolingContextSource;
  }

  @Bean
  public Boolean isDevEnvironment() {
    Boolean result = false;

    String[] activeProfiles = this.env.getActiveProfiles();
    for (String profile : activeProfiles) {
      if (profile.equals(this.DEV_PROFILE_NAME)) {
        result = true;
        break;
      }
    }

    return result;
  }
}