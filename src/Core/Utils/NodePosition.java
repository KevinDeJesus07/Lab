package Core.Utils;

/**
 * Clase auxiliar para almacenar la posición calculada de un nodo
 * del árbol binario en la escena de JavaFX.
 *
 * Sirve para asociar un {@link NodeCell} con las coordenadas
 * (x, y) en las que debe dibujarse.
 *
 * Ejemplo de uso:
 *   NodePosition pos = new NodePosition(node, 100, 200);
 *   double x = pos.x;
 *   double y = pos.y;
 */
public class NodePosition {

    /** Nodo real del árbol que se va a representar. */
    public final NodeCell node;

    /** Coordenada X en píxeles dentro del contenedor de dibujo. */
    public final double x;

    /** Coordenada Y en píxeles dentro del contenedor de dibujo. */
    public final double y;

    /**
     * Constructor para crear una posición de nodo.
     *
     * @param node Nodo del árbol (no nulo).
     * @param x    Posición horizontal en píxeles.
     * @param y    Posición vertical en píxeles.
     */
    public NodePosition(NodeCell node, double x, double y) {
        this.node = node;
        this.x = x;
        this.y = y;
    }
}
