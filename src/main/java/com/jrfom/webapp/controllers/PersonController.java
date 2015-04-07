package com.jrfom.webapp.controllers;

import java.util.Optional;

import com.jrfom.webapp.dao.RedisStore;
import com.jrfom.webapp.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class PersonController {
  @Autowired
  private RedisStore<Person> personRedisStore;

  @RequestMapping("/save")
  public String save() {
    Person person = new Person();
    person.setName("John Doe");
    person.setEmail("john.doe@example.com");

    this.personRedisStore.save("jd", person);
    return "Saved person to Redis";
  }

  @RequestMapping("/get")
  public Person get() {
    Optional<Person> personOptional = this.personRedisStore.get("jd");
    Person result;

    if (personOptional.isPresent()) {
      result = personOptional.get();
    } else {
      result = new Person();
      result.setName("Error");
      result.setEmail("Couldn't find person");
    }

    return result;
  }
}