package com.mythcon.savr.movieku.Model;

public class Movie {
    private String name;
    private String description;
    private String rate;
    private String genre;

    public Movie() {
    }

    public Movie(String name, String description, String rate, String genre) {
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
