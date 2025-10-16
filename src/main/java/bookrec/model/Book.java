package bookrec.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private String description;
    private String coverUrl;
    private double averageRating;
    
    public Book() {}
    
    public Book(int id, String title, String author, String genre, String isbn, String description, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.description = description;
        this.coverUrl = coverUrl;
        this.averageRating = 0.0;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}
