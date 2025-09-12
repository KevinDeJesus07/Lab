/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

 /*
 * Clase Cell: representa la información básica de un país y su variación de temperatura.
 * Es la unidad de almacenamiento que se inserta dentro del Árbol AVL.
 */
package Core.Utils;

/**
 * Cada objeto de tipo Cell contiene: - Identificador (usado solo para
 * depuración o visualización inicial). - Nombre del país y código ISO3. - Valor
 * FM (variación promedio de la temperatura). - (Opcional) Un arreglo con varias
 * mediciones F_i, para calcular promedios.
 *
 * @author Rashid
 */
public class Cell {

    // Atributos principales
    public int ObjectId;     // ID auxiliar, no se usa en el programa, solo para debug y impresión de la lista inicial de nodos de celda
    public String Country;   // Nombre del país
    public String ISO3;      // Código ISO de 3 letras
    public double FM;        // Variación promedio de la temperatura
    public double[] F_i;     /* (Opcional) conjunto de mediciones individuales, esto se usa por que es la información necesaria para
                                calcular FM, se podría ignorar, pero para mantenerlo entendible se deja */

    /**
     * Constructor principal para crear un registro de país.
     *
     * @param objectId Identificador (se ignora en cálculos, solo para
     * depuración).
     * @param Country Nombre del país.
     * @param ISO3 Código ISO3 del país.
     * @param FM Variación promedio de temperatura.
     */
    public Cell(int objectId, String Country, String ISO3, double FM) {
        this.ObjectId = objectId;
        this.Country = Country;
        this.ISO3 = ISO3;
        this.FM = FM;
    }

    /**
     * Calcula el promedio de las mediciones almacenadas en el arreglo F_i. Si
     * no hay datos cargados, retorna 0.
     *
     * @return promedio de las mediciones, o 0 si no existen datos.
     */
    public double averageTemperature() {
        if (F_i == null || F_i.length == 0) {
            return 0.0;
        }
        double sum = 0;
        for (double val : F_i) {
            sum += val;
        }
        return sum / F_i.length;
    }

    /**
     * Representación del objeto con ID (se usa al imprimir los registros del
     * CSV).
     */
    @Override
    public String toString() {
        return "ID: " + ObjectId
                + " | Country: " + Country
                + " | ISO3: " + ISO3
                + " | FM: " + FM;
    }

    /**
     * Representación del objeto sin ID (se usa en la mayoría de operaciones).
     */
    public String toStringWithoutID() {
        return "Country: " + Country
                + " | ISO3: " + ISO3
                + " | FM: " + FM;
    }
}
