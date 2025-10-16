package com.readium;

import java.util.List;

public class Book {
    private String id;
    private String title;
    private String author;
    private List<String> genres;
    private double rating;

    // Add this constructor
    public Book(String id, String title, String author, List<String> genres, double rating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getGenres() {
        return genres;
    }

    public double getRating() {
        return rating;
    }
}
