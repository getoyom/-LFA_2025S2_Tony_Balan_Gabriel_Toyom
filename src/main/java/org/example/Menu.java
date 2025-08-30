package org.example;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private final Scanner sc;
    private final TxtReader reader;
    private final ArrayList<Book> bookList;
    private final ArrayList<Client> clientList;
    private final ArrayList<Loan> loanList;
    private final BookHashTable bookHashTable;
    private final ClientHashTable clientHashTable;
    private final ArrayList<Loan> DueLoans;
    LocalDate hoy;


    public Menu() {
        this.bookHashTable = new BookHashTable();
        this.clientHashTable = new ClientHashTable();
        this.sc = new Scanner(System.in);
        this.reader = new TxtReader();
        this.bookList = new ArrayList<>();
        this.clientList = new ArrayList<>();
        this.loanList = new ArrayList<>();
        this.DueLoans = new ArrayList<>();
        this.hoy = LocalDate.now();
    }

    public void menu() {
        boolean exit = false;
        int menuOption;
        while (!exit) {
            System.out.println("------------------------");
            System.out.println("PROYECTO #01. \n Ingrese el valor para seleccionar una opcion.");
            System.out.println("1.Cargar Usuarios\n2.Cargar Libros\n3.Cargar registro desde un archivo\n4.Mostrar historial de prestamos\n5.Mostrar usuarios unicos\n6.Mostrar Libros Prestados\n7.Mostrar estadisticas de prestamo\n8.Mostrar prestamos vencidos\n9.Exportar registros\n10.Salir");
            System.out.println("------------------------");
            try {
                menuOption = sc.nextInt();
                switch (menuOption) {
                    case 1 -> addClient();
                    case 2 -> addBook();
                    case 3 -> addFile();
                    case 4 -> showLoans();
                    case 5 -> clientHashTable.showTable();
                    case 6 -> bookHashTable.showTable();
                    case 7 -> showStats();
                    case 8 -> showDueLoans();
                    case 9 -> {}
                    case 10 -> exit = true;
                    default -> System.out.println("Seleccione una opcion entre 1-10");
                }
            } catch (InputMismatchException e) {
                System.out.println("Debes ingresar un valor entero\nPor favor vuelva a intentarlo..\n");
                sc.next();
            }
        }
        System.out.println("Gracias por utilizar nuestros servicios, vuelva pronto!");
    }

    private void addClient(){

    }

    private void addBook(){


    }
    private void addFile() {
        System.out.println("Ingrese el nombre del archivo a leer (sin la extension .txt): ");
        try {
            sc.nextLine();
            String fileName = sc.nextLine() +".txt";
            reader.readFile(fileName, bookList, clientList, loanList);
            loadFiles();
        } catch (InputMismatchException e) {
            System.out.println("Entrada invalida! ");
        }
    }

    private void loadFiles() {
        for (Book book : bookList) {
            bookHashTable.insert(book);
        }
        for (Client client : clientList) {
            clientHashTable.insert(client);
        }
        for(Loan loan : loanList) {
            // Fecha de devolución ya pasó Y no tiene fecha de devolución registrada (aún no devuelto)
            if(loan.getDateDue() != null && loan.getDateDue().isBefore(hoy)) {
                DueLoans.add(loan);
            }
        }
    }

    private void showLoans() {
        int loanCounter = 1;
        System.out.println("------------------------");
        System.out.println("PRESTAMOS");
        System.out.println("------------------------");
        for (Loan loan : loanList) {
            System.out.printf("%d) %s\n", loanCounter , loan.toString());
            loanCounter++;
        }
        System.out.println("------------------------");
    }

    private void showStats() {
        System.out.println("------------------------");
        System.out.println("ESTADISTICAS DE PRESTAMOS");
        System.out.println("------------------------");
        System.out.printf("Total de prestamos: %d\n", loanList.size());
        System.out.printf("Total de usuarios: %d\n", clientHashTable.toClientList().size());
    }

    private void showDueLoans() {
        int i = 1;
        System.out.println("------------------------");
        System.out.println("PRESTAMOS VENCIDOS");
        System.out.println("------------------------");
        for (Loan loan : DueLoans) {
            System.out.printf("%d) %s\n", i , loan.toString());
            i++;
        }
        System.out.println("------------------------");
    }
}
