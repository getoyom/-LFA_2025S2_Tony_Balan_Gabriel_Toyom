package org.example;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class htmlGenerator {
    ///Constructor
    public htmlGenerator(){}

    //Generar HTML
    public void GenerateHTML(BookHashTable libros, ClientHashTable usuarios, ArrayList<Loan> prestamos, Client masFrecuente, Book libroPrestado, ArrayList<Loan> vencidos){

        try(BufferedWriter escritor = new BufferedWriter(new FileWriter("Reportes.html"))){

            ///Escribir todo el HTML desde cero

            escritor.write("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n<meta charset=\"UTF-8\">\n");
            escritor.write("<title>Reportes de Biblioteca Digital</title>\n"); //Nombre de la pagina
            //escritor.write("</head>\n<body>\n");
            escritor.write("<style>\n" +
                    "body { font-family: Arial; margin: 20px; }\n" +
                    "table { width: 100%; border-collapse: collapse; margin-bottom: 40px; }\n" +
                    "th, td { border: 1px solid #999; padding: 8px; text-align: center; }\n" +
                    "th { background-color: #f2f2f2; }\n" +
                    "</style>\n</head>\n<body>\n");
            escritor.write("<h1>Reportes de biblioteca digital</h1>\n");

            ///Historial de préstamos
            escritor.write("<h2>Historial de Prestamos</h2>\n");
            escritor.write("<table>\n");
            escritor.write("<tr><th>ID Usuario</th><th>Nombre</th><th>ID Libro</th><th>Titulo</th><th>Fecha Prestamo</th><th>Fecha Devolucion</th></tr>");
            for(Loan prestamo : prestamos){
                escritor.write("<tr>");
                escritor.write("<td>" + prestamo.getClient().getId() + "</td>");
                escritor.write("<td>" + prestamo.getClient().getName() + "</td>");
                escritor.write("<td>" + prestamo.getBook().getId() + "</td>");
                escritor.write("<td>" + prestamo.getBook().getTitle() + "</td>");
                escritor.write("<td>" + prestamo.getDateLoan() + "</td>");
                escritor.write("<td>" + (prestamo.getDateDue() != null ? prestamo.getDateDue() : "No devuelto") + "</td>");
                escritor.write("</tr>\n");
            }
            escritor.write("</table>\n");

            ///Usuarios únicos
            escritor.write("<h2>Listado de Usuarios Unicos</h2>\n");
            escritor.write("<table>\n");
            escritor.write("<tr><th>ID Usuario</th><th>Nombre</th></tr>\n");

            ArrayList<Client> Usuarios = usuarios.toClientList();

            for(Client usuario : Usuarios){
                escritor.write("<tr>");
                escritor.write("<td>" + usuario.getId() + "</td>");
                escritor.write("<td>" + usuario.getName() + "</td>");
                escritor.write("</tr>\n");
            }
            escritor.write("</table>\n");

            //Libros prestados
            escritor.write("<h2>Listado de libros Prestados</h2>\n");
            escritor.write("<table>\n");
            escritor.write("<tr><th>ID Libro</th><th>Titulo libro</th></tr>\n");

            ArrayList<Book> Libros = libros.toBookList();

            for(Book libro : Libros){
                escritor.write("<tr>");
                escritor.write("<td>" + libro.getId() + "</td>");
                escritor.write("<td>" + libro.getTitle() + "</td>");
                escritor.write("</tr>\n");
            }
            escritor.write("</table>\n");

            //Estadísticas perronas
            escritor.write("<h2>Estadisticas de Prestamos</h2>\n");
            escritor.write("<table>\n");
            escritor.write("<tr><th>Total Prestamos</th><th>Libro Mas Prestado</th><th>Usuario Mas Activo</th><th>Total Usuarios Unicos</th></tr>\n");

            escritor.write("<tr>");
            escritor.write("<td>" + prestamos.size() + "</td>");
            escritor.write("<td>" + libroPrestado + "</td>");
            escritor.write("<td>" + masFrecuente + "</td>");
            escritor.write("<td>" + usuarios.toClientList().size() + "</td>");
            escritor.write("</tr>\n");

            escritor.write("</table>\n");

            //Préstamos vencidos
            escritor.write("<h2>Prestamos Vencidos</h2>\n");
            escritor.write("<table>\n");
            escritor.write("<tr><th>ID Usuario</th><th>Nombre</th><th>ID Libro</th><th>Titulo</th><th>Fecha Prestamo</th></tr>\n");

            for(Loan prestamo : vencidos){
                escritor.write("<tr>");
                escritor.write("<td>" + prestamo.getClient().getId() + "</td>");
                escritor.write("<td>" + prestamo.getClient().getName() + "</td>");
                escritor.write("<td>" + prestamo.getBook().getId() + "</td>");
                escritor.write("<td>" + prestamo.getBook().getTitle() + "</td>");
                escritor.write("<td>" + prestamo.getDateLoan() + "</td>");
                escritor.write("</tr>\n");
            }

            escritor.write("</table>\n");

            escritor.write("</body>\n</html>");
            System.out.println("El archivo HTML generado correctamente...\n");

        } catch (IOException e){

            System.err.println("Error al generar el archivo HTML: " + e.getMessage());
        }
    }
}
