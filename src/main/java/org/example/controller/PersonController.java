package org.example.controller;

import org.example.repository.PersonRepository;
import org.example.repository.dao.Person;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    ResponseEntity<List<Person>> getAllPersons() {
        return ResponseEntity.ok(personRepository.findAll());
    }


}
