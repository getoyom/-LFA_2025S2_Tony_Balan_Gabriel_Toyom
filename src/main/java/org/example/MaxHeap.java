package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class MaxHeap<T extends Comparable<T>> {

    // Clase anidada para el nodo
    public class HeapNode {
        T value;
        HeapNode left;
        HeapNode right;
        HeapNode parent;

        public HeapNode(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
        }

        //Getters y Setters
        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public HeapNode getLeft() {
            return left;
        }

        public void setLeft(HeapNode left) {
            this.left = left;
        }

        public HeapNode getRight() {
            return right;
        }

        public void setRight(HeapNode right) {
            this.right = right;
        }

        public HeapNode getParent() {
            return parent;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }
    }

    private HeapNode root;
    private int size;
    private final Comparator<T> comparator;

    // Constructor por defecto
    public MaxHeap() {
        this.root = null;
        this.size = 0;
        this.comparator = null;
    }

    // Constructor con Comparator personalizado
    public MaxHeap(Comparator<T> comparator) {
        this.root = null;
        this.size = 0;
        this.comparator = comparator;
    }

    // Método auxiliar para comparar elementos
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return a.compareTo(b);
    }

    // Insertar un nuevo elemento en el heap
    public void insert(T value) {
        if (value == null) {
            System.out.println("No se puede insertar un valor nulo.");
            return;
        }
        HeapNode newNode = new HeapNode(value);

        if (root == null) {
            root = newNode;
            size = 1;
            return;
        }

        // Encontrar la posición para insertar (siguiente posición disponible)
        HeapNode parent = findParentForInsertion();

        if (parent.left == null) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        newNode.parent = parent;
        size++;

        // Restaurar la propiedad del heap (burbuja hacia arriba)
        bubbleUp(newNode);
    }

    // Encuentra el padre donde se debe insertar el siguiente nodo
    private HeapNode findParentForInsertion() {
        // Convertir tamaño + 1 a binario para encontrar la ruta
        int nextPosition = size + 1;
        String binary = Integer.toBinaryString(nextPosition);

        // Remover el primer bit (que siempre es 1)
        binary = binary.substring(1);

        HeapNode current = root;

        // Navegar hasta el padre del nuevo nodo
        for (int i = 0; i < binary.length() - 1; i++) {
            if (binary.charAt(i) == '0') {
                if (current.left == null) {
                    current.left = new HeapNode(null);
                    current.left.parent = current;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new HeapNode(null);
                    current.right.parent = current;
                }
                current = current.right;
            }
        }

        return current;
    }

    // Burbuja hacia arriba para mantener la propiedad del max heap
    private void bubbleUp(HeapNode node) {
        while (node.parent != null && compare(node.value, node.parent.value) > 0) {
            // Intercambiar valores
            T temp = node.value;
            node.value = node.parent.value;
            node.parent.value = temp;
            node = node.parent;
        }
    }

    // Burbuja hacia abajo para mantener la propiedad del max heap
    private void bubbleDown(HeapNode node) {
        while (node != null) {
            HeapNode largest = node;

            if (node.left != null &&
                    compare(node.left.value, largest.value) > 0) {
                largest = node.left;
            }

            if (node.right != null &&
                    compare(node.right.value, largest.value) > 0) {
                largest = node.right;
            }

            if (largest == node) break; // Ya está en posición correcta

            // Intercambiar valores
            T temp = node.value;
            node.value = largest.value;
            largest.value = temp;
            node = largest;
        }
    }

    // Eliminar el elemento máximo (raíz)
    public T removeMax() {
        if (root == null) {
            System.out.println("El montículo está vacío, no se puede eliminar.");
            return null;
        }

        T maxValue = root.value;

        if (size == 1) {
            root = null;
            size = 0;
            return maxValue;
        }

        // Encontrar el último nodo
        HeapNode last = findLastNode();

        // Mover el valor del último nodo a la raíz
        root.value = last.value;

        // Eliminar el último nodo
        if (last.parent != null) {
            if (last.parent.left == last) {
                last.parent.left = null;
            } else {
                last.parent.right = null;
            }
        }

        size--;

        // Restaurar la propiedad del heap
        bubbleDown(root);

        return maxValue;
    }

    // Encuentra el último nodo insertado
    private HeapNode findLastNode() {
        if (size <= 1) return root;

        // Convertir tamaño a binario para encontrar la ruta
        String binary = Integer.toBinaryString(size);
        binary = binary.substring(1); // Remover el primer bit

        HeapNode current = root;

        // Seguir la ruta binaria
        for (char bit : binary.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return current;
    }

    // Obtener el máximo sin eliminarlo, equivalente a un .peek()
    public T getMax() {
        return root != null ? root.value : null;
    }

    // Verificar si el heap está vacío
    public boolean isEmpty() {
        return root == null;
    }

    // Obtener el tamaño del heap
    public int getSize() {
        return size;
    }

    // Mostrar el heap como una lista (representación por niveles)
    // Representación en string del heap
    @Override
    public String toString() {
        if (root == null)
            return "MaxHeap: []\nCantidad de elementos: 0 | Elemento Maximo: null";

        List<T> elements = new ArrayList<>();
        collectElementsByLevel(elements);

        StringBuilder sb = new StringBuilder();
        sb.append("MaxHeap:\n");
        sb.append("[\n");

        for (int i = 0; i < elements.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(elements.get(i));
            if (i < elements.size() - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }

        sb.append("]\n");
        sb.append("Cantidad de elementos: ").append(size);
        sb.append(" | Elemento Maximo: ").append(root.value);

        return sb.toString();
    }


    // Recolecta los elementos en orden por niveles (breadth-first)
    private void collectElementsByLevel(List<T> elements) {
        if (root == null) return;

        List<HeapNode> queue = new ArrayList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            HeapNode current = queue.removeFirst();
            elements.add(current.value);

            if (current.left != null) {
                queue.add(current.left);
            }
            if (current.right != null) {
                queue.add(current.right);
            }
        }
    }
}