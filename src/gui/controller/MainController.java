package gui.controller;

import Core.Utils.CSVReaderUtil;
import Core.Utils.Cell;
import Core.Utils.NodeCell;
import Core.Utils.SelfBalancingBST;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class MainController {

    // ==================== COMPONENTES FXML ====================
    @FXML
    private Pane rootPane;                          // Panel raíz donde se dibuja el árbol
    @FXML
    private Group treeGroup;                        // Grupo que contiene todos los elementos del árbol

    @FXML
    private Button insertarButton;                  // Botón para insertar nuevos nodos
    @FXML
    private Button eliminarButton;                  // Botón para eliminar nodos

    @FXML
    private TabPane mainTabPane;                    // Contenedor de pestañas principal
    @FXML
    private Tab visualizationTab;                   // Pestaña de visualización del árbol
    @FXML
    private Tab insertTab;                          // Pestaña de inserción
    @FXML
    private Tab deleteTab;                          // Pestaña de eliminación  
    @FXML
    private Tab searchTab;                          // Pestaña de búsqueda
    @FXML
    private Label debugLabel;                       // Etiqueta para información de depuración

    // Campos del formulario de inserción
    @FXML
    private TextField nombreField;                  // Campo para nombre del país
    @FXML
    private TextField isoField;                     // Campo para código ISO (3 letras)
    @FXML
    private TextField temperaturaField;             // Campo para temperatura
    @FXML
    private Label mensajeLabel;                     // Etiqueta para mensajes de inserción

    // Campos del formulario de eliminación  
    @FXML
    private TextField temperaturaEliminarField;     // Campo para temperatura a eliminar
    @FXML
    private Label mensajeEliminarLabel;             // Etiqueta para mensajes de eliminación

    // Componentes de búsqueda
    @FXML
    private ComboBox<String> tipoBusquedaComboBox;  // Selector de tipo de búsqueda
    @FXML
    private VBox parametroBusquedaBox;              // Contenedor para parámetros de búsqueda
    @FXML
    private TextField valorBusquedaField;           // Campo para valor de búsqueda
    @FXML
    private Label mensajeBusquedaLabel;             // Etiqueta para mensajes de búsqueda
    @FXML
    private VBox resultadosBox;                     // Contenedor para resultados de búsqueda
    @FXML
    private FlowPane resultadosFlowPane;            // Panel flow para mostrar resultados

    @FXML
    private VBox temperaturaContainer;

    @FXML
    private TextField temperaturaBuscarField;
    @FXML
    private VBox resultadoBusquedaBox;
    @FXML
    private VBox infoNodoBox;
    @FXML
    private Label mensajeBuscarLabel;
    @FXML
    private Tab searchNodeTab;

    // ==================== PROPIEDADES Y VARIABLES ====================
    private final DoubleProperty scale = new SimpleDoubleProperty(1.0); // Propiedad observable para el zoom
    private double lastX, lastY;                          // Coordenadas anteriores para cálculo de arrastre

    private Tree tree;                                    // Instancia del árbol visual

    /**
     * Mapa que almacena promedios de temperatura por año Key: año, Value:
     * temperatura promedio
     */
    private Map<Integer, Double> promediosAnuales = new HashMap<>();

    private Label infoLabelFlotante;                      // Etiqueta flotante para información
    private Cell nodoEncontrado;

    /**
     * Propiedad observable para información de depuración Se vincula
     * automáticamente con la etiqueta debugLabel
     */
    private final StringProperty debugInfo = new SimpleStringProperty();

    // ==================== CONSTANTES DE CONFIGURACIÓN ====================
    private static final double INITIAL_SCALE = 0.1;      // Zoom inicial (10%)
    private static final double INITIAL_X = -8640;        // Posición X inicial del árbol
    private static final double INITIAL_Y = -4940;        // Posición Y inicial del árbol

    // ==================== COMPONENTES DEL ÁRBOL ====================
    private SelfBalancingBST arbolAVL;                    // Árbol AVL con la lógica de negocio

    private Popup popupInfo;                              // Ventana emergente para tooltips
    private Label popupLabel;                             // Etiqueta dentro del popup

    private boolean modoRecorridoActivo = false;          // Flag para controlar modo de recorrido

    /**
     * Mapeo de nodos a círculos visuales (no utilizado actualmente) Permite
     * relacionar nodos lógicos con su representación gráfica
     */
    private Map<NodeCell, Circle> nodosCirculos = new HashMap<>();

    /**
     * Mapeo de aristas originales (no utilizado actualmente) Podría usarse para
     * restaurar estado original después de efectos
     */
    private Map<Line, Line> aristasOriginales = new HashMap<>();

    @FXML
    private void initialize() {
        Tooltip tooltip = new Tooltip();
        tooltip.setStyle("-fx-background-color: yellow; -fx-text-fill: black; -fx-font-size: 14px;");

        // Configurar valores iniciales
        scale.set(INITIAL_SCALE);
        treeGroup.setTranslateX(INITIAL_X);
        treeGroup.setTranslateY(INITIAL_Y);

        treeGroup.scaleXProperty().bind(scale);
        treeGroup.scaleYProperty().bind(scale);

        // Configurar el binding automático para debug
        setupDebugBinding();

        // Zoom con rueda del mouse
        rootPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            double factor = e.getDeltaY() > 0 ? 1.1 : 0.9; // Factor de zoom: 1.1 para acercar, 0.9 para alejar
            double newScale = Math.max(0.1, Math.min(1.0, scale.get() * factor)); // Limitar zoom entre 10% y 100%
            scale.set(newScale);
            applyDragLimits(treeGroup.getTranslateX(), treeGroup.getTranslateY());
            updateDebugPosition();
        });

        // Arrastre del árbol
        rootPane.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            lastX = e.getSceneX(); // Guardar posición inicial X
            lastY = e.getSceneY(); // Guardar posición inicial Y
        });

        rootPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            double newX = treeGroup.getTranslateX() + e.getSceneX() - lastX; // Calcular nueva posición X
            double newY = treeGroup.getTranslateY() + e.getSceneY() - lastY; // Calcular nueva posición Y

            // Aplicar límites al arrastre
            applyDragLimits(newX, newY);

            lastX = e.getSceneX(); // Actualizar última posición X
            lastY = e.getSceneY(); // Actualizar última posición Y

            // Actualizar información de debug después de arrastrar
            updateDebugPosition();
        });

        // Configurar combo box de búsqueda
        if (tipoBusquedaComboBox != null) {
            tipoBusquedaComboBox.getItems().addAll(
                    "Temperatura mayor al promedio anual",
                    "Temperatura menor al promedio global",
                    "Temperatura mayor o igual a valor"
            );

            // Aplicar estilo al ComboBox principal
            tipoBusquedaComboBox.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #4682B4; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 8px; -fx-font-size: 14px;");

            tipoBusquedaComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                configurarParametrosBusqueda(newVal);
                if (resultadosBox != null) {
                    resultadosBox.setVisible(false); // Ocultar resultados anteriores
                }
            });
        }

        // Aplicar estilos a los botones principales
        if (insertarButton != null) {
            aplicarEstiloBotonPrincipal(insertarButton, "#2E8B57", "#27AE60"); // Verde para inserción
        }

        if (eliminarButton != null) {
            aplicarEstiloBotonPrincipal(eliminarButton, "#DC143C", "#FF4757"); // Rojo para eliminación
        }

        generarCamposTemperatura();

        arbolAVL = new SelfBalancingBST(); // Inicializar árbol AVL lógico

        tree = new Tree(treeGroup, arbolAVL); // Inicializar árbol visual

        // Cargar datos iniciales desde CSV
        ArrayList<Cell> cells = CSVReaderUtil.readCSV("src/Main/data.csv");
        for (Cell c : cells) {
            arbolAVL.addNode(c); // Insertar cada celda en el árbol
        }
        tree.drawTree(arbolAVL); // Dibujar árbol inicial

        inicializarPromedios();
    }

    // === MÉTODO PARA MOSTRAR LA PESTAÑA ===
    @FXML
    private void showSearchNodeTab() {
        mainTabPane.getSelectionModel().select(searchNodeTab);
        temperaturaBuscarField.clear();
        resultadoBusquedaBox.setVisible(false);
        mensajeBuscarLabel.setText("");
    }

