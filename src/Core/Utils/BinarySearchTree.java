/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Core.Utils;

/**
 * Binary Search Tree (BST) para trabajar con objetos Cell, ordenados de acuerdo
 * al valor promedio FM.
 *
 * Esta clase implementa inserción, eliminación y búsqueda mínima necesaria para
 * ser extendida por un AVL o ABB.
 *
 * @author Rashid
 */
public class BinarySearchTree extends BinaryTree {

    public BinarySearchTree() {
        this.root = null;
    }

    public BinarySearchTree(NodeCell root) {
        this.root = root;
    }

    /**
     * Inserta un nodo en el BST de acuerdo al valor FM. No se permiten
     * duplicados.
     *
     * @param cell objeto Cell a insertar
     */
    public void addNode(Cell cell) {
        if (this.root == null) {
            this.root = new NodeCell(cell);
        } else {
            this.root = recursiveInsertion(this.root, cell);
        }
    }

    private NodeCell recursiveInsertion(NodeCell root, Cell cell) {
        if (root == null) {
            return new NodeCell(cell);
        }

        if (cell.FM < root.cell.FM) {
            root.left = recursiveInsertion(root.left, cell);
        } else if (cell.FM > root.cell.FM) {
            root.right = recursiveInsertion(root.right, cell);
        }
        // Duplicados no se insertan (ignorar sin imprimir nada)

        return root;
    }

    /**
     * Elimina un nodo del árbol por su valor FM.
     *
     * @param FM valor a eliminar
     */
    public void deleteNode(double FM) {
        this.root = delete(this.root, FM);
    }

    private NodeCell delete(NodeCell root, double FM) {
        if (root == null) {
            return null;
        }

        if (FM < root.cell.FM) {
            root.left = delete(root.left, FM);
        } else if (FM > root.cell.FM) {
            root.right = delete(root.right, FM);
        } else {
            // Caso 1: sin hijos
            if (root.left == null && root.right == null) {
                return null;
            }
            // Caso 2: un solo hijo
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            // Caso 3: dos hijos -> reemplazar por sucesor
            NodeCell successor = findMin(root.right);
            root.cell = successor.cell;
            root.right = delete(root.right, successor.cell.FM);
        }
        return root;
    }

    /**
     * Encuentra el nodo con menor FM en el árbol.
     *
     * @param node nodo raíz del subárbol
     * @return el nodo con FM mínimo
     */
    protected NodeCell findMin(NodeCell node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
}
