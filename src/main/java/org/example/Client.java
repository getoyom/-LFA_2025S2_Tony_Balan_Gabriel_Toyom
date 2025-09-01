package org.example;

public class Client{
    int id_client;
    String name_client;
    Book book_loan;

    public Client(int id_client, String name_client, Book book_loan) {
        this.id_client = id_client;
        this.name_client = name_client;
        this.book_loan = book_loan;
    }
    public String getName() {
        return name_client;
    }
    public void setName(String name_client) {
        this.name_client = name_client;
    }
    public int getId() {
        return id_client;
    }
    public void setId_client(int id_client) {
        this.id_client = id_client;
    }
    public Book getBook_loan() {
        return book_loan;
    }
    public void setBook_loan(Book book_loan) {
        this.book_loan = book_loan;
    }
    @Override
    public String toString() {
        return String.format("Cliente{ID:%d, Nombre:'%s', LibroPrestado:%s}",
                id_client, name_client, book_loan);
    }

    public String toStringTable() {
        return String.format("|ID: %d| Nombre: %s|", id_client, name_client);
    }
}
