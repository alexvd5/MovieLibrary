package com.example.movielibrary.services;

import com.example.movielibrary.models.Movie;
import com.example.movielibrary.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieById(int id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    @Override
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(int id, Movie movieDetails) {
        Movie movie = getMovieById(id);

        movie.setTitle(movieDetails.getTitle());
        movie.setDirector(movieDetails.getDirector());
        movie.setReleaseYear(movieDetails.getReleaseYear());

        return movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(int id) {
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }

}
