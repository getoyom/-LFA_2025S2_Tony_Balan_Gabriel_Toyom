package org.example;

public class Book{
    String id_book;
    String title;

    public Book(String id_book, String title) {
        this.id_book = id_book;
        this.title = title;
    }
    public String getId() {
        return id_book;
    }
    public void setId(String id_book) {
        this.id_book = id_book;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return String.format("|ID: %s| Titulo: %s|", id_book, title);
    }

}
