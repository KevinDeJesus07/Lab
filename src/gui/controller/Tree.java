package gui.controller;

import Core.Utils.BinaryTree;
import Core.Utils.NodeCell;
import Core.Utils.NodePosition;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Tree {

    private final Group group;
    private final Color NORMAL_COLOR = Color.LIGHTBLUE;
    private final Color HOVER_COLOR = Color.RED;

    public Tree(Group group) {
        this.group = group;
    }

    public void drawTree(BinaryTree tree) {
        group.getChildren().clear();
        NodeCell root = tree.root;
        if (root == null) {
            return;
        }

        double width = 1920 * 4;
        double height = 1080 * 4;

        int heightTree = tree.getHeight(tree.root);
        double yStep = height / (heightTree + 1);

        List<NodePosition> positions = new ArrayList<>();
        computePositions(root, 1, 0, width, yStep, positions);

        // Aristas
        for (NodePosition np : positions) {
            if (np.node.left != null) {
                NodePosition leftPos = findPos(positions, np.node.left);
                Line line = new Line(np.x, np.y, leftPos.x, leftPos.y);
                line.setStroke(Color.BLACK);
                group.getChildren().add(line);
            }
            if (np.node.right != null) {
                NodePosition rightPos = findPos(positions, np.node.right);
                Line line = new Line(np.x, np.y, rightPos.x, rightPos.y);
                line.setStroke(Color.BLACK);
                group.getChildren().add(line);
            }
        }

        // Nodos
        for (NodePosition np : positions) {
            Circle circle = new Circle(np.x, np.y, 25, NORMAL_COLOR);
            Text text = new Text(np.x - 10, np.y + 4, np.node.cell.ISO3);

            // Agregar eventos de hover al círculo
            circle.setOnMouseEntered(e -> circle.setFill(HOVER_COLOR));
            circle.setOnMouseExited(e -> circle.setFill(NORMAL_COLOR));

            // Agregar eventos de hover al texto también
            text.setOnMouseEntered(e -> circle.setFill(HOVER_COLOR));
            text.setOnMouseExited(e -> circle.setFill(NORMAL_COLOR));

            group.getChildren().addAll(circle, text);
        }
    }

    private void computePositions(NodeCell node,
            int depth,
            double xMin,
            double xMax,
            double yStep,
            List<NodePosition> positions) {
        if (node == null) {
            return;
        }

        double xMid = (xMin + xMax) / 2.0;
        double y = depth * yStep;

        computePositions(node.left, depth + 1, xMin, xMid, yStep, positions);
        positions.add(new NodePosition(node, xMid, y));
        computePositions(node.right, depth + 1, xMid, xMax, yStep, positions);
    }

    private NodePosition findPos(List<NodePosition> list, NodeCell target) {
        for (NodePosition np : list) {
            if (np.node == target) {
                return np;
            }
        }
        return null;
    }
}
