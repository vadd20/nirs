package app;

import GUI.EqualizerApp;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Приложение с альтернативным буфером.
 * Имеются 2 массива. Параллельно выполняются следующие действия:
 * Один из массивов заполняется данными, из второго считываются данными. По завершении считывания
 * массивы меняются ролями
 */
public class AlternativeApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/GUI/FxmlDocumentAlternative.fxml")));

        Scene scene = new Scene(root);
        stage.setTitle("EQUALIZER");
        stage.setScene(scene);
        scene.getStylesheets().add
                (EqualizerApp.class.getResource("/GUI/Style.css").toExternalForm());
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
