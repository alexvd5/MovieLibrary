package com.example.movielibrary.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OmdbResponseDto {
    @JsonProperty("imdbRating")
    private String imdbRating;

    @JsonProperty("Response")
    private String response;

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
