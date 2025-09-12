package gui.controller;

import Core.Utils.CSVReaderUtil;
import Core.Utils.Cell;
import Core.Utils.SelfBalancingBST;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class MainController {

    @FXML
    private Pane rootPane;
    @FXML
    private Group treeGroup;

    private final DoubleProperty scale = new SimpleDoubleProperty(1.0);
    private double lastX, lastY;

    private Tree tree;


    @FXML
    private void initialize() {
        treeGroup.scaleXProperty().bind(scale);
        treeGroup.scaleYProperty().bind(scale);

        // Zoom
        rootPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            double factor = e.getDeltaY() > 0 ? 1.1 : 0.9; 
            double newScale = Math.max(0.1, Math.min(3.0, scale.get() * factor));
            scale.set(newScale);
        });

        // Arrastre
        rootPane.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            lastX = e.getSceneX();
            lastY = e.getSceneY();
        });
        rootPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            treeGroup.setTranslateX(treeGroup.getTranslateX() + e.getSceneX() - lastX);
            treeGroup.setTranslateY(treeGroup.getTranslateY() + e.getSceneY() - lastY);
            lastX = e.getSceneX();
            lastY = e.getSceneY();
        });

        tree = new Tree(treeGroup);
        
        ArrayList<Cell> cells = CSVReaderUtil.readCSV("src/Main/data.csv");
        SelfBalancingBST ArbolInicial = new SelfBalancingBST();
        for (Cell c : cells) {
            ArbolInicial.addNode(c);
        }
        tree.drawTree(ArbolInicial);
    }

    @FXML
    private void onInsertNode() {}

    @FXML
    private void onDeleteNode() {}

    @FXML
    private void onSearchNode() {}
}
