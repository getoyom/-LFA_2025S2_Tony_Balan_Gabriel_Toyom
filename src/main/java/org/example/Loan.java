package org.example;
import java.time.LocalDate;

public class Loan {
    Book book;
    Client client;
    LocalDate dateLoan;
    LocalDate dateDue;

    public Loan(Client client, Book book, LocalDate dateDue, LocalDate dateLoan) {
        this.dateDue = dateDue;
        this.dateLoan = dateLoan;
        this.client = client;
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getDateLoan() {
        return dateLoan;
    }

    public void setDateLoan(LocalDate dateLoan) {
        this.dateLoan = dateLoan;
    }

    public LocalDate getDateDue() {
        return dateDue;
    }

    public void setDateDue(LocalDate dateDue) {
        this.dateDue = dateDue;
    }

    @Override
    public String toString() {
        return "Prestamo{" + "Libro: " + book + ", Cliente: " + client + ", Fecha de prestamo: " + dateLoan + ", Fecha de devolucion: " + dateDue + '}';
    }
}
