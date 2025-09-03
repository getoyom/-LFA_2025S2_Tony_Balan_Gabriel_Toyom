package org.example;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TxtReader {
    protected String[] rows = null;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Lee un archivo de texto con registros de prestamo y los procesa
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
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Saltar header si existe
                if (isFirstLine && isHeaderLine(line)) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;

                //Saltar lineas completamente vacias
                if (line.trim().isEmpty()) {
                    System.out.printf("Saltando linea vacia en linea %d%n", lineNumber);
                    continue;
                }

                // Validar Y hacer split en una sola pasada
                rows = validateAndSplit(line, lineNumber);
                // Si hay error de validacion, continuar con la siguiente linea
                if (rows == null) {
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

    // Detecta si la primera linea es un header
    private boolean isHeaderLine(String line) {
        return line.toLowerCase().contains("id_usuario") ||
                line.toLowerCase().contains("nombre_usuario") ||
                line.toLowerCase().contains("id_libro");
    }

    // Parser que maneja caracteres especiales
    private String[] validateAndSplit(String line, int lineNumber) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        //Boolean para verificar si existen comillas
        boolean insideQuotes = false;
        boolean hasInvalidChars = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            // Validar caracteres permitidos
            if (!isValidCharacter(character)) {
                System.err.printf("Caracter potencialmente problematico en linea %d, posicion %d: '%c' (codigo: %d)%n",
                        lineNumber, (i + 1), character, (int)character);
                hasInvalidChars = true;
                //Intentar procesar
            }

            // Manejo de comillas
            if (character == '"') {
                insideQuotes = !insideQuotes;
                // No agregamos las comillas al campo
                continue;
            }

            if (character == ',' && !insideQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(character);
            }
        }

        // Agregar el último campo
        fields.add(currentField.toString().trim());

        // Si habia caracteres muy problematicos, advertir pero continuar
        if (hasInvalidChars) {
            System.out.printf("ADVERTENCIA: Linea %d contiene caracteres especiales, procesando de todos modos%n", lineNumber);
        }
        return fields.toArray(new String[0]);
    }

    // Validacion de caracteres flexible
    private boolean isValidCharacter(char character) {
        // Caracteres ASCII basicos
        if (Character.isLetterOrDigit(character) ||
                character == ',' || character == ' ' || character == '-' ||
                character == '_' || character == '.' || character == ':' ||
                character == '"' || character == '\'' || character == '(' ||
                character == ')' || character == '&' || character == ';' ||
                character == '/' || character == '\\' || character == '?' ||
                character == '¿' || character == '!' || character == '@' ||
                character == '#' || character == '%' || character == '*' ||
                character == '+' || character == '=' || character == '[' ||
                character == ']' || character == '{' || character == '}' ||
                character == '|') {
            return true;
        }
        // Caracteres con acentos y especiales latinos
        if ((character >= 'À' && character <= 'ÿ') || character == 'ñ' || character == 'Ñ') {
            return true;
        }

        // Algunos caracteres de control aceptables
        if (character == '\t' || character == '\n' || character == '\r') {
            return true;
        }

        return false;
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

    // Validacion para IDs de cliente
    private boolean isValidClientId(String clientId, int lineNumber) {
        // Manejar casos especiales
        if (clientId.isEmpty() || clientId.equalsIgnoreCase("NULL") || clientId.equals("")) {
            System.err.printf("ID de cliente vacio o nulo en linea %d: '%s'%n", lineNumber, clientId);
            return false;
        }

        // Rechazar IDs claramente problematicos
        if (clientId.equals("0")) {
            System.err.printf("ID de cliente no puede ser 0 en linea %d%n", lineNumber);
            return false;
        }

        if (clientId.startsWith("-")) {
            System.err.printf("ID de cliente no puede ser negativo en linea %d: '%s'%n", lineNumber, clientId);
            return false;
        }

        // Permitir IDs alfanumericos pero advertir
        boolean hasLetters = false;
        boolean hasNumbers = false;

        for (int i = 0; i < clientId.length(); i++) {
            char c = clientId.charAt(i);
            if (Character.isLetter(c)) {
                hasLetters = true;
            } else if (Character.isDigit(c)) {
                hasNumbers = true;
            } else {
                System.err.printf("Caracter invalido en ID de cliente en linea %d, posicion %d: '%c'%n",
                        lineNumber, (i + 1), c);
                return false;
            }
        }

        if (hasLetters) {
            System.out.printf("ADVERTENCIA: ID de cliente alfanumerico en linea %d: '%s'%n", lineNumber, clientId);
            return true;
        }

        // Validacion para IDs completamente numericos
        if (clientId.length() > 4) {
            System.err.printf("ID de cliente demasiado largo en linea %d: '%s' (maximo 4 digitos)%n",
                    lineNumber, clientId);
            return false;
        }

        try {
            int id = Integer.parseInt(clientId);
            if (id <= 0) {
                System.err.printf("ID de cliente debe ser mayor a 0 en linea %d: %d%n", lineNumber, id);
                return false;
            }
        } catch (NumberFormatException e) {
            System.err.printf("ID de cliente no numerico en linea %d: '%s'%n", lineNumber, clientId);
            return false;
        }

        return true;
    }

    // Validacion para nombres de usuario
    private boolean isValidUserName(String userName, int lineNumber) {
        if (userName.isEmpty() || userName.equalsIgnoreCase("NULL")) {
            System.err.printf("Nombre de usuario vacio o nulo en linea %d%n", lineNumber);
            return false;
        }

        // Permitir caracteres en nombres
        for (int i = 0; i < userName.length(); i++) {
            char character = userName.charAt(i);
            if (!isValidNameCharacter(character)) {
                System.err.printf("Caracter cuestionable en nombre en linea %d, posicion %d: '%c' - procesando de todos modos%n",
                        lineNumber, (i + 1), character);
            }
        }
        return true;
    }

    // Caracteres validos en nombres
    private boolean isValidNameCharacter(char c) {
        return Character.isLetter(c) || c == ' ' || c == '\'' || c == '-' ||
                c == '.' || c == '"' || c == '&' || (c >= 'À' && c <= 'ÿ') ||
                c == 'ñ' || c == 'Ñ';
    }

    // Validacion para IDs de libro
    private boolean isValidBookId(String bookId, int lineNumber) {
        if (bookId.isEmpty() || bookId.equalsIgnoreCase("NULL")) {
            System.err.printf("ID de libro vacio o nulo en linea %d: '%s'%n", lineNumber, bookId);
            return false;
        }

        // Permitir diferentes longitudes pero advertir si no es el formato esperado
        if (bookId.length() < 3) {
            System.err.printf("ID de libro demasiado corto en linea %d: '%s'%n", lineNumber, bookId);
            return false;
        }

        if (bookId.length() != 6) {
            System.out.printf("ADVERTENCIA: ID de libro con formato no estandar en linea %d: '%s' (se esperaba 6 caracteres)%n",
                    lineNumber, bookId);
        }

        // Si tiene al menos 3 caracteres, verificar si empieza con LIB
        if (bookId.length() >= 3) {
            String prefix = bookId.substring(0, 3).toUpperCase();
            if (!prefix.equals("LIB")) {
                System.out.printf("ADVERTENCIA: ID de libro no empieza con 'LIB' en linea %d: '%s'%n",
                        lineNumber, bookId);
            }
        }
        return true;
    }

    // Validacion para titulos
    private boolean isValidBookTitle(String title, int lineNumber) {
        if (title.isEmpty() || title.equalsIgnoreCase("NULL")) {
            System.err.printf("Titulo de libro vacio o nulo en linea %d%n", lineNumber);
            return false;
        }
        // Los titulos pueden contener cualquier caracter
        return true;
    }

    // Parser de fechas
    private LocalDate parseDate(String dateString, int lineNumber, String fieldName) {
        if (dateString.isEmpty()) {
            return null;
        }

        // Manejar casos especiales
        if (dateString.equalsIgnoreCase("NULL") ||
                dateString.equalsIgnoreCase("invalid_date") ||
                dateString.contains("invalid")) {
            System.err.printf("Fecha invalida en %s, linea %d: '%s'%n",
                    fieldName, lineNumber, dateString);
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

    //Imprime el resumen del procesamiento del archivo
    private void printSuccessfully(int totalLines, int successfulRecords, int totalLoans) {
        System.out.println("------------------------");
        System.out.printf("Archivo TXT procesado completamente%n");
        System.out.printf("Lineas procesadas: %d%n", totalLines);
        System.out.printf("Registros exitosos: %d%n", successfulRecords);
        System.out.printf("Registros fallidos: %d%n", (totalLines - successfulRecords));
        System.out.printf("Prestamos cargados: %d%n", totalLoans);
        System.out.println("------------------------");
    }
}