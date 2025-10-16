package com.collabia.bookrec.model;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User {
    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private List<String> favoriteGenres;
    private List<String> readBooks;
    private List<String> likedBooks;
    private Date createdAt;

    public User() {
    }

    public User(String name, String email, String passwordHash, List<String> favoriteGenres, List<String> readBooks, List<String> likedBooks) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.favoriteGenres = favoriteGenres;
        this.readBooks = readBooks;
        this.likedBooks = likedBooks;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
                .append("passwordHash", passwordHash)
                .append("favoriteGenres", favoriteGenres)
                .append("readBooks", readBooks)
                .append("likedBooks", likedBooks)
                .append("createdAt", createdAt);
        if (id != null) {
            doc.append("_id", new ObjectId(id));
        }
        return doc;
    }

    public static User fromDocument(Document doc) {
        User user = new User();
        user.setId(doc.getObjectId("_id").toHexString());
        user.setName(doc.getString("name"));
        user.setEmail(doc.getString("email"));
        user.setPasswordHash(doc.getString("passwordHash"));
        user.setFavoriteGenres(doc.getList("favoriteGenres", String.class));
        user.setReadBooks(doc.getList("readBooks", String.class));
        user.setLikedBooks(doc.getList("likedBooks", String.class));
        user.setCreatedAt(doc.getDate("createdAt"));
        return user;
    }
}
