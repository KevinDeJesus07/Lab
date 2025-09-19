package gui.controller;

import Core.Utils.CSVReaderUtil;
import Core.Utils.Cell;
import Core.Utils.SelfBalancingBST;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;

public class MainController {

    @FXML
    private Pane rootPane;
    @FXML
    private Group treeGroup;

    private final DoubleProperty scale = new SimpleDoubleProperty(1.0);
    private double lastX, lastY;

    private Tree tree;

    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab visualizationTab;
    @FXML
    private Tab insertTab;
    @FXML
    private Tab deleteTab;
    @FXML
    private Tab searchTab;
    @FXML
    private Label debugLabel;

    // Propiedad para el texto de debug
    private final StringProperty debugInfo = new SimpleStringProperty();

    // Valores iniciales configurables
    private static final double INITIAL_SCALE = 0.1; // Zoom inicial al 80%
    private static final double INITIAL_X = -8640;   // Posición X inicial
    private static final double INITIAL_Y = -4940;    // Posición Y inicial

    @FXML
    private void initialize() {
        // Configurar valores iniciales
        scale.set(INITIAL_SCALE);
        treeGroup.setTranslateX(INITIAL_X);
        treeGroup.setTranslateY(INITIAL_Y);

        treeGroup.scaleXProperty().bind(scale);
        treeGroup.scaleYProperty().bind(scale);

        // Configurar el binding automático para debug
        setupDebugBinding();

        // Zoom
        rootPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            double factor = e.getDeltaY() > 0 ? 1.1 : 0.9;
            double newScale = Math.max(0.1, Math.min(1.0, scale.get() * factor));
            scale.set(newScale);
            applyDragLimits(treeGroup.getTranslateX(), treeGroup.getTranslateY());
            updateDebugPosition();
        });

        // Arrastre
        rootPane.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            lastX = e.getSceneX();
            lastY = e.getSceneY();

        });
        rootPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            double newX = treeGroup.getTranslateX() + e.getSceneX() - lastX;
            double newY = treeGroup.getTranslateY() + e.getSceneY() - lastY;

            // Aplicar límites al arrastre
            applyDragLimits(newX, newY);

            lastX = e.getSceneX();
            lastY = e.getSceneY();

            // Actualizar información de debug después de arrastrar
            updateDebugPosition();
        });

        tree = new Tree(treeGroup);

        ArrayList<Cell> cells = CSVReaderUtil.readCSV("src/Main/data.csv");
        SelfBalancingBST ArbolInicial = new SelfBalancingBST();
        for (Cell c : cells) {
            ArbolInicial.addNode(c);
        }
        tree.drawTree(ArbolInicial);
    }

    // Agrega este método para aplicar los límites de arrastre
    private void applyDragLimits(double newX, double newY) {
        double currentScale = scale.get();

        // Definir los límites para diferentes niveles de zoom
        // Estos son valores de ejemplo, ajústalos según tus necesidades
        // Para zoom 1.0 (máximo)
        double xMinAtMaxZoom = -17350;
        double xMaxAtMaxZoom = 250;
        double yMinAtMaxZoom = -8900;
        double yMaxAtMaxZoom = -950;

        // Para zoom 0.1 (mínimo)
        double xMinAtMinZoom = -8640;
        double xMaxAtMinZoom = -8640; // Mismo valor para min y max = posición fija
        double yMinAtMinZoom = -4940;
        double yMaxAtMinZoom = -4940; // Mismo valor para min y max = posición fija

        // Interpolar los límites según el nivel de zoom actual
        // Factor de interpolación (0 a 1) donde 0 = zoom mínimo, 1 = zoom máximo
        double zoomFactor = (currentScale - 0.1) / (1.0 - 0.1);
        zoomFactor = Math.max(0, Math.min(1, zoomFactor)); // Asegurar que esté entre 0 y 1

        // Calcular límites actuales interpolados
        double xMin = interpolate(xMinAtMinZoom, xMinAtMaxZoom, zoomFactor);
        double xMax = interpolate(xMaxAtMinZoom, xMaxAtMaxZoom, zoomFactor);
        double yMin = interpolate(yMinAtMinZoom, yMinAtMaxZoom, zoomFactor);
        double yMax = interpolate(yMaxAtMinZoom, yMaxAtMaxZoom, zoomFactor);

        // Aplicar límites
        double clampedX = Math.max(xMin, Math.min(xMax, newX));
        double clampedY = Math.max(yMin, Math.min(yMax, newY));

        // Establecer la posición limitada
        treeGroup.setTranslateX(clampedX);
        treeGroup.setTranslateY(clampedY);
    }

    private double interpolate(double start, double end, double factor) {
        return start + (end - start) * factor;
    }

    private void setupDebugBinding() {
        // Vincular el Label a la propiedad debugInfo
        if (debugLabel != null) {
            debugLabel.textProperty().bind(debugInfo);
        }

        // Establecer valor inicial
        updateDebugInfo();
    }

    private void updateDebugInfo() {
        debugInfo.set(String.format("Debug: escala=%.2f, x=%.1f, y=%.1f",
                scale.get(), treeGroup.getTranslateX(), treeGroup.getTranslateY()));
    }

    private void updateDebugPosition() {
        debugInfo.set(String.format("Debug: escala=%.2f, x=%.1f, y=%.1f",
                scale.get(), treeGroup.getTranslateX(), treeGroup.getTranslateY()));
    }

    @FXML
    private void showVisualizationTab() {
        mainTabPane.getSelectionModel().select(visualizationTab);
    }

    @FXML
    private void showInsertTab() {
        mainTabPane.getSelectionModel().select(insertTab);
    }

    @FXML
    private void showDeleteTab() {
        mainTabPane.getSelectionModel().select(deleteTab);
    }

    @FXML
    private void showSearchTab() {
        mainTabPane.getSelectionModel().select(searchTab);
    }

    @FXML
    private void onInsertNode() {
        // Tu lógica de inserción aquí
    }

    @FXML
    private void onDeleteNode() {
        // Tu lógica de eliminación aquí
    }

    @FXML
    private void onSearchNode() {
        // Tu lógica de búsqueda aquí
    }

    @FXML
    private void showDebugInfo() {
        // Método para mostrar información de debug si es necesario
        updateDebugInfo();
    }
}
