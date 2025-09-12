/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*
 * Programa principal del laboratorio de Árboles AVL.
 * Este código carga registros desde un CSV, arma un árbol balanceado
 * y permite realizar operaciones básicas (insertar, eliminar, buscar, 
 * recorridos y búsquedas con criterios).
 */
package Main;

import Core.Utils.CSVReaderUtil;
import Core.Utils.SelfBalancingBST;
import Core.Utils.Cell;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Clase principal que contiene el menú interactivo para manipular un Árbol AVL
 * a partir de registros de variaciones de temperatura por país.
 *
 * @author Rashid
 */
public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // Uso Locale.US para forzar el separador decimal como '.' en lugar de ','
        in.useLocale(Locale.US);

        // Cargar nodos desde el archivo CSV
        System.out.println("Creando los nodos...");
        ArrayList<Cell> cells = CSVReaderUtil.readCSV("src/Main/data.csv");
        System.out.println("Nodos creados con éxito\n");

        // Mostrar nodos cargados antes de armar el árbol
        System.out.println("Mostrando los nodos antes de armar el árbol...\n");
        if (!cells.isEmpty()) {
            for (Cell c : cells) {
                System.out.println(c);
            }
        }

        // Crear el árbol AVL e insertar los registros
        System.out.println("\nCreando el árbol...\n");
        SelfBalancingBST ArbolInicial = new SelfBalancingBST();
        for (Cell c : cells) {
            ArbolInicial.addNode(c);
        }

        // Mostrar el árbol recién creado por niveles
        System.out.println("\nMostrando un recorrido por niveles...\n");
        ArbolInicial.perLevelsTraversal();

        // Iniciar el menú interactivo
        System.out.println("\nIniciando las funcionalidades...\n");
        String opcM = "n";
        int opcAcc;
        while (opcM.equalsIgnoreCase("N")) {
            System.out.println("ÁRBOL AVL LAB 1");
            System.out.println("1. Añadir un nodo");
            System.out.println("2. Eliminar un nodo");
            System.out.println("3. Buscar un nodo");
            System.out.println("4. Buscar un nodo con criterios adicionales");
            System.out.println("5. Realizar recorrido por niveles");
            System.out.println("6. Salir");
            System.out.print("Ingresa una opción: ");
            opcAcc = in.nextInt();

            switch (opcAcc) {
                case 1:
                    // --- Añadir un nodo ---
                    System.out.println("-- AÑADIR UN NODO --");
                    in.nextLine(); // limpiar el buffer de la línea anterior
                    System.out.print("Ingrese el nombre del país: ");
                    String country = in.nextLine(); // ahora sí acepta espacios
                    System.out.println(country);
                    System.out.print("Ingrese el acrónimo del país: ");
                    String ISO3 = in.next(); // acrónimo es corto, no necesita espacios
                    System.out.print("Ingrese el promedio de la variación de la temperatura del país: ");
                    double avgTempVar = in.nextDouble();
                    Cell cell = new Cell(0, country, ISO3, avgTempVar);
                    ArbolInicial.addNode(cell);
                    break;

                case 2:
                    // --- Eliminar un nodo ---
                    System.out.println("-- ELIMINAR UN NODO --");
                    System.out.print("Ingrese el promedio de la variación de la temperatura del país a eliminar: ");
                    avgTempVar = in.nextDouble();
                    ArbolInicial.deleteNode(avgTempVar);
                    break;

                case 3:
                    // --- Buscar un nodo ---
                    System.out.println("-- BUSCAR UN NODO --");
                    System.out.print("Ingrese el promedio de la variación de la temperatura del país a buscar: ");
                    avgTempVar = in.nextDouble();
                    ArbolInicial.searchNode(avgTempVar);
                    break;

                case 4:
                    // --- Búsqueda con criterios ---
                    int busNCC = 0;
                    while (busNCC != 4) {
                        System.out.println("-- BUSCAR UN NODO CON CRITERIO --");
                        System.out.println("1. Países mayores al promedio");
                        System.out.println("2. Países menores al promedio");
                        System.out.println("3. Países mayores o iguales a un dato");
                        System.out.println("4. Salir de la operación");
                        System.out.print("Ingresa una opción: ");
                        busNCC = in.nextInt();

                        switch (busNCC) {
                            case 1: {
                                double promedio = ArbolInicial.sumOfElementsValue() / ArbolInicial.getCard();
                                buscarConCriterio(ArbolInicial, in,
                                        "Promedio: " + promedio,
                                        c -> c.FM > promedio);
                                break;
                            }
                            case 2: {
                                double promedio = ArbolInicial.sumOfElementsValue() / ArbolInicial.getCard();
                                buscarConCriterio(ArbolInicial, in,
                                        "Promedio: " + promedio,
                                        c -> c.FM < promedio);
                                break;
                            }
                            case 3: {
                                System.out.print("Ingresa el dato base: ");
                                double dato = in.nextDouble();
                                buscarConCriterio(ArbolInicial, in,
                                        "Buscando nodos con valor mayor o igual a " + dato,
                                        c -> c.FM >= dato);
                                break;
                            }
                            case 4:
                                break;
                            default:
                                System.out.println("La opción no existe");
                        }
                    }
                    break;

                case 5:
                    // --- Recorrido por niveles (modo resumido) ---
                    System.out.println("-- MOSTRAR ÁRBOL POR NIVELES --");
                    ArbolInicial.perLevelsTraversalOnlyMot();
                    break;

                case 6:
                    // --- Salida del programa ---
                    System.out.println("-- ¿QUIERE SALIR? --");
                    System.out.print("Ingrese S o N: ");
                    opcM = in.next();
                    while (!(opcM.equalsIgnoreCase("N") || opcM.equalsIgnoreCase("S"))) {
                        System.out.println("OPCIÓN NO VÁLIDA");
                        System.out.print("Ingrese S o N: ");
                        opcM = in.next();
                    }
                    break;

                default:
                    System.out.println("La opción no existe");
                    break;
            }
        }
    }

    /**
     * Método auxiliar que encapsula la lógica de búsqueda con criterios. Aquí
     * evito repetir el mismo código en cada case del menú.
     *
     * @param arbol Árbol AVL donde se hace la búsqueda
     * @param in Scanner para leer selección del usuario
     * @param mensaje Mensaje inicial que se imprime antes de listar resultados
     * @param criterio Condición que debe cumplir el nodo para ser mostrado
     */
    private static void buscarConCriterio(SelfBalancingBST arbol, Scanner in,
            String mensaje, Predicate<Cell> criterio) {
        System.out.println(mensaje);

        List<Cell> encontrados = new ArrayList<>();
        int index = 1;

        // Recorrer el árbol en orden e ir filtrando según el criterio
        for (Cell c : arbol.inOrderAdding()) {
            if (criterio.test(c)) {
                System.out.println(index + ". " + c.toStringWithoutID());
                encontrados.add(c);
                index++;
            }
        }

        if (encontrados.isEmpty()) {
            System.out.println("No se encontraron nodos con este criterio.");
        } else {
            System.out.print("Seleccione el número del nodo para ver detalles (0 para cancelar): ");
            int seleccion = in.nextInt();
            if (seleccion > 0 && seleccion <= encontrados.size()) {
                Cell elegido = encontrados.get(seleccion - 1);
                arbol.searchNode(elegido.FM);
            }
        }
    }
}