// === MÉTODO DE BÚSQUEDA ===
    @FXML
    private void onSearchNode() {
        try {
            String tempStr = temperaturaBuscarField.getText().trim();

            if (tempStr.isEmpty()) {
                mostrarMensajeBuscar("Ingrese un valor de temperatura", true);
                resultadoBusquedaBox.setVisible(false);
                return;
            }

            double temperatura = Double.parseDouble(tempStr);
            if (temperatura < -2.0 || temperatura > 2.0) {
                mostrarMensajeBuscar("La temperatura debe estar entre -2.0 y 2.0", true);
                resultadoBusquedaBox.setVisible(false);
                return;
            }

            // Buscar el nodo
            nodoEncontrado = buscarNodoPorTemperatura(arbolAVL.root, temperatura);

            if (nodoEncontrado != null) {
                mostrarInformacionNodo(nodoEncontrado);
                resultadoBusquedaBox.setVisible(true);
                mostrarMensajeBuscar("✓ Nodo encontrado: " + nodoEncontrado.ISO3, false);
            } else {
                resultadoBusquedaBox.setVisible(false);
                mostrarMensajeBuscar("No se encontró ningún nodo con temperatura: " + temperatura, true);
            }

        } catch (NumberFormatException e) {
            mostrarMensajeBuscar("La temperatura debe ser un número válido", true);
            resultadoBusquedaBox.setVisible(false);
        }
    }

// === MÉTODO PARA RESALTAR EN EL ÁRBOL ===
    @FXML
    private void onHighlightNode() {
        if (nodoEncontrado != null) {
            mainTabPane.getSelectionModel().select(visualizationTab);
            tree.resaltarNodoEspecifico(nodoEncontrado);
            mostrarMensajeBuscar("Nodo " + nodoEncontrado.ISO3 + " resaltado en el árbol", false);
        }
    }

