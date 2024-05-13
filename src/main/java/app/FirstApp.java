package app;

import GUI.EqualizerApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
public class FirstApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Загрузка структуры GUI из FXML файла
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/GUI/FxmlDocumentFirst.fxml")));
        // Создание сцены на основе загруженного интерфейса
        Scene scene = new Scene(root);
        // Установка заголовка окна
        stage.setTitle("EQUALIZER");
        // Установка сцены в окне
        stage.setScene(scene);
        // Добавление стилей из CSS файла
        scene.getStylesheets().add(EqualizerApp.class.getResource("/GUI/Style.css").toExternalForm());
        // Отображение окна
        stage.show();
    }

    /**
     * Точка входа в приложение
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // Запуск JavaFX приложения
        launch(args);
    }
}
