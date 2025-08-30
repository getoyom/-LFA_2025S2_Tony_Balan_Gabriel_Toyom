package org.example;
import java.util.ArrayList;

// Hash para promediar valoraciones de artículos
public class ClientHashTable {

    //Clase anidada para nodo
    public class TableNode {
        private Client client;
        private boolean occupied;
        private TableNode next;

        public TableNode() {
            this.client = null;
            this.occupied = false;
            this.next = null;
        }

        public void assign(Client client, TableNode next) {
            this.client = client;
            this.occupied = true;
            this.next = next;
        }

        public void clear() {
            this.client = null;
            this.occupied = false;
            this.next = null;
        }

        public Client getClient() {
            return client;
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
            return client.toStringTable();
        }
    }

    private final int SIZE = 9999;
    private final TableNode[] table;

    public ClientHashTable() {
        table = new TableNode[SIZE + 1];
        for (int i = 1; i <= SIZE; i++) {
            table[i] = new TableNode();
        }
    }

    private int hashFunction(String name) {
        int hash = 0;
        int prime = 31;
        for (int i = 0; i < name.length(); i++) {
            hash = hash * prime + name.charAt(i);
        }
        hash = Math.abs(hash);
        return (hash % SIZE) + 1;
    }

    // Inserta o actualiza promedio y votos
    public void insert(Client client) {
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            System.out.println("Nombre no valido.");
            return;
        }

        // Pasar el nombre del cliente a minusculas para comparar
        String ToLowerName = client.getName().trim().toLowerCase();
        int index = hashFunction(ToLowerName);

        // Buscar si el nombre ya existe
        if (table[index].isOccupied()) {
            TableNode n = table[index];
            while (n != null) {
                if (n.getClient().getName() != null && n.getClient().getName().trim().toLowerCase().equals(ToLowerName)) {
                    //System.out.println("Cliente ya existe: " + client.getName());
                    return;
                }
                n = n.getNext();
            }
            //Si el cliente no existe, crear nuevo nodo
            TableNode newNode = new TableNode();
            newNode.assign(client, table[index].getNext());
            table[index].setNext(newNode);
        } else {
            // Índice vacío, agregar el cliente original (sin modificar)
            table[index].assign(client, null);
        }
    }


    public Client get(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        name = name.trim().toLowerCase(); // ← Normalizar aquí
        int index = hashFunction(name);
        TableNode n = table[index];
        while (n != null) {
            if (n.getClient() != null &&
                    n.getClient().getName().trim().toLowerCase().equals(name)) { // ← Y aquí
                return n.getClient();
            }
            n = n.getNext();
        }
        return null;
    }

    public boolean contains(String name) {
        return get(name) != null;
    }


    public boolean delete(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        name = name.trim().toLowerCase(); // ← Normalizar aquí
        int index = hashFunction(name);
        TableNode n = table[index];
        TableNode previous = null;

        while (n != null) {
            if (n.getClient() != null &&
                    n.getClient().getName().trim().toLowerCase().equals(name)) { // ← Y aquí
                if (previous == null) {
                    // primer nodo
                    if (n.getNext() != null) {
                        TableNode s = n.getNext();
                        n.assign(s.getClient(), s.getNext());
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


    // Transformar directamente en arrayList
    public ArrayList<Client> toClientList() {
        ArrayList<Client> list = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) {
            if (table[i].isOccupied()) {
                list.add(table[i].getClient());
                TableNode n = table[i].getNext();
                while (n != null) {
                    list.add(n.getClient());
                    n = n.getNext();
                }
            }
        }
        return list;
    }

    public void showTable() {
        System.out.println("------------------------");
        System.out.println(" TABLA CLIENTES");
        System.out.println("------------------------");
        int elements = 0;
        for (int i = 1; i <= SIZE; i++) {
            if (table[i].isOccupied()) {
                System.out.printf("Indice %d : ", i);
                System.out.println(table[i]);
                elements++;
                TableNode n = table[i].getNext();
                while (n != null) {
                    System.out.println("\t↳ " + n);
                    elements++;
                    n = n.getNext();
                }
            }
        }
        if (elements == 0) System.out.println("La tabla está vacia.");
        System.out.printf("Total de elementos: %d \n", elements);
        System.out.println("------------------------");
    }
}