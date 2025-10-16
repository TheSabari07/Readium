package com.collabia.bookrec.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.collabia.bookrec.db.MongoDBConnection;
import com.collabia.bookrec.model.Rating;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

public class RatingDAO {
    private final MongoDatabase database;
    private final MongoCollection<Document> ratingsCollection;

    public RatingDAO() {
        this.database = MongoDBConnection.getDatabase();
        this.ratingsCollection = database.getCollection("ratings");
    }

    public void addRating(Rating rating) {
        ratingsCollection.insertOne(rating.toDocument());
    }

    public List<Rating> findByUserId(String userId) {
        if (!ObjectId.isValid(userId)) {
            return new ArrayList<>();
        }
        return findRatingsByFilter(Filters.eq("userId", new ObjectId(userId)));
    }

    public List<Rating> findByBookId(String bookId) {
        if (!ObjectId.isValid(bookId)) {
            return new ArrayList<>();
        }
        return findRatingsByFilter(Filters.eq("bookId", new ObjectId(bookId)));
    }

    public List<Rating> getRatingsByUserId(String userId) {
        List<Rating> ratings = new ArrayList<>();
        
        if (userId == null || userId.isEmpty()) {
            return ratings;
        }
        
        try {
            if (!ObjectId.isValid(userId)) {
                return ratings;
            }
            
            Document filter = new Document("userId", new ObjectId(userId));
            
            try (MongoCursor<Document> cursor = ratingsCollection.find(filter).iterator()) {
                while (cursor.hasNext()) {
                    Rating rating = Rating.fromDocument(cursor.next());
                    if (rating != null) {
                        ratings.add(rating);
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ratings;
    }

    private List<Rating> findRatingsByFilter(Bson filter) {
        List<Rating> ratings = new ArrayList<>();
        try (MongoCursor<Document> cursor = ratingsCollection.find(filter).iterator()) {
            while (cursor.hasNext()) {
                ratings.add(Rating.fromDocument(cursor.next()));
            }
        }
        return ratings;
    }

    public double calculateAverageRating(String bookId) {
        if (!ObjectId.isValid(bookId)) {
            return 0.0;
        }
        Document result = ratingsCollection.aggregate(Arrays.asList(
                Aggregates.match(Filters.eq("bookId", new ObjectId(bookId))),
                Aggregates.group("$bookId", Accumulators.avg("averageRating", "$rating"))
        )).first();

        return result != null ? result.getDouble("averageRating") : 0.0;
    }
}
