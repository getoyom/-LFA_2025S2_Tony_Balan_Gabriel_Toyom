package org.example;
import java.util.ArrayList;

// Hash table para libros
public class BookHashTable {

    //Clase anidada para nodo
    private class TableNode {
        private Book book;
        private boolean occupied;
        private TableNode next;

        public TableNode() {
            this.book = null;
            this.occupied = false;
            this.next = null;
        }

        public void assign(Book book, TableNode next) {
            this.book = book;
            this.occupied = true;
            this.next = next;
        }

        public void clear() {
            this.book = null;
            this.occupied = false;
            this.next = null;
        }

        public Book getBook() {
            return book;
        }

        public boolean isOccupied() {
            return occupied;
        }

        public TableNode getNext() {
            return next;
        }

        public void setNext(TableNode next) {
            this.next = next;
        }

        @Override
        public String toString() {
            if (!occupied) return "Vacio";
            return book.toString();
        }
    }

    private final int SIZE = 9999;
    private final TableNode[] table;

    public BookHashTable() {
        table = new TableNode[SIZE + 1];
        for (int i = 1; i <= SIZE; i++) {
            table[i] = new TableNode();
        }
    }

    private int hashFunction(String title) {
        int hash = 0;
        int prime = 31;
        for (int i = 0; i < title.length(); i++) {
            hash = hash * prime + title.charAt(i);
        }
        hash = Math.abs(hash);
        return (hash % SIZE) + 1;
    }

    // Inserta libro por title (sin duplicados)
    public void insert(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            System.out.println("Título no válido.");
            return;
        }

        // titulo en min para comparar
        String lowerTitle = book.getTitle().trim().toLowerCase();
        int index = hashFunction(lowerTitle);

        // Buscar si el titulo ya existe
        if (table[index].isOccupied()) {
            TableNode n = table[index];
            while (n != null) {
                if (n.getBook().getTitle() != null &&
                        n.getBook().getTitle().trim().toLowerCase().equals(lowerTitle)) {
                    // System.out.printf("Libro con título ya existe: %s \n",book.getTitle());
                    return;
                }
                n = n.getNext();
            }
            // Si el libro no existe, crear nuevo nodo
            TableNode newNode = new TableNode();
            newNode.assign(book, table[index].getNext());
            table[index].setNext(newNode);
        } else {
            // Índice vacio, agregar el libro
            table[index].assign(book, null);
        }
    }

    public Book get(String title) {
        if (title == null || title.trim().isEmpty()) return null;
        title = title.trim().toLowerCase();
        int index = hashFunction(title);
        TableNode n = table[index];
        while (n != null) {
            if (n.getBook() != null &&
                    n.getBook().getTitle().trim().toLowerCase().equals(title)) {
                return n.getBook();
            }
            n = n.getNext();
        }
        return null;
    }

    // Buscar libro por ID
    public Book getById(String id) {
        if (id == null || id.trim().isEmpty()) return null;
        id = id.trim().toLowerCase();

        for (int i = 1; i <= SIZE; i++) {
            if (table[i].isOccupied()) {
                // Revisar nodo principal
                if (table[i].getBook().getId().trim().toLowerCase().equals(id)) {
                    return table[i].getBook();
                }
                // Revisar nodos encadenados
                TableNode n = table[i].getNext();
                while (n != null) {
                    if (n.getBook().getId().trim().toLowerCase().equals(id)) {
                        return n.getBook();
                    }
                    n = n.getNext();
                }
            }
        }
        return null;
    }

    public boolean contains(String title) {
        return get(title) != null;
    }

    public boolean containsId(String id) {
        return getById(id) != null;
    }

    public boolean delete(String title) {
        if (title == null || title.trim().isEmpty()) return false;
        title = title.trim().toLowerCase();
        int index = hashFunction(title);
        TableNode n = table[index];
        TableNode previous = null;

        while (n != null) {
            if (n.getBook() != null &&
                    n.getBook().getTitle().trim().toLowerCase().equals(title)) {
                if (previous == null) {
                    // primer nodo
                    if (n.getNext() != null) {
                        TableNode s = n.getNext();
                        n.assign(s.getBook(), s.getNext());
                    } else {
                        n.clear();
                    }
                } else {
                    previous.setNext(n.getNext());
                }
                return true;
            }
            previous = n;
            n = n.getNext();
        }
        return false;
    }

    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) return false;
        id = id.trim().toLowerCase();

        for (int i = 1; i <= SIZE; i++) {
            if (table[i].isOccupied()) {
                // Revisar nodo principal
                if (table[i].getBook().getId().trim().toLowerCase().equals(id)) {
                    if (table[i].getNext() != null) {
                        TableNode s = table[i].getNext();
                        table[i].assign(s.getBook(), s.getNext());
                    } else {
                        table[i].clear();
                    }
                    return true;
                }

                // Revisar nodos encadenados
                TableNode n = table[i].getNext();
                TableNode previous = table[i];
                while (n != null) {
                    if (n.getBook().getId().trim().toLowerCase().equals(id)) {
                        previous.setNext(n.getNext());
                        return true;
                    }
                    previous = n;
                    n = n.getNext();
                }
            }
        }
        return false;
    }

    public ArrayList<Book> toBookList() {
        ArrayList<Book> list = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) {
            if (table[i].isOccupied()) {
                list.add(table[i].getBook());
                TableNode n = table[i].getNext();
                while (n != null) {
                    list.add(n.getBook());
                    n = n.getNext();
                }
            }
        }
        return list;
    }

    public void showTable() {
        System.out.println("------------------------");
        System.out.println(" TABLA LIBROS");
        System.out.println("------------------------");
        int elements = 0;
        for (int i = 1; i <= SIZE; i++) {
            if (table[i].isOccupied()) {
                System.out.printf("Indice %d : ", i);
                System.out.println(table[i]);
                elements++;
                TableNode n = table[i].getNext();
                while (n != null) {
                    System.out.printf("\t↳ %s \n", n);
                    elements++;
                    n = n.getNext();
                }
            }
        }
        if (elements == 0) System.out.println("La tabla está vacía.");
        System.out.printf("Total de elementos: %d \n", elements);
        System.out.println("------------------------");
    }
}
