package com.readium;

import java.util.List;

public class User {
    private List<String> readBooks;
    private List<String> favoriteGenres;

    public List<String> getReadBooks() {
        return readBooks;
    }

    public void setReadBooks(List<String> readBooks) {
        this.readBooks = readBooks;
    }

    public List<String> getFavoriteGenres() {
        return favoriteGenres;
    }

    public void setFavoriteGenres(List<String> favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
    }
}
