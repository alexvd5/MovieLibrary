package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.Movie;
import com.example.movielibrary.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieEnrichmentService movieEnrichmentService;

    public MovieServiceImpl(MovieRepository movieRepository, MovieEnrichmentService movieEnrichmentService) {
        this.movieRepository = movieRepository;
        this.movieEnrichmentService = movieEnrichmentService;
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieById(int id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + id));
    }

    @Override
    public Movie getMovieByTitle(String title) {
        return movieRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with title: " + title));
    }

    @Override
    public Movie createMovie(Movie movie) {

        if (movie.getTitle() == null || movie.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty!");
        }

        if (movieRepository.existsByTitle(movie.getTitle())) {
            throw new DuplicateEntityException("Movie with that title '" + movie.getTitle() + "' already exists!");
        }

        Movie savedMovie = movieRepository.save(movie);

        movieEnrichmentService.enrichMovieRating(savedMovie.getId(), savedMovie.getTitle());

        return savedMovie;
    }

    @Override
    public Movie updateMovie(int id, Movie movieDetails) {
        Movie movie = getMovieById(id);

        boolean isTitleChanged = !movie.getTitle().equalsIgnoreCase(movieDetails.getTitle());

        if (isTitleChanged && movieRepository.existsByTitle(movieDetails.getTitle())) {
            throw new DuplicateEntityException("Movie with that title already exists: " + movieDetails.getTitle());
        }

        movie.setTitle(movieDetails.getTitle());
        movie.setDirector(movieDetails.getDirector());
        movie.setReleaseYear(movieDetails.getReleaseYear());

        if (isTitleChanged) {
            movieEnrichmentService.enrichMovieRating(movie.getId(), movie.getTitle());
        }

        return movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(int id) {
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }

}
