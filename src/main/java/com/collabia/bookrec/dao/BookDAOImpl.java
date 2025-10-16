package com.collabia.bookrec.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BookDAOImpl {
    
    private Map<String, Book> booksMap = new HashMap<>();

    public List<Book> getAllBooks() {
        // Return all books from your data source
        // Example implementation (adjust based on your data storage):
        return new ArrayList<>(booksMap.values());
        // or if using a database:
        // return jdbcTemplate.query("SELECT * FROM books", new BookRowMapper());
    }
}
