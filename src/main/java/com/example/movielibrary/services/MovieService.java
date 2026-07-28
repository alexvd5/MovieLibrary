package com.example.movielibrary.services;

import com.example.movielibrary.models.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();

    Movie getMovieById(int id);

    Movie getMovieByTitle(String title);

    Movie createMovie(Movie movie);

    Movie updateMovie(int id, Movie movieDetails);

    void deleteMovie(int id);
}
