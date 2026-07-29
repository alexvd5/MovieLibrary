package com.example.movielibrary.services;

import com.example.movielibrary.models.Movie;
import com.example.movielibrary.models.OmdbResponseDto;
import com.example.movielibrary.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieEnrichmentServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieEnrichmentService enrichmentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(enrichmentService, "omdbUrl", "http://www.omdbapi.com/");
        ReflectionTestUtils.setField(enrichmentService, "apiKey", "testkey");
    }

    @Test
    @DisplayName("Successfully updated rating when OMDb returns valid rating")
    void enrichMovieRating_Success() {

        OmdbResponseDto mockResponse = new OmdbResponseDto();
        mockResponse.setResponse("True");
        mockResponse.setImdbRating("8.8");

        Movie movie = new Movie();
        movie.setId(1);
        movie.setTitle("Inception");

        when(restTemplate.getForObject(anyString(), eq(OmdbResponseDto.class))).thenReturn(mockResponse);
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));

        enrichmentService.enrichMovieRating(1, "Inception");

        verify(movieRepository, times(1)).save(argThat(savedMovie ->
                savedMovie.getRating() != null && savedMovie.getRating() == 8.8
        ));
    }

    @Test
    @DisplayName("Does not update rating when movie is not found in OMDb")
    void enrichMovieRating_NotFoundInOmdb() {
        OmdbResponseDto mockResponse = new OmdbResponseDto();
        mockResponse.setResponse("False");

        when(restTemplate.getForObject(anyString(), eq(OmdbResponseDto.class))).thenReturn(mockResponse);

        enrichmentService.enrichMovieRating(1, "UnknownMovie");

        verify(movieRepository, never()).save(any());
    }
}