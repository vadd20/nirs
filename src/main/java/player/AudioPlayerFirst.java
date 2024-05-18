package player;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class AudioPlayerFirst {
    // Файл текущей проигрываемой музыки
    private final File currentMusicFile;
    // Переменные для управления состоянием воспроизведения
    private volatile boolean isPlaying;
    private volatile boolean isPaused;
    private volatile boolean isStopped;
    // Поток для воспроизведения
    private Thread playThread;
    // Потоковый буфер для передачи данных аудиопотоку
    public static final int BUFF_SIZE = 4096;
    private final byte[] bufferBytes = new byte[BUFF_SIZE];
    // Поток для чтения аудиоданных из файла
    private AudioInputStream audioStream;
    // Поток для передачи аудиоданных на устройство вывода
    private SourceDataLine sourceDataLine;

    // Конструктор класса, инициализирует переменные и открывает потоки для проигрывания аудио
    public AudioPlayerFirst(File musicFile) {
        this.currentMusicFile = musicFile;
        this.isPlaying = false;
        this.isPaused = false;
        this.isStopped = false;
        init();
    }

    // Метод для инициализации аудиопотоков
    private void init() {
        try {
            audioStream = AudioSystem.getAudioInputStream(currentMusicFile);
            AudioFormat format = audioStream.getFormat();
            sourceDataLine = AudioSystem.getSourceDataLine(format);
            sourceDataLine.open(format);
            sourceDataLine.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Метод для воспроизведения аудио
    public void play() {
        if (isPlaying && !isPaused) return; // Если уже играет и не на паузе, то выходим
        if (isPlaying) {
            resume(); // Если на паузе, то продолжаем
            return;
        }
        isStopped = false;
        isPlaying = true;
        playThread = new Thread(() -> {
            try {
                int bytesRead;
                while (!isStopped && (bytesRead = audioStream.read(bufferBytes)) != -1) {
                    if (isPaused) {
                        synchronized (this) {
                            while (isPaused && !isStopped) wait();
                        }
                    }
                    if (!isStopped) sourceDataLine.write(bufferBytes, 0, bytesRead);
                }
                sourceDataLine.drain();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeResources();
            }
        });
        playThread.start();
    }

    // Метод для приостановки воспроизведения аудио
    public void pause() {
        isPaused = true;
    }

    // Метод для возобновления воспроизведения аудио
    public synchronized void resume() {
        isPaused = false;
        notifyAll(); // Возобновляем поток воспроизведения
    }

    // Метод для остановки воспроизведения аудио
    public void stop() {
        isStopped = true;
        isPlaying = false;
        resume(); // Если на паузе, возобновляем для корректной остановки
    }

    // Метод для закрытия ресурсов аудио
    public void closeResources() {
        if (audioStream != null) {
            try {
                audioStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sourceDataLine != null) {
            sourceDataLine.close();
        }
    }

    // Геттеры и сеттеры
    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }
}
