package com.readium;

import java.util.List;

public class BookService {
    private BookDAO bookDAO = new BookDAO();
    
    public List<Book> getAllBooks() {
        return bookDAO.getAll();
    }
}
