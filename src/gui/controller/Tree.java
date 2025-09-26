package gui.controller;

import Core.Utils.BinaryTree;
import Core.Utils.Cell;
import Core.Utils.NodeCell;
import Core.Utils.NodePosition;
import Core.Utils.SelfBalancingBST;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Tooltip;

/**
 * Clase Tree - Responsable de la visualización y animación de árboles binarios
 *
 * Esta clase proporciona funcionalidades para dibujar árboles AVL y animar
 * recorridos por niveles con efectos visuales interactivos.
 *
 * @author Kevin
 */
public class Tree {

    // ==================== CONSTANTES DE COLOR ====================
    private final Color NORMAL_COLOR = Color.LIGHTBLUE;        // Color azul claro para nodos en estado normal
    private final Color HOVER_COLOR = Color.RED;               // Color rojo para nodos al hacer hover
    private final Color NORMAL_ARISTA = Color.BLACK;           // Color negro para aristas en estado normal
    private final Color HOVER_ARISTA = Color.RED;              // Color rojo para aristas al hacer hover

    // ==================== VARIABLES DE INSTANCIA ====================
    private final Group group;                                 // Contenedor principal donde se dibuja el árbol completo
    private SelfBalancingBST arbolAVL;

    /**
     * Mapeo que relaciona cada nodo lógico (NodeCell) con su representación
     * visual (Circle) Permite acceder rápidamente a los círculos durante las
     * animaciones
     */
    private Map<NodeCell, Circle> mapaNodoCirculo = new HashMap<>();

    private boolean animacionEnCurso = false;                  // Bandera que indica si hay una animación activa

    /**
     * Lista que almacena todas las líneas (aristas) dibujadas entre nodos Se
     * utiliza para aplicar efectos de hover y restaurar estados
     */
    private List<Line> aristas = new ArrayList<>();

    /**
     * Constructor de la clase Tree
     *
     * @param group Grupo JavaFX donde se realizará el dibujo del árbol
     */
    public Tree(Group group, SelfBalancingBST arbolAVL) {
        this.group = group;
        this.arbolAVL = arbolAVL;
    }

    // ==================== MÉTODOS PRINCIPALES ====================
    /**
     * Inicia la animación del recorrido por niveles del árbol
     *
     * @param tree Árbol binario a animar
     */
    public void animarRecorridoPorNiveles(BinaryTree tree) {
        if (tree == null || tree.root == null || animacionEnCurso) {
            return;
        }

        List<List<NodeCell>> niveles = obtenerNodosPorNivel(tree.root);
        animarNodosSecuencialmente(niveles);
    }

    /**
     * Dibuja el árbol binario completo en el grupo JavaFX
     *
     * @param tree Árbol binario a dibujar
     */
    public void drawTree(BinaryTree tree) {
        group.getChildren().clear();
        aristas.clear();
        mapaNodoCirculo.clear();

        if (tree == null || tree.root == null) {
            return;
        }

        double width = 1920 * 10;
        double height = 1080 * 10;
        int heightTree = tree.getHeight(tree.root);
        double yStep = height / (heightTree + 1);

        List<NodePosition> positions = new ArrayList<>();
        computePositions(tree.root, 1, 0, width, yStep, positions);

        dibujarAristas(positions);
        dibujarNodos(positions);
    }

    /**
     * Restaura todos los nodos y aristas a sus colores normales
     */
    public void restaurarColores() {
        for (Circle circle : mapaNodoCirculo.values()) {
            if (circle != null) {
                circle.setFill(NORMAL_COLOR);
            }
        }
        for (Line arista : aristas) {
            if (arista != null) {
                arista.setStroke(NORMAL_ARISTA);
                arista.setStrokeWidth(1);
            }
        }
    }

    // ==================== MÉTODOS PRIVADOS DE ANIMACIÓN ====================
    /**
     * Obtiene los nodos del árbol organizados por niveles
     *
     * @param root Nodo raíz del árbol
     * @return Lista de listas, donde cada lista contiene los nodos de un nivel
     */
    private List<List<NodeCell>> obtenerNodosPorNivel(NodeCell root) {
        List<List<NodeCell>> niveles = new ArrayList<>();
        obtenerNodosPorNivelRecursivo(root, 0, niveles);
        return niveles;
    }

