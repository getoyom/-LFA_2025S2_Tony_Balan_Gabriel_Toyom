package org.example;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TxtReader {
    protected String[] rows = null;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

     //Lee un archivo de texto con registros de prestamo y los procesa
    public void readFile(String fileName, ArrayList<Book> bookList, ArrayList<Client> clientList, ArrayList<Loan> loanList) {
        // Limpieza de cache
        bookList.clear();
        clientList.clear();
        loanList.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Verificar si el archivo no tiene datos
            if (!reader.ready()) {
                System.out.println("El archivo está vacio o no se puede leer.");
                return;
            }
            String line;
            int lineNumber = 0;
            int successfulRecords = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                rows = line.split(",");

                // Validar caracteres invalidos en la linea completa
                if (hasInvalidCharacters(line, lineNumber)) {
                    continue;
                }

                // Verificar que la linea tenga al menos 5 campos
                if (rows.length < 5) {
                    System.err.printf("Linea %d mal formateada (esperaba al menos 5 campos, se encontraron %d): %s%n",
                            lineNumber, rows.length, line);
                    continue;
                }

                // Procesar cada campo de la linea
                if (processRecord(lineNumber, bookList, clientList, loanList)) {
                    successfulRecords++;
                }
            }
            //Imprimir resumen del archivo y como el reader lo proceso
            printSuccessfully(lineNumber, successfulRecords, loanList.size());

        } catch (FileNotFoundException e) {
            System.err.printf("ERROR: No se encontro el archivo: %s%n", fileName);
            System.err.println("Verificar que el archivo existe y está en la ubicacion correcta.");
        } catch (IOException e) {
            System.err.printf("ERROR: No se pudo leer el archivo: %s%n", fileName);
            e.printStackTrace();
        }
    }

    //Procesa un registro individual del archivo
    private boolean processRecord(int lineNumber, ArrayList<Book> bookList, ArrayList<Client> clientList, ArrayList<Loan> loanList) {
        try {
            // Validar ID del cliente
            String clientIdStr = rows[0].trim();
            if (!isValidClientId(clientIdStr, lineNumber)) {
                return false;
            }

            // Validar nombre del usuario
            String userName = rows[1].trim();
            if (!isValidUserName(userName, lineNumber)) {
                return false;
            }

            // Validar ID del libro
            String bookId = rows[2].trim();
            if (!isValidBookId(bookId, lineNumber)) {
                return false;
            }

            // Validar titulo del libro
            String bookTitle = rows[3].trim();
            if (!isValidBookTitle(bookTitle, lineNumber)) {
                return false;
            }

            // Procesar fechas con auxiliar propio
            LocalDate loanDate = parseDate(rows[4].trim(), lineNumber, "fecha_prestamo");
            if (loanDate == null) {
                return false;
            }

            LocalDate returnDate = null;
            if (rows.length > 5 && !rows[5].trim().isEmpty()) {
                returnDate = parseDate(rows[5].trim(), lineNumber, "fecha_devolucion");

                // Validar que fecha de devolucion no sea anterior a fecha de préstamo
                if (returnDate != null && returnDate.isBefore(loanDate)) {
                    System.err.printf("Error en linea %d: La fecha de devolucion (%s) no puede ser anterior a la fecha de prestamo (%s)%n",
                            lineNumber, returnDate, loanDate);
                    return false;
                }
            }

            // Crear objetos
            int clientId = Integer.parseInt(clientIdStr);
            Book book = new Book(bookId, bookTitle);
            Client client = new Client(clientId, userName, book);
            Loan loan = new Loan(client, book, loanDate, returnDate);

            // Agregar a las listas (simulador de cache)
            bookList.add(book);
            clientList.add(client);
            loanList.add(loan);

            return true;

        } catch (NumberFormatException e) {
            System.err.printf("Error al convertir ID de cliente a numero en linea %d: %s%n", lineNumber, rows[0].trim());
            return false;
        }
    }

    //Auxiliar que el ID del cliente sea numérico y tenga longitud correcta
    private boolean isValidClientId(String clientId, int lineNumber) {
        if (clientId.isEmpty() || clientId.length() > 4) {
            System.err.printf("El ID del cliente no tiene la longitud correcta en linea %d: '%s' (maximo 4 digitos)%n", lineNumber, clientId);
            return false;
        }

        for (int i = 0; i < clientId.length(); i++) {
            char digit = clientId.charAt(i);
            if (!Character.isDigit(digit)) {
                System.err.printf("Caracter invalido en el ID del cliente en linea %d, posicion %d: '%c'%n",
                        lineNumber, (i + 1), digit);
                return false;
            }
        }

        try {
            int id = Integer.parseInt(clientId);
            if (id <= 0) {
                System.err.printf("ID de cliente debe ser mayor a 0 en linea %d: %d%n", lineNumber, id);
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    //Auxiliar que el nombre del usuario solo contenga letras y espacios
    private boolean isValidUserName(String userName, int lineNumber) {
        if (userName.isEmpty()) {
            System.err.printf("Nombre de usuario vacio en linea %d%n", lineNumber);
            return false;
        }

        for (int i = 0; i < userName.length(); i++) {
            char character = userName.charAt(i);
            if (!Character.isLetter(character) && character != ' ') {
                System.err.printf("Nombre invalido en linea %d, posicion %d: '%c' (solo se permiten letras y espacios)%n",
                        lineNumber, (i + 1), character);
                return false;
            }
        }
        return true;
    }


    //Auxiliar que verifica el ID del libro y tenga el formato correcto LIB###
    private boolean isValidBookId(String bookId, int lineNumber) {
        if (bookId.length() != 6) {
            System.err.printf("Longitud del ID del libro invalida en linea %d: '%s' (debe ser de 6 caracteres)%n",
                    lineNumber, bookId);
            return false;
        }

        // Validar codigo 'LIB'
        String code = bookId.substring(0, 3);
        if (!isValidLibCode(code, lineNumber)) {
            return false;
        }

        // Validar numero del libro
        String number = bookId.substring(3, 6);
        return isValidLibNumber(number, lineNumber);
    }


    //Auxiliar que verifica el codigo  y este sea exactamente 'LIB' sin números
    private boolean isValidLibCode(String code, int lineNumber) {
        // Verificar que no tenga números
        for (int i = 0; i < code.length(); i++) {
            char character = code.charAt(i);
            if (Character.isDigit(character)) {
                System.err.printf("El codigo de libro contiene un numero en linea %d, posicion %d: '%c'%n",
                        lineNumber, (i + 1), character);
                return false;
            }
        }

        // Verificar que sea exactamente 'LIB'
        if (!code.equals("LIB")) {
            System.err.printf("El codigo del libro es incorrecto en linea %d: '%s' (debe ser 'LIB')%n",
                    lineNumber, code);
            return false;
        }

        return true;
    }

    //Auxiliar que verifica que el numero del libro sean exactamente 3 dígitos
    private boolean isValidLibNumber(String number, int lineNumber) {
        if (number.length() != 3) {
            System.err.printf("El numero del libro es incorrecto en linea %d: '%s' (debe tener 3 digitos)%n",
                    lineNumber, number);
            return false;
        }

        for (int i = 0; i < number.length(); i++) {
            char digit = number.charAt(i);
            if (!Character.isDigit(digit)) {
                System.err.printf("El numero del libro contiene caracter no numerico en linea %d, posicion %d: '%c'%n",
                        lineNumber, (i + 1), digit);
                return false;
            }
        }

        return true;
    }


    //Auxiliar para ver que el título del libro no este vacio
    private boolean isValidBookTitle(String title, int lineNumber) {
        if (title.isEmpty()) {
            System.err.printf("Titulo de libro vacio en linea %d%n", lineNumber);
            return false;
        }
        return true;
    }


    //Method que parsea una fecha con manejo de errores
    private LocalDate parseDate(String dateString, int lineNumber, String fieldName) {
        if (dateString.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.printf("Error al parsear %s en linea %d: '%s' (formato esperado: YYYY-MM-DD)%n",
                    fieldName, lineNumber, dateString);
            return null;
        }
    }


    //Verifica si una linea contiene caracteres invalidos
    private boolean hasInvalidCharacters(String line, int lineNumber) {
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            // Validar que sea un caracter permitido
            if (!Character.isLetterOrDigit(character) &&
                    character != ',' && character != ' ' &&
                    character != '-' && character != '_') {

                System.err.printf("Caracter invalido en linea %d, posicion %d: '%c'%n",
                        lineNumber, (i + 1), character);
                return true;
            }
        }
        return false;
    }



    //Imprime el resumen del procesamiento del archivo
    private void printSuccessfully(int totalLines, int successfulRecords, int totalLoans) {
        System.out.println("------------------------");
        System.out.printf("Archivo TXT leido exitosamente. Procesadas %d lineas%n", totalLines);
        System.out.printf("Registros exitosos: %d%n", successfulRecords);
        System.out.printf("Prestamos cargados: %d%n", totalLoans);
        System.out.println("------------------------");
    }
}