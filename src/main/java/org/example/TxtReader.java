package org.example;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TxtReader {
    protected String[] rows = null;
    // Formatter para las fechas en formato YYYY-MM-DD
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Lector registros completos
    public void readFile(String fileName, ArrayList<Book> bookList, ArrayList<Client> clientList, ArrayList<Loan> loanList) {
        //Limpieza de cache
        bookList.clear();
        clientList.clear();
        loanList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            int successfulRecords = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;
                rows = line.split(",");

                if(caracteresInvalidos(line, lineNumber)){ //Si es true pasa directamente a la siguiente línea de texto
                    continue;
                }

                // id_usuario, nombre_usuario, id_libro, titulo_libro, fecha_prestamo, fecha_devolucion
                if (rows.length >= 5) { // Al menos 5 campos (algunos registros pueden no tener fecha de devolucion)

                    ///Acá se valida la longitud del id del cliente/usuario
                    String idCliente = rows[0].trim();
                    if(idCliente.isEmpty() || idCliente.length() > 4){
                        System.out.println("El ID del cliente no tiene la longitud correcta: " + "Numero de linea: " + lineNumber + ", ID no valido: " + idCliente + "\n");
                        continue; ///Fuera del try para que salte correctamente
                    }

                    ///Validación carácter por carácter del id del usuario
                    boolean caracterNoValidoID = false;
                    for(int i = 0; i < idCliente.length(); i++){

                        char numero = idCliente.charAt(i);
                        if(!Character.isDigit(numero)){
                            System.out.printf("Caracter invalido en el id del cliente en la linea %d, posicion %d y el caracter es: '%c' %n", lineNumber, (i+1), numero);
                            caracterNoValidoID = true;
                        }
                    }

                    if(caracterNoValidoID){
                        continue;
                    }

                    ///Validación carácter por carácter del nombre del usuario
                    String validacionNombre = rows[1].trim();
                    boolean tieneNumero = false;

                    //Se valida que el nombre solo tenga letras y espacios
                    for (int i = 0; i < validacionNombre.length(); i++) {
                        char letra = validacionNombre.charAt(i);
                        if (!Character.isLetter(letra) && letra != ' ') {
                            System.out.printf("Nombre invalido en línea %d, posición %d: '%c'%n", lineNumber, i + 1, letra);
                            tieneNumero = true;
                            break;
                        }
                    }

                    if(tieneNumero){
                        continue; ///Sigue a la siguiente línea del texto
                    }

                    ///Validación ID del libro
                    String validacionIdLibro = rows[2].trim();
                    if (validacionIdLibro.length() != 6){

                        System.out.println("Longitud del id del libro no valido en la linea " + lineNumber  + "\n");
                        continue;
                    }

                    String codigo = validacionIdLibro.substring(0, 3); ///Solo obtiene LIB
                    ///Validacion del código 'LIB'
                    boolean numeroEncontrado = false;
                    //Ver si hay números dentro de 'LIB'
                    for(int i = 0; i < codigo.length()-1; i++){
                        char caracter = codigo.charAt(i);
                        if(Character.isDigit(caracter)){
                            System.out.println("El codigo de libro esta incorrecto en la linea " + lineNumber + ", el codigo no valido es " + codigo + "caracter: " + caracter + "\n");
                            numeroEncontrado = true;
                            break;
                        }
                    }

                    if(numeroEncontrado){
                        continue;
                    }
                    //Ver si cumple con la palabra 'LIB'
                    if(!codigo.equals("LIB")){
                        System.out.println("El codigo del libro esta incorrecto en la linea " + lineNumber + ", el codigo no valido es : " + codigo + "\n");
                        continue;
                    }
                    ///Validación del número del código de libro
                    String numero = validacionIdLibro.substring(3, 6);
                    if(numero.length() != 3){
                        System.out.println("El numero del libro esta incorrecto en la linea " + lineNumber + ", el numero no valido es : " + numero + "\n");
                        continue;
                    }
                    //Ver si tiene letra
                    boolean contieneLetra = false;
                    for(int i = 0; i < numero.length(); i++){
                        char num = numero.charAt(i);
                        if(!Character.isDigit(num)){
                            System.out.println("El numero del libro contiene una letra en la linea " + lineNumber + ", el numero no valido es: " + validacionNombre + "\n");
                            contieneLetra = true;
                            break;
                        }
                    }
                    if(contieneLetra) continue;

                    try {
                        int id_cliente = Integer.parseInt(rows[0].trim()); ///Si no puede hacer el casteo devuelve la excepción
                        String nombre_usuario = rows[1].trim();
                        String id_libro = rows[2].trim();
                        String titulo_libro = rows[3].trim();
                        // Manejo de fecha de préstamo
                        LocalDate fecha_prestamo = null;
                        if (rows.length > 4 && !rows[4].trim().isEmpty()) {
                            fecha_prestamo = parseDate(rows[4].trim(), lineNumber, "fecha_prestamo");
                        }
                        // Manejo de fecha de devolución (opcional)
                        LocalDate fecha_devolucion = null;
                        if (rows.length > 5 && !rows[5].trim().isEmpty()) {
                            fecha_devolucion = parseDate(rows[5].trim(), lineNumber, "fecha_devolucion");
                        }

                        // Crear objetos libro y usuario
                        Book libro = new Book(id_libro, titulo_libro);
                        Client usuario = new Client(id_cliente, nombre_usuario, libro);

                        ///Objeto que guardará el préstamo
                        Loan prestamo = new Loan(usuario, libro, fecha_prestamo, fecha_devolucion);

                        // Agregar a las listas si no existen ya
                        successfulRecords++;
                        bookList.add(libro);
                        clientList.add(usuario);
                        loanList.add(prestamo);
                        ///Acá colocar la lógica para que se guarde el préstamo

                    } catch (NumberFormatException e) {
                        System.err.printf("Error al convertir ID de cliente a número en línea %d: %s%n",
                                lineNumber, line);
                        System.err.printf("Valor problemático: '%s'%n", rows[0].trim());
                    }
                }
                else {
                    System.err.printf("Línea %d mal formateada (esperaba al menos 5 campos, se encontraron %d): %s%n",
                            lineNumber, rows.length, line);
                }
            }

            System.out.println("------------------------");
            System.out.printf("Archivo TXT leído exitosamente. Procesadas %d líneas%n", lineNumber);
            System.out.printf("Registros exitosos: %d%n", successfulRecords);
            System.out.printf("Prestamos cargados: %d%n", loanList.size());
            System.out.println("------------------------");

        } catch (FileNotFoundException e) {
            System.err.printf("ERROR: No se encontró el archivo: %s%n", fileName);
            System.err.println("Verificar que el archivo existe y está en la ubicación correcta.");
        }
        catch (IOException e) {
            System.err.printf("ERROR: No se pudo leer el archivo: %s%n", fileName);
            e.printStackTrace();
        }
    }

    //Auxiliar para parsear fechas con manejo de errores
    private LocalDate parseDate(String dateString, int lineNumber, String fieldName) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.printf("Error al parsear %s en línea %d: '%s'. Formato esperado: YYYY-MM-DD%n",
                    fieldName, lineNumber, dateString);
            return null;
        }
    }

    ///Otra función auxiliar para recorrer carácter por carácter
    private static boolean caracteresInvalidos(String linea, int numeroLinea){

        for(int i = 0; i < linea.length(); i++){

            char caracter = linea.charAt(i);  ///Recorre cada carácter de la línea completa

            if(!Character.isLetterOrDigit(caracter) && caracter != ',' && caracter != ' ' && caracter != '-' && caracter != '_'){
                ///Se valida que no sea un carácter/dígito válido o algún otro símbolo distinto a ',',' ', '-' y '_'
                System.out.printf("Caracter invalido en la linea %d, posicion %d y el caracter es: '%c' %n", numeroLinea, (i+1), caracter);
                return true;
            }
        }
        return false;
    }
}
