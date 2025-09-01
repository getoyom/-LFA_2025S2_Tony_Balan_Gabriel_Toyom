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
    private final HtmlGenerator Generator;
    private Client mostActiveClient;
    private Book mostLoanedBook;

    public Menu() {
        this.bookHashTable = new BookHashTable();
        this.clientHashTable = new ClientHashTable();
        this.sc = new Scanner(System.in);
        this.reader = new TxtReader();
        this.bookList = new ArrayList<>();
        this.clientList = new ArrayList<>();
        this.loanList = new ArrayList<>();
        this.DueLoans = new ArrayList<>();
        this.Generator = new HtmlGenerator();
    }

    public void menu() {
        boolean exit = false;
        int menuOption;
        while (!exit) {
            System.out.println("------------------------");
            System.out.println("PROYECTO #01. \n Ingrese el valor para seleccionar una opcion.");
            System.out.println("1.Cargar registro desde un archivo\n2.Cargar Usuarios\n3.Cargar Libros\n4.Mostrar historial de prestamos\n5.Mostrar usuarios unicos\n6.Mostrar Libros Prestados\n7.Mostrar estadisticas de prestamo\n8.Mostrar prestamos vencidos\n9.Exportar registros\n10.Salir");
            System.out.println("------------------------");
            try {
                menuOption = sc.nextInt();
                switch (menuOption) {
                    case 1 -> addFile();
                    case 2 -> addClient();
                    case 3 -> addBook();
                    case 4 -> showLoans();
                    case 5 -> clientHashTable.showTable();
                    case 6 -> bookHashTable.showTable();
                    case 7 -> showStats();
                    case 8 -> showDueLoans();
                    case 9 -> export();
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
        if(clientList.isEmpty()){
            System.out.println("La lista de clientes no contiene datos!.");
        }
        else{
            for (Client client : clientList) {
                clientHashTable.insert(client);
            }
            System.out.println("Los clientes se han agregado correctamente!");
        }
    }

    private void addBook(){
        if(bookList.isEmpty()){
            System.out.println("La lista de libros no contiene datos!.");
        }
        else {
            for (Book book : bookList) {
                bookHashTable.insert(book);
            }
            System.out.println("La lista de libros se han agregado correctamente!");
        }
    }

    private void addFile() {
        System.out.println("Ingrese el nombre del archivo a leer (sin la extension .txt): ");
        try {
            sc.nextLine();
            String fileName = sc.nextLine();
            String fullFileName = fileName + ".txt";
            reader.readFile(fullFileName, bookList, clientList, loanList);
            loadLoans();
        } catch (InputMismatchException e) {
            System.out.println("Entrada invalida! ");
        }
    }

    private void loadLoans() {
        //Si la fecha de devolucion es null
        for(Loan loan : loanList) {
            if(loan.getDateDue() == null){
                // El plazo de préstamo será de 15 días
                LocalDate newDueDate = loan.getDateLoan().plusDays(15);
                loan.setDateDue(newDueDate);
            }
            // Verificar si el prestamo se vencio (fecha actual > fecha de vencimiento)
            if(LocalDate.now().isAfter(loan.getDateDue())){
                // Verificar que no esté ya en la lista de vencidos
                boolean yaEnLista = false;
                for(Loan dueLoan : DueLoans){
                    if(dueLoan.getClient().getId() == loan.getClient().getId() &&
                            dueLoan.getBook().getId().equals(loan.getBook().getId()) &&
                            dueLoan.getDateLoan().equals(loan.getDateLoan())){
                        yaEnLista = true;
                        break;
                    }
                }
                if(!yaEnLista){
                    DueLoans.add(loan);
                }
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
        //Total de préstamos
        int totalPrestamos = loanList.size();
        //Libro más prestado
        mostLoanedBook = findMostLoanedBook(loanList);
        //Usuario más activo
        mostActiveClient = findMostActiveClient(loanList);
        // Total de usuarios únicos
        int totalUsuarios = clientHashTable.toClientList().size();
        System.out.printf("Total de prestamos: %d\n", totalPrestamos);
        System.out.printf("Libro más prestado: %s\n", mostLoanedBook != null ? mostLoanedBook.getTitle() : "N/A");
        System.out.printf("Usuario más activo: %s\n", mostActiveClient != null ? mostActiveClient.getName() : "N/A");
        System.out.printf("Total de usuarios únicos: %d\n",  totalUsuarios);
        System.out.println("------------------------");
    }

    // Auxiliar para encontrar el libro mas prestado
    private Book findMostLoanedBook(ArrayList<Loan> loans) {
        if (loans.isEmpty()) return null;
        // Obtener todos los libros únicos desde la hash table
        ArrayList<Book> allBooks = bookHashTable.toBookList();

        int maxLoanCount = 0;
        // Para cada libro unico, contar cuantas veces aparece en los loans
        for (Book book : allBooks) {
            int loanCount = 0;
            for (Loan loan : loans) {
                if (loan.getBook() != null &&
                        loan.getBook().getId().equals(book.getId())) {
                    loanCount++;
                }
            }
            if (loanCount > maxLoanCount) {
                maxLoanCount = loanCount;
                mostLoanedBook = book;
            }
        }
        return mostLoanedBook;
    }

    // Auxiliar para encontrar el cliente más activo
    private Client findMostActiveClient(ArrayList<Loan> loans) {
        if (loans.isEmpty()) return null;

        // Obtener todos los clientes unicos desde la hash table
        ArrayList<Client> allClients = clientHashTable.toClientList();

        int maxLoanCount = 0;

        // Para cada cliente unico, contar cuántas veces aparece en los loans
        for (Client client : allClients) {
            int loanCount = 0;

            for (Loan loan : loans) {
                if (loan.getClient() != null &&
                        loan.getClient().getId() == client.getId()) {
                    loanCount++;
                }
            }
            if (loanCount > maxLoanCount) {
                maxLoanCount = loanCount;
                mostActiveClient = client;
            }
        }
        return mostActiveClient;
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

    private void export(){
        addClient();
        addBook();
        mostLoanedBook = findMostLoanedBook(loanList);
        mostActiveClient = findMostActiveClient(loanList);
        Generator.GenerateHTML(bookHashTable, clientHashTable, loanList, mostActiveClient, mostLoanedBook, DueLoans);
    }
}