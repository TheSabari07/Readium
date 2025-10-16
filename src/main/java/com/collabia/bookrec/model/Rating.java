package com.collabia.bookrec.model;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Rating {
    private String id;
    private String userId;
    private String bookId;
    private int rating; // 1-5
    private String review;
    private Date createdAt;

    public Rating() {
    }

    public Rating(String userId, String bookId, int rating, String review) {
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.review = review;
        this.createdAt = new Date();
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

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // MongoDB Document Conversion
    public Document toDocument() {
        Document doc = new Document("userId", userId)
                .append("bookId", bookId)
                .append("rating", rating)
                .append("review", review)
                .append("createdAt", createdAt);
        if (id != null) {
            doc.append("_id", new ObjectId(id));
        }
        return doc;
    }

    public static Rating fromDocument(Document doc) {
        Rating rating = new Rating();
        rating.setId(doc.getObjectId("_id").toHexString());
        rating.setUserId(doc.getString("userId"));
        rating.setBookId(doc.getString("bookId"));
        rating.setRating(doc.getInteger("rating"));
        rating.setReview(doc.getString("review"));
        rating.setCreatedAt(doc.getDate("createdAt"));
        return rating;
    }
}
