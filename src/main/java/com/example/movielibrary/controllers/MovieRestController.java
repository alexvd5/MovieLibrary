package com.example.movielibrary.controllers;

import com.example.movielibrary.models.Movie;
import com.example.movielibrary.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movie Controller", description = "Endpoints for managing the movie library")
public class MovieRestController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "Create a new movie", description = "Adds a new movie to the database and triggers background enrichment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return new ResponseEntity<>(createdMovie, HttpStatus.CREATED);
    }


    @GetMapping
    @Operation(summary = "Get all movies", description = "Retrieves a list of all movies in the library")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID", description = "Returns details of a single movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie found"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Movie> getMovieById(@PathVariable int id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update movie", description = "Updates details of an existing movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Movie> updateMovie(@PathVariable int id, @Valid @RequestBody Movie movieDetails) {
        Movie updatedMovie = movieService.updateMovie(id, movieDetails);
        return ResponseEntity.ok(updatedMovie);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete movie", description = "Deletes a movie from the library by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Void> deleteMovie(@PathVariable int id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }


}
