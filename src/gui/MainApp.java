package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/main.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Árbol");

        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/tree_icon.png"));
            stage.getIcons().add(icon);
            System.out.println("Imagen creada");
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
