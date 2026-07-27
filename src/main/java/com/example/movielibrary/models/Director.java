package com.example.movielibrary.models;

import jakarta.persistence.*;

@Entity
@Table(name = "directors")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private int id;

    @Column(name = "director_name")
    private String name;

    public Director() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
