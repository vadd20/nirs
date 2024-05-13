package GUI;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import player.AudioPlayerFirst;

public class FxmlControllerFirst implements Initializable {
    private AudioPlayerFirst audioPlayer;
    private Thread playThread;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Пустой метод инициализации, не требуется дополнительных действий при загрузке FXML файла
    }

    // Метод для открытия аудиофайла и запуска его воспроизведения
    @FXML private void open() {
        // Открытие диалогового окна выбора файла
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        // Проверка, был ли выбран файл
        if (selectedFile == null) return;

        // Создание объекта AudioPlayerFirst для выбранного файла и запуск потока воспроизведения
        this.audioPlayer = new AudioPlayerFirst(selectedFile);
        playThread = new Thread(() -> {
            System.out.println("PLAY");
            this.audioPlayer.play();
        });
        playThread.start();
    }

    // Метод для продолжения воспроизведения аудиофайла
    @FXML private void play() {
        // Вывод сообщения о начале воспроизведения
        System.out.println("PLAY");
        // Проверка, существует ли объект audioPlayer и был ли он остановлен
        if (this.audioPlayer != null) {
            if (this.audioPlayer.isStopped()) {
                // Если аудиофайл был остановлен, воспроизведение начинается с начала
                this.audioPlayer.setStopped(false);
                playThread = new Thread(() -> this.audioPlayer.play());
                playThread.start();
            } else {
                // Если аудиофайл на паузе, воспроизведение продолжается с места остановки
                this.audioPlayer.setPaused(false);
                this.audioPlayer.resume();
            }
        }
    }

    // Метод для приостановки воспроизведения аудиофайла
    @FXML private void pause() {
        // Вывод сообщения о приостановке воспроизведения
        System.out.println("PAUSE");
        // Проверка, существует ли объект audioPlayer
        if (this.audioPlayer != null) this.audioPlayer.setPaused(true);
    }

    // Метод для остановки воспроизведения аудиофайла
    @FXML private void stop() {
        // Вывод сообщения о остановке воспроизведения
        System.out.println("STOP");
        // Проверка, существует ли объект audioPlayer
        if (this.audioPlayer != null) this.audioPlayer.setStopped(true);
    }

    // Метод для закрытия ресурсов и завершения программы
    @FXML private void clickClose() {
        // Проверка, существует ли объект audioPlayer
        if (this.audioPlayer != null) {
            // Проверка, существует ли поток воспроизведения и прерывание его работы
            if (this.playThread != null) this.playThread.interrupt();
            // Закрытие ресурсов аудиофайла
            this.audioPlayer.closeResources();
        }
        // Завершение программы
        System.exit(0);
    }
}

