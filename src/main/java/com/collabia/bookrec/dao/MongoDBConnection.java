package com.collabia.bookrec.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    private static final Logger LOGGER = Logger.getLogger(MongoDBConnection.class.getName());
    private static final String DEFAULT_MONGO_URI = "mongodb://localhost:27017/bookrec";

    private static MongoDBConnection instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoDBConnection() {
        String mongoUri = System.getenv("BOOKREC_MONGO_URI");
        if (mongoUri == null || mongoUri.isEmpty()) {
            mongoUri = DEFAULT_MONGO_URI;
            LOGGER.info("Using default MongoDB URI: " + mongoUri);
        } else {
            LOGGER.info("Using MongoDB URI from environment variable BOOKREC_MONGO_URI.");
        }

        MongoClient client = null;
        MongoDatabase db = null;
        try {
            ConnectionString connectionString = new ConnectionString(mongoUri);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();
            client = MongoClients.create(settings);
            String dbName = connectionString.getDatabase();
            if (dbName == null) {
                throw new MongoException("Database name not found in MongoDB URI.");
            }
            db = client.getDatabase(dbName);
            LOGGER.info("Successfully connected to MongoDB database: " + dbName);
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to MongoDB", e);
            if (client != null) {
                client.close();
            }
            throw new IllegalStateException("Failed to initialize MongoDB connection", e);
        }
        this.mongoClient = client;
        this.database = db;
    }

    public static synchronized MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            LOGGER.info("MongoDB connection closed.");
        }
    }
}
