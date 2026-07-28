package com.example.movielibrary;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.Director;
import com.example.movielibrary.models.Movie;
import com.example.movielibrary.repositories.MovieRepository;
import com.example.movielibrary.services.MovieEnrichmentService;
import com.example.movielibrary.services.MovieService;
import com.example.movielibrary.services.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieEnrichmentService movieEnrichmentService;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie testMovie;
    private Director testDirector;

    @BeforeEach
    void setUp() {
        testDirector = new Director();
        testDirector.setId(1);
        testDirector.setName("Christopher Nolan");

        testMovie = new Movie();
        testMovie.setId(1);
        testMovie.setTitle("Inception");
        testMovie.setDirector(testDirector);
        testMovie.setReleaseYear(2010);
    }

    @Test
    @DisplayName("Successfully added movie when title is unique")
    void createMovie_Success() {
        when(movieRepository.existsByTitle("Inception")).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        Movie created = movieService.createMovie(testMovie);

        assertNotNull(created);
        assertEquals("Inception", created.getTitle());

        verify(movieRepository, times(1)).save(testMovie);
        verify(movieEnrichmentService, times(1)).enrichMovieRating(1, "Inception");
    }

    @Test
    @DisplayName("Throws exception when there is already a movie with the same title")
    void createMovie_DuplicateTitle_ThrowsException() {
        when(movieRepository.existsByTitle("Inception")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> {
            movieService.createMovie(testMovie);
        });

        verify(movieRepository, never()).save(any());
        verify(movieEnrichmentService, never()).enrichMovieRating(anyInt(), anyString());
    }

    @Test
    @DisplayName("Successfully found movie by id")
    void getMovieById_Success() {
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));

        Movie found = movieService.getMovieById(1);

        assertNotNull(found);
        assertEquals(1,found.getId());
    }

    @Test
    @DisplayName("Throws exception when movie with given id is not found")
    void getMovieById_NotFound_ThrowsException(){
        when(movieRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            movieService.getMovieById(1);
        });
    }

    @Test
    @DisplayName("Successfully found movie by title")
    void getMovieByTitle_Success() {
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.of(testMovie));

        Movie found = movieService.getMovieByTitle("Inception");

        assertNotNull(found);
        assertEquals("Inception",found.getTitle());
    }

    @Test
    @DisplayName("Throws exception when movie with given title is not found")
    void getMovieByTitle_NotFound_ThrowsException(){
        when(movieRepository.findByTitle("FakeMovie")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            movieService.getMovieByTitle("FakeMovie");
        });
    }


}
