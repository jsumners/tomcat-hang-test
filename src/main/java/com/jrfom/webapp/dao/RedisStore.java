package com.jrfom.webapp.dao;

import java.util.Optional;

public interface RedisStore<T> {
  void save(String key, T object);
  Optional<T> get(String key);
}
