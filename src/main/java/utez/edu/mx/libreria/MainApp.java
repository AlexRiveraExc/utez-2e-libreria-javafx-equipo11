package utez.edu.mx.libreria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utez.edu.mx.libreria.service.LibroService;

import java.io.IOException;

/**
 * Punto de entrada de la aplicación JavaFX.
 *
 * @author Integrante 1 (Backend)
 */
public class MainApp extends Application {

    /** Instancia compartida del servicio para todos los controllers. */
    private static LibroService libroService;

    @Override
    public void start(Stage primaryStage) throws IOException {
        libroService = new LibroService();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/utez/edu/mx/libreria/views/principal.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);

        scene.getStylesheets().add(
                getClass().getResource(
                                "/utez/edu/mx/libreria/views/styles.css")
                        .toExternalForm());

        primaryStage.setTitle("Biblioteca Escolar — Catálogo de Libros");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    /** Getter para que los controllers accedan al servicio. */
    public static LibroService getLibroService() {
        return libroService;
    }

    public static void main(String[] args) {
        launch(args);
    }
}