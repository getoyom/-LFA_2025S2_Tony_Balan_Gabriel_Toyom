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
    public void GenerateHTML(BookHashTable libros, ClientHashTable usuarios, ArrayList<Loan> prestamos,
                             Client masFrecuente, Book libroPrestado, ArrayList<Loan> vencidos) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            writeHtmlHeader(escritor);
            writeLoansHistory(escritor, prestamos);
            writeUniqueUsers(escritor, usuarios);
            writeLoanedBooks(escritor, libros);
            writeLoanStatistics(escritor, prestamos, masFrecuente, libroPrestado, usuarios);
            writeOverdueLoans(escritor, vencidos);
            writeHtmlFooter(escritor);
            System.out.println("El archivo HTML generado correctamente...\n");
        } catch (IOException e) {
            System.err.println("Error al generar el archivo HTML: " + e.getMessage());
        }
    }

    // Escribe la cabecera HTML con estilos CSS
    private void writeHtmlHeader(BufferedWriter escritor) throws IOException {
        escritor.write("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n<meta charset=\"UTF-8\">\n");
        escritor.write("<title>Reportes de Biblioteca Digital</title>\n");
        escritor.write(getCssStyles());
        escritor.write("</head>\n<body>\n");
        escritor.write("<h1>Reportes de biblioteca digital</h1>\n");
    }

    // Retorna los estilos CSS para el documento HTML
    private String getCssStyles() {
        return "<style>\n" +
                "body { font-family: Arial; margin: 20px; }\n" +
                "table { width: 100%; border-collapse: collapse; margin-bottom: 40px; }\n" +
                "th, td { border: 1px solid #999; padding: 8px; text-align: center; }\n" +
                "th { background-color: #f2f2f2; }\n" +
                "</style>\n";
    }

    // Escribe la seccion de historial de prestamos
    private void writeLoansHistory(BufferedWriter escritor, ArrayList<Loan> prestamos) throws IOException {
        escritor.write("<h2>Historial de Prestamos</h2>\n");
        escritor.write("<table>\n");
        escritor.write("<tr><th>ID Usuario</th><th>Nombre</th><th>ID Libro</th><th>Titulo</th><th>Fecha Prestamo</th><th>Fecha Devolucion</th></tr>\n");

        for (Loan prestamo : prestamos) {
            String row = String.format("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                    prestamo.getClient().getId(),
                    prestamo.getClient().getName(),
                    prestamo.getBook().getId(),
                    prestamo.getBook().getTitle(),
                    prestamo.getDateLoan(),
                    prestamo.getDateDue() != null ? prestamo.getDateDue() : "No devuelto");
            escritor.write(row);
        }
        escritor.write("</table>\n");
    }


     //Escribe la seccion de usuarios unicos
    private void writeUniqueUsers(BufferedWriter escritor, ClientHashTable usuarios) throws IOException {
        escritor.write("<h2>Listado de Usuarios Unicos</h2>\n");
        escritor.write("<table>\n");
        escritor.write("<tr><th>ID Usuario</th><th>Nombre</th></tr>\n");

        ArrayList<Client> listaUsuarios = usuarios.toClientList();
        for (Client usuario : listaUsuarios) {
            String row = String.format("<tr><td>%d</td><td>%s</td></tr>\n",
                    usuario.getId(), usuario.getName());
            escritor.write(row);
        }
        escritor.write("</table>\n");
    }

    //Escribe la seccion de libros prestados
    private void writeLoanedBooks(BufferedWriter escritor, BookHashTable libros) throws IOException {
        escritor.write("<h2>Listado de libros Prestados</h2>\n");
        escritor.write("<table>\n");
        escritor.write("<tr><th>ID Libro</th><th>Titulo libro</th></tr>\n");

        ArrayList<Book> listaLibros = libros.toBookList();
        for (Book libro : listaLibros) {
            String row = String.format("<tr><td>%s</td><td>%s</td></tr>\n",
                    libro.getId(), libro.getTitle());
            escritor.write(row);
        }
        escritor.write("</table>\n");
    }


     //Escribe la seccion de estadisticas de prestamos
    private void writeLoanStatistics(BufferedWriter escritor, ArrayList<Loan> prestamos, Client masFrecuente,
                                     Book libroPrestado, ClientHashTable usuarios) throws IOException {
        escritor.write("<h2>Estadisticas de Prestamos</h2>\n");
        escritor.write("<table>\n");
        escritor.write("<tr><th>Total Prestamos</th><th>Libro Mas Prestado</th><th>Usuario Mas Activo</th><th>Total Usuarios Unicos</th></tr>\n");

        String row = String.format("<tr><td>%d</td><td>%s</td><td>%s</td><td>%d</td></tr>\n",
                prestamos.size(),
                libroPrestado != null ? libroPrestado.getTitle() : "N/A",
                masFrecuente != null ? masFrecuente.getName() : "N/A",
                usuarios.toClientList().size());
        escritor.write(row);
        escritor.write("</table>\n");
    }

    //Escribe la seccion de prestamos vencidos
    private void writeOverdueLoans(BufferedWriter escritor, ArrayList<Loan> vencidos) throws IOException {
        escritor.write("<h2>Prestamos Vencidos</h2>\n");
        escritor.write("<table>\n");
        escritor.write("<tr><th>ID Usuario</th><th>Nombre</th><th>ID Libro</th><th>Titulo</th><th>Fecha Prestamo</th></tr>\n");

        for (Loan prestamo : vencidos) {
            String row = String.format("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                    prestamo.getClient().getId(),
                    prestamo.getClient().getName(),
                    prestamo.getBook().getId(),
                    prestamo.getBook().getTitle(),
                    prestamo.getDateLoan());
            escritor.write(row);
        }
        escritor.write("</table>\n");
    }

    //Escribe el pie del documento HTML
    private void writeHtmlFooter(BufferedWriter escritor) throws IOException {
        escritor.write("</body>\n</html>");
    }
}