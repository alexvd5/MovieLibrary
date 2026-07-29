package com.example.movielibrary.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OmdbResponseDto {
    @JsonProperty("imdbRating")
    private String imdbRating;

    @JsonProperty("Response")
    private String response;

}
