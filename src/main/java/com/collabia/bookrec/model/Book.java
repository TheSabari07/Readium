package com.collabia.bookrec.model;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Book {
    private String id;
    private String title;
    private String author;
    private List<String> genres;
    private String description;
    private String coverPath;
    private double averageRating;

    public Book() {
    }

    public Book(String title, String author, List<String> genres, String description, String coverPath) {
        this.title = title;
        this.author = author;
        this.genres = genres;
        this.description = description;
        this.coverPath = coverPath;
        this.averageRating = 0.0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // MongoDB Document Conversion
    public Document toDocument() {
        Document doc = new Document("title", title)
                .append("author", author)
                .append("genres", genres)
                .append("description", description)
                .append("coverPath", coverPath)
                .append("averageRating", averageRating);
        if (id != null) {
            doc.append("_id", new ObjectId(id));
        }
        return doc;
    }

    public static Book fromDocument(Document doc) {
        Book book = new Book();
        book.setId(doc.getObjectId("_id").toHexString());
        book.setTitle(doc.getString("title"));
        book.setAuthor(doc.getString("author"));
        book.setGenres(doc.getList("genres", String.class));
        book.setDescription(doc.getString("description"));
        book.setCoverPath(doc.getString("coverPath"));
        book.setAverageRating(doc.getDouble("averageRating"));
        return book;
    }
}
