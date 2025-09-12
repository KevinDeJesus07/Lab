/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Core.Utils;

/**
 * Clase que representa un árbol binario cuyos nodos son de tipo
 * {@link NodeCell}.
 *
 * En este laboratorio se usa principalmente como soporte para construir el
 * Árbol AVL. Incluye operaciones de búsqueda, recorrido por niveles,
 * similitud/igualdad entre árboles, y consultas útiles como altura,
 * cardinalidad y valores mínimo/máximo.
 *
 * NOTA: por decisión de diseño del profesor, todos los atributos de las
 * estructuras son públicos para evitar lógica adicional de encapsulamiento.
 *
 * @author Rashid
 */
public class BinaryTree {

    public NodeCell root; // nodo raíz del árbol

    // ==================== CONSTRUCTORES ====================
    public BinaryTree() {
        this.root = null;
    }

    public BinaryTree(NodeCell root) {
        this.root = root;
    }

    // ==================== ALTURA ====================
    /**
     * Muestra por consola la altura del árbol.
     */
    public void getHeight() {
        if (this.root == null) {
            System.out.println("The height of the tree is: 0");
        } else {
            System.out.println("The height of the tree is: " + getHeight(this.root));
        }
    }

    /**
     * Calcula recursivamente la altura de un subárbol.
     *
     * @param root nodo raíz del subárbol
     * @return altura entera
     */
    public int getHeight(NodeCell root) {
        if (root == null) {
            return -1;
        }
        return 1 + Math.max(getHeight(root.left), getHeight(root.right));
    }

    // ==================== RECORRIDOS ====================
    /**
     * Muestra el árbol por niveles de forma recursiva.
     */
    public void perLevelsTraversal() {
        if (root != null) {
            int height = getHeight(root);
            for (int level = 0; level < height; level++) {
                System.out.println("Nivel " + level + ": ");
                printLevel(root, level);
                System.out.println();
            }
        }
    }

    /**
     * Igual al anterior pero imprime solo el ISO3 de cada país.
     */
    public void perLevelsTraversalOnlyMot() {
        if (root != null) {
            int height = getHeight(root);
            for (int level = 0; level < height; level++) {
                System.out.println("Nivel " + level + ": ");
                printLevelOnlyMot(root, level);
                System.out.println();
            }
        }
    }

    // Helpers de impresión por niveles
    private void printLevel(NodeCell node, int level) {
        if (node == null) {
            return;
        }
        if (level == 0) {
            System.out.print(node.cell.toStringWithoutID() + " ");
        } else {
            printLevel(node.left, level - 1);
            printLevel(node.right, level - 1);
        }
    }

    private void printLevelOnlyMot(NodeCell node, int level) {
        if (node == null) {
            return;
        }
        if (level == 0) {
            System.out.print(node.cell.ISO3 + " ");
        } else {
            printLevelOnlyMot(node.left, level - 1);
            printLevelOnlyMot(node.right, level - 1);
        }
    }

    // ==================== OPERACIONES SOBRE NODOS ====================
    /**
     * Busca un nodo con un FM específico e imprime información detallada:
     * nivel, balance, padre, abuelo y tío.
     */
    /**
     * Busca un nodo con un FM específico e imprime información detallada:
     * nivel, balance, padre, abuelo y tío.
     */
    public void searchNode(double FM) {
        NodeCell node = search(FM); // usamos búsqueda por BST
        if (node != null) {
            System.out.println("Node with FM=" + FM + " exists");
            showNodeInfo(FM);
        } else {
            System.out.println("Node with FM=" + FM + " doesn't exist");
        }
    }

    /**
     * Búsqueda principal en el árbol (tipo AVL/BST). Recorre hacia izquierda o
     * derecha según el valor de FM.
     */
    public NodeCell search(double key) {
        return searchRecursive(root, key);
    }

    private NodeCell searchRecursive(NodeCell current, double key) {
        if (current == null || current.cell.FM == key) {
            return current;
        }
        if (key < current.cell.FM) {
            return searchRecursive(current.left, key);
        } else {
            return searchRecursive(current.right, key);
        }
    }

    // ==================== CONSULTAS GENERALES ====================
    public void getCardinality() {
        System.out.println("The amount of nodes in the tree is: " + amountOfNodes(this.root));
    }

    public int getCard() {
        return amountOfNodes(this.root);
    }

    private int amountOfNodes(NodeCell root) {
        if (root == null) {
            return 0;
        }
        return 1 + amountOfNodes(root.left) + amountOfNodes(root.right);
    }

    public void sumOfElements() {
        System.out.println("The sum of all FM values in the tree is: " + sumOfElementsValue());
    }

    public double sumOfElementsValue() {
        return recursiveSum(this.root);
    }

    private double recursiveSum(NodeCell root) {
        if (root == null) {
            return 0.0;
        }
        return root.cell.FM + recursiveSum(root.left) + recursiveSum(root.right);
    }

    // ==================== UTILIDADES ====================
    private int getBalance(NodeCell node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.left) - getHeight(node.right);
    }

    private int getLevel(NodeCell current, NodeCell target, int level) {
        if (current == null) {
            return -1;
        }
        if (current == target) {
            return level;
        }

        int left = getLevel(current.left, target, level + 1);
        if (left != -1) {
            return left;
        }

        return getLevel(current.right, target, level + 1);
    }

    private NodeCell getParent(NodeCell current, NodeCell target) {
        if (current == null || current == target) {
            return null;
        }
        if (current.left == target || current.right == target) {
            return current;
        }

        if (target.cell.FM < current.cell.FM) {
            return getParent(current.left, target);
        } else {
            return getParent(current.right, target);
        }
    }

    // ==================== INFO DEL NODO ====================
    public void showNodeInfo(double key) {
        NodeCell node = search(key);

        System.out.println("Información del nodo con clave: " + key);

        // Nivel del nodo
        int level = getLevel(this.root, node, 0);
        System.out.println("Nivel: " + level);

        // Factor de equilibrio
        int balance = getBalance(node);
        System.out.println("Factor de equilibrio: " + balance);

        // Padre
        NodeCell parent = getParent(root, node);
        if (parent != null) {
            System.out.println("Padre: " + parent.cell.toStringWithoutID());
        } else {
            System.out.println("Padre: (no tiene, es la raíz)");
        }

        // Abuelo
        NodeCell grandparent = (parent != null) ? getParent(root, parent) : null;
        if (grandparent != null) {
            System.out.println("Abuelo: " + grandparent.cell.toStringWithoutID());
        } else {
            System.out.println("Abuelo: (no tiene)");
        }

        // Tío (hermano del padre)
        if (grandparent != null) {
            if (grandparent.left != null && grandparent.left != parent) {
                System.out.println("Tío: " + grandparent.left.cell.toStringWithoutID());
            } else if (grandparent.right != null && grandparent.right != parent) {
                System.out.println("Tío: " + grandparent.right.cell.toStringWithoutID());
            } else {
                System.out.println("Tío: (no tiene)");
            }
        } else {
            System.out.println("Tío: (no tiene)");
        }
    }
}
