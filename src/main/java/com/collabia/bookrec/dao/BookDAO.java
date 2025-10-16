package com.collabia.bookrec.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.collabia.bookrec.db.MongoDBConnection;
import com.collabia.bookrec.model.Book;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class BookDAO {
    private final MongoCollection<Document> booksCollection;

    public BookDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.booksCollection = database.getCollection("books");
    }

    public void insert(Book book) {
        booksCollection.insertOne(book.toDocument());
    }

    public Optional<Book> findById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        Document doc = booksCollection.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(Book.fromDocument(doc));
    }

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (MongoCursor<Document> cursor = booksCollection.find().iterator()) {
            while (cursor.hasNext()) {
                books.add(Book.fromDocument(cursor.next()));
            }
        }
        return books;
    }

    public List<Book> findByGenres(List<String> genres) {
        List<Book> books = new ArrayList<>();
        Bson filter = Filters.in("genres", genres);
        try (MongoCursor<Document> cursor = booksCollection.find(filter).iterator()) {
            while (cursor.hasNext()) {
                books.add(Book.fromDocument(cursor.next()));
            }
        }
        return books;
    }

    public List<Book> searchByTitleOrAuthor(String query) {
        List<Book> books = new ArrayList<>();
        Pattern regex = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        Bson filter = Filters.or(Filters.regex("title", regex), Filters.regex("author", regex));
        try (MongoCursor<Document> cursor = booksCollection.find(filter).iterator()) {
            while (cursor.hasNext()) {
                books.add(Book.fromDocument(cursor.next()));
            }
        }
        return books;
    }

    public Book getBookById(String bookId) {
        if (bookId == null || bookId.isEmpty()) {
            return null;
        }
        
        if (!ObjectId.isValid(bookId)) {
            return null;
        }
        
        try {
            Document filter = new Document("_id", new ObjectId(bookId));
            Document bookDoc = booksCollection.find(filter).first();
            
            return bookDoc != null ? Book.fromDocument(bookDoc) : null;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
