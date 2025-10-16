package com.collabia.bookrec.model;

import java.time.LocalDate;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Rating {
    private String id;
    private String userId;
    private String bookId;
    private int rating; // e.g., 1-5
    private String reviewText;
    private LocalDate date;

    public Rating() {
    }

    public Rating(String userId, String bookId, int rating) {
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // MongoDB Document Conversion
    public Document toDocument() {
        Document doc = new Document("userId", new ObjectId(userId))
                .append("bookId", new ObjectId(bookId))
                .append("rating", rating);
        if (id != null) {
            doc.append("_id", new ObjectId(id));
        }
        return doc;
    }

    public static Rating fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }
        Rating rating = new Rating();
        rating.setId(doc.getObjectId("_id").toHexString());
        rating.setUserId(doc.getObjectId("userId").toHexString());
        rating.setBookId(doc.getObjectId("bookId").toHexString());
        rating.setRating(doc.getInteger("rating"));
        return rating;
    }
}
