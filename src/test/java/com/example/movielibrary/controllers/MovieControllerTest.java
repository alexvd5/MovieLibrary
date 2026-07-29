package com.example.movielibrary.controllers;

import com.example.movielibrary.models.Movie;
import com.example.movielibrary.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieService movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1);
        testMovie.setTitle("Inception");
        testMovie.setReleaseYear(2010);
    }

    @Test
    @DisplayName("GET /api/movies - Returns 200 OK and List of movies")
    @WithMockUser
    void getAllMovies_AsUser_Success() throws Exception {
        when(movieService.getAllMovies()).thenReturn(List.of(testMovie));

        mockMvc.perform(get("/api/movies")
                        .with(user("user").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    @DisplayName("GET /api/movies - Returns 200 OK and List of movies")
    @WithMockUser
    void getAllMovies_AsAdmin_Success() throws Exception {
        when(movieService.getAllMovies()).thenReturn(List.of(testMovie));

        mockMvc.perform(get("/api/movies")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    @DisplayName("GET /api/movies/1 - Returns 200 OK for USER role")
    void getMovieById_AsUser_Success() throws Exception {
        when(movieService.getMovieById(1)).thenReturn(testMovie);

        mockMvc.perform(get("/api/movies/1")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    @DisplayName("GET /api/movies/1 - Returns 200 OK for ADMIN role")
    void getMovieById_AsAdmin_Success() throws Exception {
        when(movieService.getMovieById(1)).thenReturn(testMovie);

        mockMvc.perform(get("/api/movies/1")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    @DisplayName("POST /api/movies - Creates new movie and returns status 201 Created")
    @WithMockUser
    void createMovie_Success() throws Exception {
        when(movieService.createMovie(any(Movie.class))).thenReturn(testMovie);

        mockMvc.perform(post("/api/movies")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    @DisplayName("PUT /api/movies/1 - Updates the movie and returns status code 200 OK")
    @WithMockUser
    void updateMovie_Success() throws Exception {
        Movie updatedMovie = new Movie();
        updatedMovie.setId(1);
        updatedMovie.setTitle("Inception Updated");

        when(movieService.updateMovie(eq(1), any(Movie.class))).thenReturn(updatedMovie);

        mockMvc.perform(put("/api/movies/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception Updated"));
    }

    @Test
    @DisplayName("DELETE /api/movies/1 - Deletes the movie and returns status code 204 No Content")
    @WithMockUser
    void deleteMovie_Success() throws Exception {
        doNothing().when(movieService).deleteMovie(1);

        mockMvc.perform(delete("/api/movies/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/movies - Returns status code 400 Bad Request")
    @WithMockUser
    void createMovie_InvalidTitle_ReturnsBadRequest() throws Exception {
        Movie invalidMovie = new Movie();
        invalidMovie.setTitle("");

        when(movieService.createMovie(any(Movie.class)))
                .thenThrow(new IllegalArgumentException("Movie title cannot be empty!"));

        mockMvc.perform(post("/api/movies")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

}
