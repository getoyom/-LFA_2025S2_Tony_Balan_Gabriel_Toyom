package org.example;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HtmlGenerator {
    //Constante para guardar los reportes
    private static final String OUTPUT_FILE = "Reportes.html";
    public HtmlGenerator() {}

    // Genera un archivo HTML completo con todos los reportes de la biblioteca
    public void GenerateHTML(BookHashTable books, ClientHashTable users, ArrayList<Loan> loans,
                             Client mostFrequent, Book mostLoanedBook, ArrayList<Loan> overdue) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            writeHtmlHeader(escritor);
            writeLoansHistory(escritor, loans);
            writeUniqueUsers(escritor, users);
            writeLoanedBooks(escritor, books);
            writeLoanStatistics(escritor, loans, mostFrequent, mostLoanedBook, users);
            writeOverdueLoans(escritor, overdue);
            writeHtmlFooter(escritor);
            System.out.println("El archivo HTML generado correctamente...\n");
        } catch (IOException e) {
            System.err.printf("Error al generar el archivo HTML: %s ", e.getMessage());
        }
    }

    // Escribe la cabecera HTML con estilos CSS
    private void writeHtmlHeader(BufferedWriter writer) throws IOException {
        writer.write("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n<meta charset=\"UTF-8\">\n");
        writer.write("<title>Reportes de Biblioteca Digital</title>\n");
        writer.write(getCssStyles());
        writer.write("</head>\n<body>\n");
        writer.write("<h1>Reportes de biblioteca digital</h1>\n");
    }

    // Retorna los estilos CSS para el documento HTML
    private String getCssStyles() {
        return """
                <style>
                body { font-family: Arial; margin: 20px; }
                table { width: 100%; border-collapse: collapse; margin-bottom: 40px; }
                th, td { border: 1px solid #999; padding: 8px; text-align: center; }
                th { background-color: #f2f2f2; }
                </style>
                """;
    }

    // Escribe la seccion de historial de prestamos
    private void writeLoansHistory(BufferedWriter writer, ArrayList<Loan> loans) throws IOException {
        writer.write("<h2>Historial de Prestamos</h2>\n");
        writer.write("<table>\n");
        writer.write("<tr><th>ID Usuario</th><th>Nombre</th><th>ID Libro</th><th>Titulo</th><th>Fecha Prestamo</th><th>Fecha Devolucion</th></tr>\n");

        for (Loan loan : loans) {
            String row = String.format("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                    loan.getClient().getId(),
                    loan.getClient().getName(),
                    loan.getBook().getId(),
                    loan.getBook().getTitle(),
                    loan.getDateLoan(),
                    loan.getDateDue() != null ? loan.getDateDue() : "No devuelto");
            writer.write(row);
        }
        writer.write("</table>\n");
    }


     //Escribe la seccion de usuarios unicos
    private void writeUniqueUsers(BufferedWriter writer, ClientHashTable users) throws IOException {
        writer.write("<h2>Listado de Usuarios Unicos</h2>\n");
        writer.write("<table>\n");
        writer.write("<tr><th>ID Usuario</th><th>Nombre</th></tr>\n");

        ArrayList<Client> usersList = users.toClientList();
        for (Client client : usersList) {
            String row = String.format("<tr><td>%d</td><td>%s</td></tr>\n",
                    client.getId(), client.getName());
            writer.write(row);
        }
        writer.write("</table>\n");
    }

    //Escribe la seccion de libros prestados
    private void writeLoanedBooks(BufferedWriter writer, BookHashTable books) throws IOException {
        writer.write("<h2>Listado de libros Prestados</h2>\n");
        writer.write("<table>\n");
        writer.write("<tr><th>ID Libro</th><th>Titulo libro</th></tr>\n");

        ArrayList<Book> bookList = books.toBookList();
        for (Book book : bookList) {
            String row = String.format("<tr><td>%s</td><td>%s</td></tr>\n",
                    book.getId(), book.getTitle());
            writer.write(row);
        }
        writer.write("</table>\n");
    }


     //Escribe la seccion de estadisticas de prestamos
    private void writeLoanStatistics(BufferedWriter writer, ArrayList<Loan> loans, Client mostFrequent,
                                     Book mostLoanedBook, ClientHashTable users) throws IOException {
        writer.write("<h2>Estadisticas de Prestamos</h2>\n");
        writer.write("<table>\n");
        writer.write("<tr><th>Total Prestamos</th><th>Libro Mas Prestado</th><th>Usuario Mas Activo</th><th>Total Usuarios Unicos</th></tr>\n");

        String row = String.format("<tr><td>%d</td><td>%s</td><td>%s</td><td>%d</td></tr>\n",
                loans.size(),
                mostLoanedBook != null ? mostLoanedBook.getTitle() : "N/A",
                mostFrequent != null ? mostFrequent.getName() : "N/A",
                users.toClientList().size());
        writer.write(row);
        writer.write("</table>\n");
    }

    //Escribe la seccion de prestamos vencidos
    private void writeOverdueLoans(BufferedWriter writer, ArrayList<Loan> overdue) throws IOException {
        writer.write("<h2>Prestamos Vencidos</h2>\n");
        writer.write("<table>\n");
        writer.write("<tr><th>ID Usuario</th><th>Nombre</th><th>ID Libro</th><th>Titulo</th><th>Fecha Prestamo</th></tr>\n");

        for (Loan loan : overdue) {
            String row = String.format("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                    loan.getClient().getId(),
                    loan.getClient().getName(),
                    loan.getBook().getId(),
                    loan.getBook().getTitle(),
                    loan.getDateLoan());
            writer.write(row);
        }
        writer.write("</table>\n");
    }

    //Escribe el pie del documento HTML
    private void writeHtmlFooter(BufferedWriter writer) throws IOException {
        writer.write("</body>\n</html>");
    }
}