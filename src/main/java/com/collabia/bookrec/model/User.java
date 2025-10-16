package com.collabia.bookrec.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String name;
    private String email;
    private String password; // Should be hashed
    private List<String> favoriteGenres;
    private List<String> readBooks;
    private List<String> likedBooks;
    private Date createdAt;

    public User() {
        this.favoriteGenres = new ArrayList<>();
        this.readBooks = new ArrayList<>();
        this.likedBooks = new ArrayList<>();
        this.createdAt = new Date();
    }

    public User(String name, String email, String password) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, List<String> favoriteGenres, List<String> readBooks, List<String> likedBooks) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.favoriteGenres = favoriteGenres;
        this.readBooks = readBooks;
        this.likedBooks = likedBooks;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFavoriteGenres() {
        return favoriteGenres;
    }

    public void setFavoriteGenres(List<String> favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
    }

    public List<String> getReadBooks() {
        return readBooks;
    }

    public void setReadBooks(List<String> readBooks) {
        this.readBooks = readBooks;
    }

    public List<String> getLikedBooks() {
        return likedBooks;
    }

    public void setLikedBooks(List<String> likedBooks) {
        this.likedBooks = likedBooks;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // MongoDB Document Conversion
    public Document toDocument() {
        Document doc = new Document("name", name)
                .append("email", email)
                .append("password", password)
                .append("favoriteGenres", favoriteGenres)
                .append("readBooks", readBooks)
                .append("likedBooks", likedBooks)
                .append("createdAt", createdAt);
        if (id != null) {
            doc.append("_id", id);
        }
        return doc;
    }

    public static User fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }
        User user = new User();
        user.setId(doc.getObjectId("_id"));
        user.setName(doc.getString("name"));
        user.setEmail(doc.getString("email"));
        user.setPassword(doc.getString("password"));
        user.setFavoriteGenres(doc.getList("favoriteGenres", String.class, new ArrayList<>()));
        user.setReadBooks(doc.getList("readBooks", String.class, new ArrayList<>()));
        user.setLikedBooks(doc.getList("likedBooks", String.class, new ArrayList<>()));
        user.setCreatedAt(doc.getDate("createdAt"));
        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
