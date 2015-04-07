package com.jrfom.webapp.dao;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrfom.webapp.models.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class PersonRedisStore implements RedisStore<Person> {
  private static final Logger log = LoggerFactory.getLogger(PersonRedisStore.class);

  @Autowired
  private JedisPool jedisPool;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public void save(String key, Person person) {
    log.debug("Saving person object to Redis with key: `{}`", key);

    try {
      String json = this.objectMapper.writeValueAsString(person);
      try (Jedis jedis = this.jedisPool.getResource()) {
        jedis.set(key, json);
      }
    } catch (JsonProcessingException e) {
      log.error("Could not serialize person object");
      log.debug(e.toString());
    }
  }

  @Override
  public Optional<Person> get(String key) {
    Optional<Person> result = Optional.empty();

    String json;
    try (Jedis jedis = this.jedisPool.getResource()) {
      json = jedis.get(key);
    }

    if (json != null) {
      try {
        Person person = this.objectMapper.readValue(json, Person.class);
        result = Optional.of(person);
      } catch (JsonMappingException e) {
        log.error("Couldn't map to Person object json");
        log.debug(e.toString());
      } catch (JsonParseException e) {
        log.error("Couldn't parse JSON");
        log.debug(e.toString());
      } catch (IOException e) {
        log.debug("Couldn't read JSON");
        log.debug(e.toString());
      }
    }

    return result;
  }
}