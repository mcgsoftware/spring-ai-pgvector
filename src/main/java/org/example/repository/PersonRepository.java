package org.example.repository;

import org.example.repository.dao.Person;

import java.util.List;

public interface PersonRepository {
    List<Person> findAll();
}