// === MÉTODO AUXILIAR PARA MOSTRAR INFORMACIÓN ===
    private void mostrarInformacionNodo(Cell nodo) {
        infoNodoBox.getChildren().clear();

        agregarLineaInformacion("País:", nodo.Country);
        agregarLineaInformacion("Código ISO:", nodo.ISO3);
        agregarLineaInformacion("Temperatura promedio:", String.format("%.4f°C", nodo.FM));

        try {
            int nivel = arbolAVL.getNodeLevel(nodo.FM);
            int balance = arbolAVL.getNodeBalance(nodo.FM);
            Cell padre = arbolAVL.getParentNode(nodo.FM);

            agregarLineaInformacion("Nivel en árbol:", String.valueOf(nivel));
            agregarLineaInformacion("Factor de balance:", String.valueOf(balance));
            agregarLineaInformacion("Padre:", padre != null ? padre.ISO3 + " (" + padre.Country + ")" : "Raíz");

        } catch (Exception e) {
            agregarLineaInformacion("Info árbol:", "No disponible");
        }
    }

    private void agregarLineaInformacion(String etiqueta, String valor) {
        HBox linea = new HBox(10);
        linea.setAlignment(Pos.CENTER_LEFT);

        Label lblEtiqueta = new Label(etiqueta);
        lblEtiqueta.setStyle("-fx-font-weight: bold; -fx-min-width: 150;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: #2C3E50;");

        linea.getChildren().addAll(lblEtiqueta, lblValor);
        infoNodoBox.getChildren().add(linea);
    }

    private void mostrarMensajeBuscar(String mensaje, boolean esError) {
        mensajeBuscarLabel.setText(mensaje);
        mensajeBuscarLabel.setStyle(esError
                ? "-fx-text-fill: #E74C3C; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
    }

    private void generarCamposTemperatura() {
        temperaturaContainer.getChildren().clear();

        for (int i = 0; i < 62; i++) {
            int year = 1961 + i;
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label yearLabel = new Label(String.valueOf(year) + ":");
            yearLabel.setPrefWidth(50);
            yearLabel.setStyle("-fx-font-weight: bold;");

            TextField tempField = new TextField();
            tempField.setPromptText("0.0");
            tempField.setPrefWidth(100);
            tempField.setId("temp_" + year); // ID único para cada campo

            row.getChildren().addAll(yearLabel, tempField);
            temperaturaContainer.getChildren().add(row);
        }
    }

    @FXML
    private void fillWithZeros() {
        for (int year = 1961; year <= 2022; year++) {
            TextField field = (TextField) temperaturaContainer.lookup("#temp_" + year);
            if (field != null) {
                field.setText("0.0");
            }
        }
    }

    @FXML
    private void fillWithSingleValue() {
        TextInputDialog dialog = new TextInputDialog("0.0");
        dialog.setTitle("Valor único");
        dialog.setHeaderText("Ingrese un valor para todos los años");
        dialog.setContentText("Valor:");
        dialog.setGraphic(null); // Elimina el ?

        // Icono personalizado
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        Image icon = new Image(getClass().getResourceAsStream("/images/tree_icon.png"));
        dialogStage.getIcons().add(icon);

        // Estilos botones
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #48bb78; -fx-text-fill: white;");
        dialogPane.lookupButton(ButtonType.CANCEL).setStyle("-fx-background-color: #f56565; -fx-text-fill: white;");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            for (int year = 1961; year <= 2022; year++) {
                TextField field = (TextField) temperaturaContainer.lookup("#temp_" + year);
                if (field != null) {
                    field.setText(value);
                }
            }
        });
    }

    /**
     * Calcula y almacena los promedios anuales reales basados en los datos del
     * árbol Usa los datos reales del CSV en lugar de valores de ejemplo
     */
    private void inicializarPromedios() {
        // Limpiar promedios anteriores
        promediosAnuales.clear();

        // Calcular promedios para cada año en el rango 1961-2022
        for (int año = 1961; año <= 2022; año++) {
            double promedioAnual = calcularPromedioAnual(año);
            promediosAnuales.put(año, promedioAnual);
        }
    }

    /**
     * Aplica límites al arrastre del árbol según el nivel de zoom Los límites
     * se interpolan entre zoom mínimo y máximo
     */
    private void applyDragLimits(double newX, double newY) {
        double currentScale = scale.get();

        // Límites para zoom máximo (100%)
        double xMinAtMaxZoom = -17350;
        double xMaxAtMaxZoom = 250;
        double yMinAtMaxZoom = -8900;
        double yMaxAtMaxZoom = -950;

        // Límites para zoom mínimo (10%) - posición fija
        double xMinAtMinZoom = -8640;
        double xMaxAtMinZoom = -8640;
        double yMinAtMinZoom = -4940;
        double yMaxAtMinZoom = -4940;

        // Interpolar límites según nivel de zoom actual
        double zoomFactor = (currentScale - 0.1) / (1.0 - 0.1);
        zoomFactor = Math.max(0, Math.min(1, zoomFactor)); // Asegurar factor entre 0 y 1

        // Calcular límites actuales interpolados
        double xMin = interpolate(xMinAtMinZoom, xMinAtMaxZoom, zoomFactor);
        double xMax = interpolate(xMaxAtMinZoom, xMaxAtMaxZoom, zoomFactor);
        double yMin = interpolate(yMinAtMinZoom, yMinAtMaxZoom, zoomFactor);
        double yMax = interpolate(yMaxAtMinZoom, yMaxAtMaxZoom, zoomFactor);

        // Aplicar límites
        double clampedX = Math.max(xMin, Math.min(xMax, newX));
        double clampedY = Math.max(yMin, Math.min(yMax, newY));

        treeGroup.setTranslateX(clampedX); // Establecer posición X limitada
        treeGroup.setTranslateY(clampedY); // Establecer posición Y limitada
    }

    /**
     * Interpola linealmente entre dos valores
     */
    private double interpolate(double start, double end, double factor) {
        return start + (end - start) * factor;
    }

    /**
     * Configura el binding entre la propiedad debugInfo y la etiqueta
     * debugLabel
     */
    private void setupDebugBinding() {
        if (debugLabel != null) {
            debugLabel.textProperty().bind(debugInfo); // Vincular propiedad con label
        }
        updateDebugInfo(); // Establecer valor inicial
    }

    private void updateDebugInfo() {
        debugInfo.set(String.format("Debug: escala=%.2f, x=%.1f, y=%.1f",
                scale.get(), treeGroup.getTranslateX(), treeGroup.getTranslateY()));
    }

    private void updateDebugPosition() {
        debugInfo.set(String.format("Debug: escala=%.2f, x=%.1f, y=%.1f",
                scale.get(), treeGroup.getTranslateX(), treeGroup.getTranslateY()));
    }

    // ==================== MÉTODOS DE NAVEGACIÓN ENTRE PESTAÑAS ====================
    @FXML
    private void showVisualizationTab() {
        mainTabPane.getSelectionModel().select(visualizationTab); // Cambiar a pestaña de visualización
    }

    @FXML
    private void showInsertTab() {
        mainTabPane.getSelectionModel().select(insertTab); // Cambiar a pestaña de inserción
    }

    @FXML
    private void showDeleteTab() {
        mainTabPane.getSelectionModel().select(deleteTab); // Cambiar a pestaña de eliminación
    }

    @FXML
    private void showSearchTab() {
        mainTabPane.getSelectionModel().select(searchTab); // Cambiar a pestaña de búsqueda
    }

    // ==================== MÉTODOS DE OPERACIONES DEL ÁRBOL ====================
    @FXML
    private void onInsertNode() {
        try {
            // 1. Validar y obtener datos del formulario
            String nombre = nombreField.getText().trim();
            String iso = isoField.getText().trim().toUpperCase();

            // Validaciones básicas
            if (nombre.isEmpty() || iso.isEmpty()) {
                mostrarMensaje("Nombre y código ISO son obligatorios", true);
                return;
            }

            if (iso.length() != 3) {
                mostrarMensaje("El código ISO debe tener exactamente 3 letras", true);
                return;
            }

            // 2. Obtener todas las temperaturas por año (1961-2022 = 62 años)
            double[] temperaturas = new double[62]; // Array para almacenar las temperaturas
            boolean hayErrores = false;
            StringBuilder errores = new StringBuilder();
            int contadorTemperaturas = 0;

            for (int i = 0; i < 62; i++) {
                int year = 1961 + i;
                TextField field = (TextField) temperaturaContainer.lookup("#temp_" + year);
                if (field != null) {
                    String tempStr = field.getText().trim();

                    if (tempStr.isEmpty()) {
                        // Si está vacío, usar 0.0 como valor por defecto
                        temperaturas[i] = 0.0;
                    } else {
                        try {
                            double temperatura = Double.parseDouble(tempStr);
                            if (temperatura < -100.0 || temperatura > 100.0) {
                                hayErrores = true;
                                errores.append("Año ").append(year).append(": temperatura fuera de rango (-100.0 a 100.0)\n");
                            } else {
                                temperaturas[i] = temperatura;
                                contadorTemperaturas++;
                            }
                        } catch (NumberFormatException e) {
                            hayErrores = true;
                            errores.append("Año ").append(year).append(": valor no válido\n");
                        }
                    }
                }
            }

            if (hayErrores) {
                mostrarMensaje("Errores en temperaturas:\n" + errores.toString(), true);
                return;
            }

            // 3. Calcular el promedio (FM) de las temperaturas
            double promedio = 0.0;
            if (contadorTemperaturas > 0) {
                double suma = 0.0;
                for (double temp : temperaturas) {
                    suma += temp;
                }
                promedio = suma / 62; // Dividir entre el total de años (62)
            }

            // 4. Crear nuevo nodo Cell
            Cell nuevoNodo = new Cell(-1, nombre, iso, promedio);
            nuevoNodo.F_i = temperaturas; // Asignar el array de temperaturas individuales

            // 5. Agregar nodo al árbol AVL y actualizar visualización
            agregarNodo(nuevoNodo);

            // 6. Limpiar formulario y mostrar mensaje
            limpiarFormulario();
            mostrarMensaje("País agregado exitosamente con " + contadorTemperaturas + " temperaturas ingresadas", false);

        } catch (Exception e) {
            mostrarMensaje("Error al agregar el país: " + e.getMessage(), true);
            e.printStackTrace(); // Para debugging
        }
    }

    /**
     * Agrega un nodo al árbol y actualiza la visualización
     */
    private void agregarNodo(Cell nuevoNodo) {
        arbolAVL.addNode(nuevoNodo);          // Agregar al árbol lógico
        treeGroup.getChildren().clear();      // Limpiar visualización actual
        tree.drawTree(arbolAVL);              // Redibujar árbol completo
    }

    @FXML
    private void limpiarFormulario() {
        nombreField.clear();
        isoField.clear();

        // Limpiar todos los campos de temperatura
        for (int year = 1961; year <= 2022; year++) {
            TextField field = (TextField) temperaturaContainer.lookup("#temp_" + year);
            if (field != null) {
                field.clear();
            }
        }
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        mensajeLabel.setText(mensaje);
        mensajeLabel.setStyle(esError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    @FXML
    private void onDeleteNode() {
        try {
            String tempStr = temperaturaEliminarField.getText().trim();

            // Validaciones
            if (tempStr.isEmpty()) {
                mostrarMensajeEliminar("Ingrese un valor de temperatura", true);
                return;
            }

            double temperatura = Double.parseDouble(tempStr);
            if (temperatura < -2.0 || temperatura > 2.0) {
                mostrarMensajeEliminar("La temperatura debe estar entre -2.0 y 2.0", true);
                return;
            }

            // Buscar el nodo EXACTO usando tolerancia
            Cell nodoABorrar = buscarNodoPorTemperatura(arbolAVL.root, temperatura);
            if (nodoABorrar == null) {
                mostrarMensajeEliminar("No se encontró un nodo con temperatura: " + temperatura, true);
                return;
            }

            // Eliminar usando el valor EXACTO del nodo encontrado
            arbolAVL.deleteNode(nodoABorrar.FM);
            actualizarVisualizacion();

            mostrarMensajeEliminar("Nodo " + nodoABorrar.ISO3 + " (" + nodoABorrar.FM + ") eliminado correctamente", false);
            temperaturaEliminarField.clear();

        } catch (NumberFormatException e) {
            mostrarMensajeEliminar("La temperatura debe ser un número válido", true);
        }
    }

    private Cell buscarNodoPorTemperatura(NodeCell nodo, double temperaturaBuscada) {
        if (nodo == null) {
            return null;
        }

        // Comparar con tolerancia de 0.0001
        if (Math.abs(nodo.cell.FM - temperaturaBuscada) < 0.0001) {
            return nodo.cell;
        }

        if (temperaturaBuscada < nodo.cell.FM) {
            return buscarNodoPorTemperatura(nodo.left, temperaturaBuscada);
        } else {
            return buscarNodoPorTemperatura(nodo.right, temperaturaBuscada);
        }
    }

    /**
     * Verifica si existe al menos un nodo con la temperatura especificada
     */
    private boolean existeNodoConTemperatura(double temperatura) {
        return buscarNodoPorTemperatura(arbolAVL.root, temperatura) != null;
    }

    private void mostrarMensajeEliminar(String mensaje, boolean esError) {
        mensajeEliminarLabel.setText(mensaje);
        mensajeEliminarLabel.setStyle(esError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    private void actualizarVisualizacion() {
        treeGroup.getChildren().clear(); // Limpiar grupo
        tree.drawTree(arbolAVL);         // Redibujar árbol
    }

    // ==================== MÉTODOS DE BÚSQUEDA ====================
    /*@FXML
    private void onSearchNode() {
        onSearchNodes(); // Alias para consistencia
    }*/
    @FXML
    private void onSearchNodes() {
        try {
            if (tipoBusquedaComboBox == null) {
                return;
            }

            String tipoBusqueda = tipoBusquedaComboBox.getValue();
            if (tipoBusqueda == null) {
                mostrarMensajeBusqueda("Seleccione un tipo de búsqueda", true);
                return;
            }

            configurarParametrosBusqueda(tipoBusqueda); // Configurar UI según tipo de búsqueda

        } catch (Exception e) {
            mostrarMensajeBusqueda("Error: " + e.getMessage(), true);
        }
    }

    /**
     * Configura los parámetros de búsqueda según el tipo seleccionado
     */
    private void configurarParametrosBusqueda(String tipoBusqueda) {
        parametroBusquedaBox.getChildren().clear(); // Limpiar parámetros anteriores

        if (tipoBusqueda.equals("Temperatura mayor al promedio anual")) {
            configurarBusquedaAnual();
        } else if (tipoBusqueda.equals("Temperatura menor al promedio global")) {
            configurarBusquedaGlobal();
        } else if (tipoBusqueda.equals("Temperatura mayor o igual a valor")) {
            configurarBusquedaPorValor();
        }
    }

    // ... (los métodos de configuración de búsqueda mantienen misma estructura)
    private void aplicarEstiloBotonBuscar(Button boton) {
        // Estilo normal
        boton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 24px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");

        // Efecto hover
        boton.setOnMouseEntered(e -> {
            boton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 24px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });

        boton.setOnMouseExited(e -> {
            boton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 24px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;");
        });

        // Efecto al presionar
        boton.setOnMousePressed(e -> {
            boton.setStyle("-fx-background-color: #FF4757; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 24px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        });

        boton.setOnMouseReleased(e -> {
            boton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 24px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        });
    }

    // ==================== MÉTODOS DE BÚSQUEDA ====================
    private void realizarBusquedaAnual(int año) {
        try {
            double promedioAnual = calcularPromedioAnual(año);
            List<Cell> todosNodos = arbolAVL.inOrderAdding();
            List<Cell> resultados = new ArrayList<>();

            for (Cell cell : todosNodos) {
                double temperaturaEnAnio = obtenerTemperaturaEnAnio(cell, año);
                if (temperaturaEnAnio != Double.MIN_VALUE && temperaturaEnAnio > promedioAnual) {
                    resultados.add(cell); // Agregar a resultados si cumple condición
                }
            }

            ContextoBusqueda contexto = new ContextoBusqueda("A", año, promedioAnual);
            mostrarResultados(resultados, contexto);
            mostrarMensajeBusqueda("A: " + resultados.size() + " países con temp(" + año + ") > " + String.format("%.2f", promedioAnual), false);

        } catch (Exception e) {
            mostrarMensajeBusqueda("Error en búsqueda A: " + e.getMessage(), true);
        }
    }

    private void realizarBusquedaGlobal(int año) {
        try {
            double promedioGlobal = calcularPromedioGlobal();
            List<Cell> todosNodos = arbolAVL.inOrderAdding();
            List<Cell> resultados = new ArrayList<>();

            for (Cell cell : todosNodos) {
                double temperaturaEnAnio = obtenerTemperaturaEnAnio(cell, año);
                if (temperaturaEnAnio != Double.MIN_VALUE && temperaturaEnAnio < promedioGlobal) {
                    resultados.add(cell); // Agregar a resultados si cumple condición
                }
            }

            ContextoBusqueda contexto = new ContextoBusqueda("B", año, promedioGlobal);
            mostrarResultados(resultados, contexto);
            mostrarMensajeBusqueda("B: " + resultados.size() + " países con temp(" + año + ") < " + String.format("%.2f", promedioGlobal), false);

        } catch (Exception e) {
            mostrarMensajeBusqueda("Error en búsqueda B: " + e.getMessage(), true);
        }
    }

    private void realizarBusquedaPorValor(double valor) {
        try {
            List<Cell> todosNodos = arbolAVL.inOrderAdding();
            List<Cell> resultados = new ArrayList<>();

            for (Cell cell : todosNodos) {
                if (cell.FM >= valor) {
                    resultados.add(cell); // Agregar a resultados si cumple condición
                }
            }

            ContextoBusqueda contexto = new ContextoBusqueda("C", null, valor);
            mostrarResultados(resultados, contexto);
            mostrarMensajeBusqueda("C: " + resultados.size() + " países con promedio ≥ " + valor, false);

        } catch (Exception e) {
            mostrarMensajeBusqueda("Error en búsqueda C: " + e.getMessage(), true);
        }
    }

    // ==================== MÉTODOS AUXILIARES DE BÚSQUEDA ====================
    /**
     * Obtiene la temperatura de una celda para un año específico
     */
    private double obtenerTemperaturaEnAnio(Cell cell, int año) {
        if (cell.F_i == null || cell.F_i.length == 0) {
            return Double.MIN_VALUE; // Valor indicador de error
        }
        int añoInicial = 1961;
        if (año < añoInicial || año > 2022) {
            return Double.MIN_VALUE; // Año fuera de rango
        }
        int indice = año - añoInicial; // Calcular índice en el array
        return (indice >= 0 && indice < cell.F_i.length) ? cell.F_i[indice] : Double.MIN_VALUE;
    }

    /**
     * Calcula el promedio de temperatura para un año específico
     */
    private double calcularPromedioAnual(int año) {
        List<Cell> todosNodos = arbolAVL.inOrderAdding();
        if (todosNodos.isEmpty()) {
            return 0.0;
        }

        double suma = 0.0;
        int count = 0;

        for (Cell cell : todosNodos) {
            double temperatura = obtenerTemperaturaEnAnio(cell, año);
            if (temperatura != Double.MIN_VALUE) {
                suma += temperatura;
                count++;
            }
        }
        return count > 0 ? suma / count : 0.0; // Evitar división por cero
    }

    /**
     * Calcula el promedio global de temperaturas
     */
    private double calcularPromedioGlobal() {
        List<Cell> todosNodos = arbolAVL.inOrderAdding();
        if (todosNodos.isEmpty()) {
            return 0.0;
        }

        double suma = 0.0;
        for (Cell cell : todosNodos) {
            suma += cell.FM; // Sumar promedios generales
        }
        return suma / todosNodos.size();
    }

    /**
     * Configura la interfaz para búsqueda por promedio anual Crea controles
     * para seleccionar año y buscar países con temperatura mayor al promedio
     * anual
     */
    private void configurarBusquedaAnual() {
        Label label = new Label("Seleccione el año (1961-2022):");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2C3E50;");

        ComboBox<Integer> añoComboBox = new ComboBox<>();
        for (int año = 1961; año <= 2022; año++) {
            añoComboBox.getItems().add(año); // Llenar comboBox con años desde 1961 hasta 2022
        }
        añoComboBox.setPromptText("Seleccione un año");
        añoComboBox.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #4682B4; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 8px;");

        Button buscarBtn = new Button("Buscar Países con Temp(Año) > Promedio(Año)");
        aplicarEstiloBotonBuscar(buscarBtn);

        buscarBtn.setOnAction(e -> {
            Integer año = añoComboBox.getValue();
            if (año == null) {
                mostrarMensajeBusqueda("Seleccione un año", true); // Validar selección de año
                return;
            }
            realizarBusquedaAnual(año); // Ejecutar búsqueda con año seleccionado
        });

        // Contenedor centrado para mejor presentación
        VBox contenedorCentrado = new VBox(15); // Espaciado de 15px entre elementos
        contenedorCentrado.setAlignment(Pos.CENTER);
        contenedorCentrado.getChildren().addAll(label, añoComboBox, buscarBtn);

        parametroBusquedaBox.getChildren().add(contenedorCentrado); // Agregar controles al panel
        parametroBusquedaBox.setVisible(true); // Mostrar panel de parámetros
        parametroBusquedaBox.setAlignment(Pos.CENTER);
    }

    /**
     * Configura la interfaz para búsqueda por promedio global Crea controles
     * para seleccionar año y buscar países con temperatura menor al promedio
     * global
     */
    private void configurarBusquedaGlobal() {
        Label label = new Label("Seleccione el año (1961-2022):");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2C3E50;");

        ComboBox<Integer> añoComboBox = new ComboBox<>();
        for (int año = 1961; año <= 2022; año++) {
            añoComboBox.getItems().add(año); // Llenar comboBox con años desde 1961 hasta 2022
        }
        añoComboBox.setPromptText("Seleccione un año");
        añoComboBox.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #4682B4; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 8px;");

        Button buscarBtn = new Button("Buscar Países con Temp(Año) < Promedio(Global)");
        aplicarEstiloBotonBuscar(buscarBtn);

        buscarBtn.setOnAction(e -> {
            Integer año = añoComboBox.getValue();
            if (año == null) {
                mostrarMensajeBusqueda("Seleccione un año", true); // Validar selección de año
                return;
            }
            realizarBusquedaGlobal(año); // Ejecutar búsqueda con año seleccionado
        });

        VBox contenedorCentrado = new VBox(15); // Espaciado de 15px entre elementos
        contenedorCentrado.setAlignment(Pos.CENTER);
        contenedorCentrado.getChildren().addAll(label, añoComboBox, buscarBtn);

        parametroBusquedaBox.getChildren().add(contenedorCentrado); // Agregar controles al panel
        parametroBusquedaBox.setVisible(true); // Mostrar panel de parámetros
        parametroBusquedaBox.setAlignment(Pos.CENTER);
    }

    /**
     * Configura la interfaz para búsqueda por valor específico Crea controles
     * para ingresar valor y buscar países con promedio mayor o igual
     */
    private void configurarBusquedaPorValor() {
        Label label = new Label("Ingrese el valor de temperatura:");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2C3E50;");

        TextField valorField = new TextField();
        valorField.setPromptText("Ej: 0.5 (-2.0 a 2.0)"); // Texto de ejemplo para usuario
        valorField.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #4682B4; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 8px;");

        Button buscarBtn = new Button("Buscar Países con Promedio ≥ Valor");
        aplicarEstiloBotonBuscar(buscarBtn);

        buscarBtn.setOnAction(e -> {
            try {
                double valor = Double.parseDouble(valorField.getText().trim()); // Convertir texto a número
                if (valor < -2.0 || valor > 2.0) {
                    mostrarMensajeBusqueda("La temperatura debe estar entre -2.0 y 2.0", true); // Validar rango
                    return;
                }
                realizarBusquedaPorValor(valor); // Ejecutar búsqueda con valor ingresado
            } catch (NumberFormatException ex) {
                mostrarMensajeBusqueda("Ingrese un valor válido", true); // Manejar error de formato
            }
        });

        VBox contenedorCentrado = new VBox(15); // Espaciado de 15px entre elementos
        contenedorCentrado.setAlignment(Pos.CENTER);
        contenedorCentrado.getChildren().addAll(label, valorField, buscarBtn);

        parametroBusquedaBox.getChildren().add(contenedorCentrado); // Agregar controles al panel
        parametroBusquedaBox.setVisible(true); // Mostrar panel de parámetros
        parametroBusquedaBox.setAlignment(Pos.CENTER);
    }

    /**
     * Busca un nodo por su valor de temperatura (recorrido recursivo)
     */
    private NodeCell buscarNodoPorValor(NodeCell raiz, double valor) {
        if (raiz == null) {
            return null;
        }

        if (Math.abs(raiz.cell.FM - valor) < 0.0001) { // Comparación con tolerancia
            return raiz;
        }

        if (valor < raiz.cell.FM) {
            return buscarNodoPorValor(raiz.left, valor);
        } else {
            return buscarNodoPorValor(raiz.right, valor);
        }
    }

    /**
     * Busca el padre de un nodo recorriendo el árbol desde la raíz
     */
    private NodeCell buscarPadre(NodeCell raiz, double valorHijo) {
        if (raiz == null) {
            return null;
        }

        // Verificar si alguno de los hijos es el nodo buscado
        if (raiz.left != null && Math.abs(raiz.left.cell.FM - valorHijo) < 0.0001) {
            return raiz; // raiz es el padre del nodo izquierdo
        }

        if (raiz.right != null && Math.abs(raiz.right.cell.FM - valorHijo) < 0.0001) {
            return raiz; // raiz es el padre del nodo derecho
        }

        // Buscar recursivamente en los subárboles
        if (valorHijo < raiz.cell.FM) {
            return buscarPadre(raiz.left, valorHijo);
        } else {
            return buscarPadre(raiz.right, valorHijo);
        }
    }

    /**
     * Busca el tío de un nodo (hermano del padre)
     */
    private NodeCell buscarTio(NodeCell raiz, double valorSobrino) {
        // 1. Encontrar el padre del nodo
        NodeCell padre = buscarPadre(raiz, valorSobrino);
        if (padre == null) {
            return null; // El nodo es la raíz, no tiene tío
        }
        // 2. Encontrar el abuelo (padre del padre)
        NodeCell abuelo = buscarPadre(raiz, padre.cell.FM);
        if (abuelo == null) {
            return null; // El padre es la raíz, no tiene hermanos
        }
        // 3. El tío es el otro hijo del abuelo (no el padre)
        if (abuelo.left == padre) {
            return abuelo.right; // Tío es el hijo derecho
        } else {
            return abuelo.left;  // Tío es el hijo izquierdo
        }
    }

    // ==================== MÉTODOS DE VISUALIZACIÓN ====================
    /**
     * Crea una representación visual de un nodo para mostrar en resultados de
     * búsqueda Incluye círculo con código ISO y efectos hover interactivos
     */
    private VBox crearNodoVisual(Cell cell, ContextoBusqueda contexto) {
        Circle circle = new Circle(30, Color.LIGHTBLUE); // Círculo azul claro de 30px de radio
        Text text = new Text(cell.ISO3); // Texto con código ISO del país
        text.setStyle("-fx-font-weight: bold;"); // Texto en negrita

        VBox container = new VBox(5); // Contenedor vertical con 5px de espaciado
        container.setAlignment(Pos.CENTER);

        // Estilo normal del contenedor (similar al árbol principal)
        container.setStyle("-fx-padding: 12; -fx-border-color: #4682B4; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-color: #F0F8FF; -fx-background-radius: 8px;");

        container.getChildren().addAll(circle, text); // Agregar círculo y texto al contenedor

        // Inicializar popup para tooltips si no existe
        if (popupInfo == null) {
            popupInfo = new Popup(); // Ventana emergente
            popupLabel = new Label();
            popupLabel.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 12px; -fx-border-color: #CCCCCC; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-font-weight: bold;");
            popupInfo.getContent().add(popupLabel);
            popupInfo.setAutoHide(true); // Ocultar automáticamente al hacer click fuera
        }

        // Efecto hover mejorado para el contenedor
        container.setOnMouseEntered(e -> {
            circle.setFill(Color.RED); // Cambiar círculo a rojo
            container.setStyle("-fx-padding: 12; -fx-border-color: #FF0000; -fx-border-width: 3px; -fx-border-radius: 8px; -fx-background-color: #FFF0F0; -fx-background-radius: 8px;"); // Borde rojo y fondo claro

            String info = crearTooltipText(cell, contexto); // Generar texto del tooltip
            popupLabel.setText(info);

            // Mostrar popup cerca del cursor
            if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
                popupInfo.show(rootPane.getScene().getWindow(), e.getScreenX() + 10, e.getScreenY() + 10);
            }
        });

        container.setOnMouseExited(e -> {
            circle.setFill(Color.LIGHTBLUE); // Restaurar color azul del círculo
            container.setStyle("-fx-padding: 12; -fx-border-color: #4682B4; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-color: #F0F8FF; -fx-background-radius: 8px;"); // Restaurar estilo normal
            popupInfo.hide(); // Ocultar popup
        });

        container.setOnMouseMoved(e -> {
            popupInfo.hide(); // Ocultar popup anterior
            // Mover popup a nueva posición del cursor
            if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
                popupInfo.show(rootPane.getScene().getWindow(), e.getScreenX() + 10, e.getScreenY() + 10);
            }
        });

        // Hover también en el círculo individualmente
        circle.setOnMouseEntered(e -> {
            circle.setFill(Color.RED);
            container.setStyle("-fx-padding: 12; -fx-border-color: #FF0000; -fx-border-width: 3px; -fx-border-radius: 8px; -fx-background-color: #FFF0F0; -fx-background-radius: 8px;");
        });

        circle.setOnMouseExited(e -> {
            circle.setFill(Color.LIGHTBLUE);
            container.setStyle("-fx-padding: 12; -fx-border-color: #4682B4; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-color: #F0F8FF; -fx-background-radius: 8px;");
        });

        // Hover también en el texto individualmente
        text.setOnMouseEntered(e -> {
            circle.setFill(Color.RED);
            container.setStyle("-fx-padding: 12; -fx-border-color: #FF0000; -fx-border-width: 3px; -fx-border-radius: 8px; -fx-background-color: #FFF0F0; -fx-background-radius: 8px;");
        });

        text.setOnMouseExited(e -> {
            circle.setFill(Color.LIGHTBLUE);
            container.setStyle("-fx-padding: 12; -fx-border-color: #4682B4; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-color: #F0F8FF; -fx-background-radius: 8px;");
        });

        return container; // Devolver nodo visual completamente configurado
    }

    /**
     * Crea el texto informativo para el tooltip que se muestra al hacer hover
     * sobre nodos de resultados La información varía según el tipo de búsqueda
     * realizada
     */
    private String crearTooltipText(Cell cell, ContextoBusqueda contexto) {
        StringBuilder sb = new StringBuilder(); // Constructor para texto eficiente
        sb.append("País: ").append(cell.Country).append("\n");        // Nombre del país
        sb.append("ISO: ").append(cell.ISO3).append("\n");            // Código ISO de 3 letras
        sb.append("Promedio general: ").append(String.format("%.2f", cell.FM)).append("\n"); // Temperatura promedio formateada

        // Información específica según el tipo de búsqueda
        switch (contexto.getTipoBusqueda()) {
            case "A": // Temperatura mayor al promedio anual
                double tempA = obtenerTemperaturaEnAnio(cell, contexto.getAño());
                sb.append("Temp(").append(contexto.getAño()).append("): ").append(String.format("%.2f", tempA)).append("\n");
                sb.append("Promedio(").append(contexto.getAño()).append("): ").append(String.format("%.2f", contexto.getValor())).append("\n");
                sb.append("Diferencia: +").append(String.format("%.2f", tempA - contexto.getValor())).append("\n"); // Diferencia positiva
                break;

            case "B": // Temperatura menor al promedio global
                double tempB = obtenerTemperaturaEnAnio(cell, contexto.getAño());
                sb.append("Temp(").append(contexto.getAño()).append("): ").append(String.format("%.2f", tempB)).append("\n");
                sb.append("Promedio global: ").append(String.format("%.2f", contexto.getValor())).append("\n");
                sb.append("Diferencia: ").append(String.format("%.2f", tempB - contexto.getValor())).append("\n"); // Diferencia con signo
                break;

            case "C": // Temperatura mayor o igual a valor específico
                sb.append("Valor buscado: ").append(String.format("%.2f", contexto.getValor())).append("\n");
                sb.append("Diferencia: +").append(String.format("%.2f", cell.FM - contexto.getValor())).append("\n"); // Diferencia positiva
                break;
        }

        // Información adicional del árbol AVL
        try {
            sb.append("Nivel: ").append(arbolAVL.getNodeLevel(cell.FM)).append("\n");      // Nivel del nodo en el árbol
            sb.append("Balance: ").append(arbolAVL.getNodeBalance(cell.FM)).append("\n");  // Factor de balance del nodo

            NodeCell nodoBuscado = buscarNodoPorValor(arbolAVL.root, cell.FM);
            if (nodoBuscado != null) {
                // Padre
                NodeCell padre = buscarPadre(arbolAVL.root, cell.FM);
                sb.append("Padre: ").append(padre != null ? padre.cell.ISO3 : "Raíz").append("\n");

                // Abuelo (necesitamos encontrar al padre primero)
                if (padre != null) {
                    NodeCell abuelo = buscarPadre(arbolAVL.root, padre.cell.FM);
                    sb.append("Abuelo: ").append(abuelo != null ? abuelo.cell.ISO3 : "No tiene").append("\n");

                    // Tío (hermano del padre)
                    NodeCell tio = buscarTio(arbolAVL.root, cell.FM);
                    sb.append("Tío: ").append(tio != null ? tio.cell.ISO3 : "No tiene").append("\n");
                } else {
                    sb.append("Abuelo: No tiene\n");
                    sb.append("Tío: No tiene\n");
                }
            }

        } catch (Exception e) {
            sb.append("Info árbol: No disponible\n"); // Mensaje de fallback en caso de error
        }

        return sb.toString(); // Devolver texto completo del tooltip
    }

    /**
     * Muestra mensajes de feedback para las operaciones de búsqueda Los
     * mensajes de error se muestran en rojo, los exitosos en verde
     */
    private void mostrarMensajeBusqueda(String mensaje, boolean esError) {
        if (mensajeBusquedaLabel != null) {
            mensajeBusquedaLabel.setText(mensaje); // Establecer texto del mensaje
            mensajeBusquedaLabel.setStyle(esError ? "-fx-text-fill: red;" : "-fx-text-fill: green;"); // Color según tipo
        }
    }

    /**
     * Aplica estilos consistentes a los botones principales de la aplicación
     * Incluye efectos de hover y transiciones suaves
     */
    private void aplicarEstiloBotonPrincipal(Button boton, String colorNormal, String colorHover) {
        // Estilo normal del botón
        boton.setStyle("-fx-background-color: " + colorNormal + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 24px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");

        // Efecto hover - se activa cuando el mouse entra al botón
        boton.setOnMouseEntered(e -> {
            boton.setStyle("-fx-background-color: " + colorHover + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 24px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });

        // Efecto al salir del hover - restaura estilo normal
        boton.setOnMouseExited(e -> {
            boton.setStyle("-fx-background-color: " + colorNormal + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 24px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;");
        });
    }

    /**
     * Maneja el evento de clic para iniciar el recorrido por niveles del árbol
     * Se ejecuta cuando el usuario selecciona la opción del menú "Recorrido por
     * Niveles"
     */
    @FXML
    private void onRecorridoNiveles() {
        try {
            if (tree != null && arbolAVL != null) {
                tree.animarRecorridoPorNiveles(arbolAVL); // Iniciar animación del recorrido
                mostrarMensajeDebug("Animación de recorrido por niveles iniciada");
            } else {
                mostrarMensajeDebug("Error: Árbol no inicializado");
            }
        } catch (Exception e) {
            System.err.println("ERROR en onRecorridoNiveles:");
            e.printStackTrace(); // Log detallado del error en consola
            mostrarMensajeDebug("Error: " + e.getMessage()); // Mensaje de error simplificado en UI
        }
    }

    private void mostrarResultados(List<Cell> resultados, ContextoBusqueda contexto) {
        if (resultadosFlowPane == null) {
            return;
        }

        resultadosFlowPane.getChildren().clear(); // Limpiar resultados anteriores

        // Configurar flow pane
        resultadosFlowPane.setAlignment(Pos.TOP_CENTER);
        resultadosFlowPane.setHgap(20);  // Espacio horizontal entre elementos
        resultadosFlowPane.setVgap(20);  // Espacio vertical entre elementos
        resultadosFlowPane.setPrefWrapLength(800); // Ancho preferido para wrap

        // Crear visualización para cada resultado
        for (Cell cell : resultados) {
            VBox nodoBox = crearNodoVisual(cell, contexto);
            resultadosFlowPane.getChildren().add(nodoBox);
        }

        if (resultadosBox != null) {
            resultadosBox.setVisible(true); // Mostrar contenedor de resultados
        }
    }

    private void mostrarMensajeDebug(String mensaje) {
        if (debugLabel != null) {
            debugLabel.setText(mensaje);
        }
        System.out.println(mensaje);
    }

}
