package org.example.repository.impl;

import org.example.repository.PersonRepository;
import org.example.repository.dao.Person;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonRepositoryImpl implements PersonRepository {
    private final JdbcTemplate jdbcTemplate;

    public PersonRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Person> findAll() {
        final String sql = "SELECT id, name, age FROM person";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            var id = rs.getLong("id");
            var name = rs.getString("name");
            var age = rs.getInt("age");
            return new Person(id, name, age);
        });
    }
}