    /**
     * Método recursivo auxiliar para obtener nodos por nivel
     */
    private void obtenerNodosPorNivelRecursivo(NodeCell node, int nivel, List<List<NodeCell>> niveles) {
        if (node == null) {
            return;
        }

        if (niveles.size() <= nivel) {
            niveles.add(new ArrayList<>());
        }
        niveles.get(nivel).add(node);

        obtenerNodosPorNivelRecursivo(node.left, nivel + 1, niveles);
        obtenerNodosPorNivelRecursivo(node.right, nivel + 1, niveles);
    }

    /**
     * Realiza la animación secuencial de nodos por niveles
     *
     * @param niveles Lista de nodos organizados por niveles
     */
    private void animarNodosSecuencialmente(List<List<NodeCell>> niveles) {
        if (animacionEnCurso) {
            return;
        }
        animacionEnCurso = true;

        SequentialTransition secuencia = new SequentialTransition();

        // Configuración de tiempos de animación
        final double TIEMPO_ENTRE_NODOS = 0.3;

        for (int i = 0; i < niveles.size(); i++) {
            List<NodeCell> nodosNivel = niveles.get(i);

            for (int j = 0; j < nodosNivel.size(); j++) {
                final NodeCell nodoActual = nodosNivel.get(j);
                final int nivelActual = i;

                // Delay constante entre cada nodo
                PauseTransition delay = new PauseTransition(Duration.seconds(TIEMPO_ENTRE_NODOS));
                delay.setOnFinished(e -> {
                    resaltarNodoIndividual(nodoActual, nivelActual);
                });

                secuencia.getChildren().add(delay);
            }

            // Delay adicional entre niveles
            if (i < niveles.size() - 1) {
                PauseTransition pausaNivel = new PauseTransition(Duration.seconds(TIEMPO_ENTRE_NODOS * 2));
                secuencia.getChildren().add(pausaNivel);
            }
        }

        // Configuración de la transición final
        PauseTransition pausaFinal = new PauseTransition(Duration.seconds(0.5));
        pausaFinal.setOnFinished(e -> {
            restaurarColores();
            animacionEnCurso = false;
        });
        secuencia.getChildren().add(pausaFinal);

        secuencia.play();
    }

    /**
     * Resalta un nodo individual durante la animación
     */
    private void resaltarNodoIndividual(NodeCell nodo, int nivel) {
        restaurarColores();
        Color colorNivel = obtenerColorParaNivel(nivel);
        resaltarNodo(nodo, colorNivel);
    }

    /**
     * Asigna un color específico para cada nivel del árbol
     *
     * @param nivel Nivel del árbol (0-based)
     * @return Color asignado al nivel
     */
    private Color obtenerColorParaNivel(int nivel) {
        return Color.RED; // Todos los niveles usan color rojo por simplicidad
    }

    /**
     * Resalta un nodo específico con el color dado
     */
    private void resaltarNodo(NodeCell nodo, Color color) {
        Circle circle = mapaNodoCirculo.get(nodo);
        if (circle != null) {
            circle.setFill(color);
        }
    }

    // ==================== MÉTODOS PRIVADOS DE DIBUJO ====================
    /**
     * Dibuja las aristas (conexiones) entre nodos del árbol
     */
    private void dibujarAristas(List<NodePosition> positions) {
        for (NodePosition np : positions) {
            if (np == null || np.node == null) {
                continue;
            }

            // Dibujar arista izquierda
            if (np.node.left != null) {
                NodePosition leftPos = findPos(positions, np.node.left);
                if (leftPos != null) {
                    Line line = crearArista(np.x, np.y, leftPos.x, leftPos.y);
                    group.getChildren().add(line);
                    aristas.add(line);
                }
            }

            // Dibujar arista derecha
            if (np.node.right != null) {
                NodePosition rightPos = findPos(positions, np.node.right);
                if (rightPos != null) {
                    Line line = crearArista(np.x, np.y, rightPos.x, rightPos.y);
                    group.getChildren().add(line);
                    aristas.add(line);
                }
            }
        }
    }

