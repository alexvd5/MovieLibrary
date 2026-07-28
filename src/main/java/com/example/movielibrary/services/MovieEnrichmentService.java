package com.example.movielibrary.services;

import com.example.movielibrary.models.OmdbResponseDto;
import com.example.movielibrary.repositories.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class MovieEnrichmentService {

    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;

    @Value("${omdb.api.url}")
    private String omdbUrl;

    @Value("${omdb.api.key}")
    private String apiKey;

    public MovieEnrichmentService(RestTemplate restTemplate, MovieRepository movieRepository) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
    }

    @Async
    public void enrichMovieRating(int movieId, String title) {
        try {

            Thread.sleep(50000);

            String url = String.format("%s?t=%s&apikey=%s", omdbUrl, title, apiKey);
            OmdbResponseDto response = restTemplate.getForObject(url, OmdbResponseDto.class);

            if (response != null && "True".equalsIgnoreCase(response.getResponse())) {
                try {
                    Double rating = Double.parseDouble(response.getImdbRating());

                    movieRepository.findById(movieId).ifPresent(movie -> {
                        movie.setRating(rating);
                        movieRepository.save(movie);
                        System.out.println("Successfully updated rating for: " + title + " -> " + rating);
                    });
                } catch (NumberFormatException e) {
                    System.out.println("Cannot parse the rating from OMDb: " + response.getImdbRating());
                }
            } else {
                System.out.println("The movie is not found in OMDb: " + title);
            }
        } catch (Exception e) {
            log.error("Error while trying to get the rating: {}", e.getMessage());
        }
    }
}