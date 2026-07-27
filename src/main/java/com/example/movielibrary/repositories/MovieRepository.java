package com.example.movielibrary.repositories;

import com.example.movielibrary.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Optional<Movie> findByTitle(String title);

    boolean existsByTitle(String title);
}