    /**
     * Dibuja los nodos del árbol con sus etiquetas
     */
    private void dibujarNodos(List<NodePosition> positions) {
        for (NodePosition np : positions) {
            if (np == null || np.node == null || np.node.cell == null) {
                continue;
            }

            Circle circle = new Circle(np.x, np.y, 25, NORMAL_COLOR);
            Text text = new Text(np.x - 10, np.y + 4, np.node.cell.ISO3);

            configurarEventosNodo(circle, text, np.node, positions);

            group.getChildren().addAll(circle, text);
            mapaNodoCirculo.put(np.node, circle);
        }
    }

    /**
     * Crea una arista con efectos de hover
     */
    private Line crearArista(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(NORMAL_ARISTA);

        // Configurar efectos de hover
        line.setOnMouseEntered(e -> {
            line.setStroke(HOVER_ARISTA);
            line.setStrokeWidth(3);
        });
        line.setOnMouseExited(e -> {
            line.setStroke(NORMAL_ARISTA);
            line.setStrokeWidth(1);
        });

        return line;
    }

    /**
     * Configura los eventos de hover para nodos
     */
    private void configurarEventosNodo(Circle circle, Text text, NodeCell nodo, List<NodePosition> positions) {
        // Crear tooltip
        Tooltip tooltip = new Tooltip();

        tooltip.setShowDelay(javafx.util.Duration.millis(100));    // Aparece después de 100ms (en lugar de 1s)
        tooltip.setHideDelay(javafx.util.Duration.millis(200));    // Se oculta después de 200ms
        tooltip.setShowDuration(javafx.util.Duration.seconds(30));

        tooltip.setStyle("-fx-font-size: 12px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        tooltip.setMaxWidth(300);
        tooltip.setWrapText(true);

        circle.setOnMouseEntered(e -> {
            circle.setFill(HOVER_COLOR);
            resaltarAristasConectadas(nodo, positions);

            // Generar texto del tooltip con información del nodo
            String tooltipText = generarTooltipParaNodo(nodo);
            tooltip.setText(tooltipText);
            Tooltip.install(circle, tooltip);
        });

        circle.setOnMouseExited(e -> {
            circle.setFill(NORMAL_COLOR);
            restaurarAristas();
            Tooltip.uninstall(circle, tooltip);
        });

        // Los textos heredan los eventos de sus círculos
        text.setOnMouseEntered(e -> circle.getOnMouseEntered().handle(e));
        text.setOnMouseExited(e -> circle.getOnMouseExited().handle(e));
    }

    // ==================== MÉTODOS AUXILIARES ====================
    /**
     * Resalta las aristas conectadas a un nodo durante el hover
     */
    private void resaltarAristasConectadas(NodeCell nodo, List<NodePosition> positions) {
        if (nodo == null || positions == null) {
            return;
        }

        for (Line arista : aristas) {
            if (arista == null) {
                continue;
            }

            for (NodePosition np : positions) {
                if (np != null && np.node == nodo && estaConectada(arista, np.x, np.y)) {
                    arista.setStroke(HOVER_ARISTA);
                    arista.setStrokeWidth(3);
                }
            }
        }
    }

    /**
     * Verifica si una arista está conectada a una posición específica
     */
    private boolean estaConectada(Line arista, double x, double y) {
        return (Math.abs(arista.getStartX() - x) < 1 && Math.abs(arista.getStartY() - y) < 1)
                || (Math.abs(arista.getEndX() - x) < 1 && Math.abs(arista.getEndY() - y) < 1);
    }

    /**
     * Restaura las aristas a su estado normal
     */
    private void restaurarAristas() {
        for (Line arista : aristas) {
            if (arista != null) {
                arista.setStroke(NORMAL_ARISTA);
                arista.setStrokeWidth(1);
            }
        }
    }

    /**
     * Calcula las posiciones de los nodos usando algoritmo recursivo
     */
    private void computePositions(NodeCell node, int depth, double xMin, double xMax,
            double yStep, List<NodePosition> positions) {
        if (node == null) {
            return;
        }

        double xMid = (xMin + xMax) / 2.0;
        double y = depth * yStep;

        computePositions(node.left, depth + 1, xMin, xMid, yStep, positions);
        positions.add(new NodePosition(node, xMid, y));
        computePositions(node.right, depth + 1, xMid, xMax, yStep, positions);
    }

    /**
     * Encuentra la posición de un nodo específico en la lista
     */
    private NodePosition findPos(List<NodePosition> list, NodeCell target) {
        if (list == null || target == null) {
            return null;
        }

        for (NodePosition np : list) {
            if (np != null && np.node == target) {
                return np;
            }
        }
        return null;
    }

    /**
     * Genera el texto del tooltip para un nodo específico (versión
     * simplificada)
     */
    private String generarTooltipParaNodo(NodeCell nodo) {
        StringBuilder sb = new StringBuilder();

        if (nodo == null || nodo.cell == null) {
            return "Información no disponible";
        }

        Cell cell = nodo.cell;

        // Información básica del país
        sb.append("País: ").append(cell.Country).append("\n");
        sb.append("ISO: ").append(cell.ISO3).append("\n");
        sb.append("Temperatura promedio: ").append(String.format("%.4f", cell.FM)).append("°C\n");

        // Información del árbol AVL
        try {
            // Nivel del nodo
            int nivel = calcularNivelNodo(arbolAVL.root, cell.FM, 1);
            sb.append("Nivel: ").append(nivel).append("\n");

            // Factor de balance
            int balance = calcularBalanceNodo(nodo);
            sb.append("Balance: ").append(balance).append("\n");

            // Información de parentesco
            NodeCell padre = buscarPadre(arbolAVL.root, cell.FM);
            if (padre != null) {
                sb.append("Padre: ").append(padre.cell.ISO3).append("\n");

                NodeCell abuelo = buscarPadre(arbolAVL.root, padre.cell.FM);
                if (abuelo != null) {
                    sb.append("Abuelo: ").append(abuelo.cell.ISO3).append("\n");

                    // Buscar tío (hermano del padre)
                    NodeCell tio = null;
                    if (abuelo.left == padre) {
                        tio = abuelo.right;
                    } else {
                        tio = abuelo.left;
                    }
                    sb.append("Tío: ").append(tio != null ? tio.cell.ISO3 : "No tiene");
                } else {
                    sb.append("Abuelo: No tiene\n");
                    sb.append("Tío: No tiene");
                }
            } else {
                sb.append("Padre: Raíz\n");
                sb.append("Abuelo: No tiene\n");
                sb.append("Tío: No tiene");
            }

        } catch (Exception e) {
            sb.append("Información del árbol: No disponible");
        }

        return sb.toString();
    }

    private int calcularNivelNodo(NodeCell nodo, double valor, int nivelActual) {
        if (nodo == null) {
            return -1; // No encontrado
        }

        if (Math.abs(nodo.cell.FM - valor) < 0.0001) {
            return nivelActual;
        }

        if (valor < nodo.cell.FM) {
            return calcularNivelNodo(nodo.left, valor, nivelActual + 1);
        } else {
            return calcularNivelNodo(nodo.right, valor, nivelActual + 1);
        }
    }

    private int calcularBalanceNodo(NodeCell nodo) {
        if (nodo == null) {
            return 0;
        }

        int alturaIzquierda = arbolAVL.getHeight(nodo.left);
        int alturaDerecha = arbolAVL.getHeight(nodo.right);

        return alturaIzquierda - alturaDerecha;
    }

    private NodeCell buscarPadre(NodeCell actual, double valorBuscado) {
        return buscarPadreRecursivo(actual, valorBuscado, null);
    }

    private NodeCell buscarPadreRecursivo(NodeCell actual, double valorBuscado, NodeCell padre) {
        if (actual == null) {
            return null;
        }

        if (Math.abs(actual.cell.FM - valorBuscado) < 0.0001) {
            return padre;
        }

        NodeCell encontrado = buscarPadreRecursivo(actual.left, valorBuscado, actual);
        if (encontrado != null) {
            return encontrado;
        }

        return buscarPadreRecursivo(actual.right, valorBuscado, actual);
    }

    /**
     * Resalta un nodo específico en el árbol
     */
    public void resaltarNodoEspecifico(Cell nodo) {
        restaurarColores();

        for (Map.Entry<NodeCell, Circle> entry : mapaNodoCirculo.entrySet()) {
            if (entry.getKey().cell == nodo) {
                entry.getValue().setFill(Color.GOLD);
                break;
            }
        }
    }

}
