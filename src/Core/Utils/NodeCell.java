/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*
 * Clase NodeCell: representa un nodo del Árbol AVL.
 * Cada nodo envuelve un objeto Cell y mantiene referencias a sus hijos
 * izquierdo y derecho, además de un factor de balanceo usado en el AVL.
 */
package Core.Utils;

/**
 * Nodo que se utiliza en el Árbol AVL. Contiene la información de un país
 * (Cell), enlaces a subárboles izquierdo y derecho, y el valor de balance
 * (altura relativa de los subárboles).
 *
 * @author Rashid
 */
public class NodeCell {

    // Atributos del nodo
    public Cell cell;       // Información principal (país)
    public NodeCell left;   // Hijo izquierdo
    public NodeCell right;  // Hijo derecho
    public int balance;     // Factor de balanceo (AVL)

    /**
     * Constructor para crear un nodo hoja.
     *
     * @param cell la información (Cell) que se guardará en el nodo
     */
    public NodeCell(Cell cell) {
        this.cell = cell;
        this.left = null;
        this.right = null;
        this.balance = 0; // Siempre inicia balanceado
    }

    /**
     * Constructor para crear un nodo con hijos explícitos.
     *
     * @param cell la información (Cell) que se guardará en el nodo
     * @param left referencia al hijo izquierdo
     * @param right referencia al hijo derecho
     */
    public NodeCell(Cell cell, NodeCell left, NodeCell right) {
        this.cell = cell;
        this.left = left;
        this.right = right;
        this.balance = 0; // Siempre inicia balanceado
    }
}
