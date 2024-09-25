package org.example.repository.dao;


//import jakarta.persistence.*;

//@Entity
//@Table(name = "Person")
public record Person(
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,

//        @Column(nullable = false, length = 100)
        String name,

//        @Column(nullable = false)
        int age
) {
    // Additional constructors if needed
    public Person(String name, int age) {
        this(null, name, age);
    }
}