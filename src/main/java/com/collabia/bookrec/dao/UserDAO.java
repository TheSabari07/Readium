package com.collabia.bookrec.dao;

import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.collabia.bookrec.db.MongoDBConnection;
import com.collabia.bookrec.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

public class UserDAO {
    private final MongoCollection<Document> usersCollection;

    public UserDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.usersCollection = database.getCollection("users");
    }

    public void createUser(User user) {
        usersCollection.insertOne(user.toDocument());
    }

    public Optional<User> findByEmail(String email) {
        Document doc = usersCollection.find(Filters.eq("email", email)).first();
        return Optional.ofNullable(User.fromDocument(doc));
    }

    public Optional<User> findById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        Document doc = usersCollection.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(User.fromDocument(doc));
    }

    public void update(User user) {
        if (user.getId() == null || !ObjectId.isValid(user.getId())) {
            throw new IllegalArgumentException("User must have a valid ID to be updated.");
        }
        Document filter = new Document("_id", new ObjectId(user.getId()));
        usersCollection.replaceOne(filter, user.toDocument(), new ReplaceOptions().upsert(true));
    }
}
