/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Core.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de un Árbol Binario de Búsqueda Auto-balanceado (AVL).
 *
 * Los nodos son de tipo {@link NodeCell}, que encapsulan una {@link Cell} y
 * enlaces a sus hijos.
 *
 * Este ABB mantiene balance mediante rotaciones simples y dobles, garantizando
 * operaciones de inserción, eliminación y búsqueda en O(log n).
 *
 * NOTA: por decisión del profesor, todos los atributos de las estructuras son
 * públicos para evitar encapsulamiento adicional.
 *
 * @author Rashid
 */
public class SelfBalancingBST extends BinarySearchTree {

    // ==================== CONSTRUCTORES ====================
    public SelfBalancingBST() {
        this.root = null;
    }

    public SelfBalancingBST(NodeCell root) {
        this.root = root;
    }

    // ==================== UTILIDADES DE BALANCE ====================
    /**
     * Calcula la altura de un subárbol.
     *
     * Convención: si el nodo es null, su altura es 0.
     */
    protected int getHeight(NodeCell node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    /**
     * Obtiene el factor de balance de un nodo.
     *
     * Balance = altura(hijo_izq) - altura(hijo_der)
     */
    private int getBalance(NodeCell node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.left) - getHeight(node.right);
    }

    // ==================== ROTACIONES ====================
    private NodeCell simpleLeftRotation(NodeCell node) {
        NodeCell aux = node.right;
        node.right = aux.left;
        aux.left = node;
        return aux;
    }

    private NodeCell simpleRightRotation(NodeCell node) {
        NodeCell aux = node.left;
        node.left = aux.right;
        aux.right = node;
        return aux;
    }

    private NodeCell doubleLeftRightRotation(NodeCell node) {
        node.left = simpleLeftRotation(node.left);
        return simpleRightRotation(node);
    }

    private NodeCell doubleRightLeftRotation(NodeCell node) {
        node.right = simpleRightRotation(node.right);
        return simpleLeftRotation(node);
    }

    // ==================== INSERCIÓN ====================
    /**
     * Inserta un nuevo nodo con la celda dada en el ABB.
     *
     * Si el FM ya existe, se ignora la inserción.
     */
    @Override
    public void addNode(Cell cell) {
        this.root = insertNode(this.root, cell);
    }

    private NodeCell insertNode(NodeCell root, Cell cell) {
        if (root == null) {
            return new NodeCell(cell);
        }

        // Inserción como BST normal
        if (cell.FM < root.cell.FM) {
            root.left = insertNode(root.left, cell);
        } else if (cell.FM > root.cell.FM) {
            root.right = insertNode(root.right, cell);
        } else {
            System.out.println("Duplicated FM, " + cell.FM + " not inserted");
            return root; // clave duplicada → no insertamos
        }

        // Balanceo AVL
        int balance = getBalance(root);

        // Caso 1: Left-Left
        if (balance > 1 && cell.FM < root.left.cell.FM) {
            return simpleRightRotation(root);
        }
        // Caso 2: Right-Right
        if (balance < -1 && cell.FM > root.right.cell.FM) {
            return simpleLeftRotation(root);
        }
        // Caso 3: Left-Right
        if (balance > 1 && cell.FM > root.left.cell.FM) {
            return doubleLeftRightRotation(root);
        }
        // Caso 4: Right-Left
        if (balance < -1 && cell.FM < root.right.cell.FM) {
            return doubleRightLeftRotation(root);
        }

        return root; // nodo ya balanceado
    }

    // ==================== ELIMINACIÓN ====================
    /**
     * Elimina un nodo por clave (FM). Si no existe, muestra mensaje de error.
     */
    public void deleteNode(double FM) {
        if (search(FM) != null) {
            this.root = deleteNode(this.root, FM);
        } else {
            System.out.println("El dato no existe");
        }
    }

    private NodeCell deleteNode(NodeCell root, double FM) {
        if (root == null) {
            return null;
        }

        // Eliminación como BST normal
        if (FM < root.cell.FM) {
            root.left = deleteNode(root.left, FM);
        } else if (FM > root.cell.FM) {
            root.right = deleteNode(root.right, FM);
        } else {
            // Nodo encontrado
            if (root.left == null || root.right == null) {
                root = (root.left != null) ? root.left : root.right;
            } else {
                NodeCell successor = findMin(root.right);
                root.cell = successor.cell;
                root.right = deleteNode(root.right, successor.cell.FM);
            }
        }

        if (root == null) {
            return null;
        }

        // Rebalanceo AVL
        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0) {
            return simpleRightRotation(root); // Left-Left
        }
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = simpleLeftRotation(root.left); // Left-Right
            return simpleRightRotation(root);
        }
        if (balance < -1 && getBalance(root.right) <= 0) {
            return simpleLeftRotation(root); // Right-Right
        }
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = simpleRightRotation(root.right); // Right-Left
            return simpleLeftRotation(root);
        }

        return root;
    }

    // ==================== RECORRIDO INORDEN ====================
    /**
     * Retorna una lista de celdas ordenadas por FM (inorden).
     */
    public List<Cell> inOrderAdding() {
        List<Cell> result = new ArrayList<>();
        inOrderRecursive(root, result);
        return result;
    }

    private void inOrderRecursive(NodeCell node, List<Cell> result) {
        if (node != null) {
            inOrderRecursive(node.left, result);
            result.add(node.cell);
            inOrderRecursive(node.right, result);
        }
    }
}
