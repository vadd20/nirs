package player;

import effect.CircularBuffer;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

// Класс для воспроизведения аудиофайлов
public class AudioPlayerSecond {
    private final File currentMusicFile; // Текущий воспроизводимый аудиофайл

    // Геттеры и сеттеры для различных полей класса

    private AudioInputStream audioStream; // Поток аудиоданных из файла
    private SourceDataLine sourceDataLine; // Интерфейс для вывода аудиоданных на звуковую карту
    private volatile boolean isPlaying; // Флаг, указывающий, воспроизводится ли аудиофайл
    private volatile boolean isPaused; // Флаг, указывающий, находится ли воспроизведение на паузе
    private volatile boolean isStopped; // Флаг, указывающий, остановлено ли воспроизведение
    private Thread playThread; // Поток для воспроизведения аудио
    public static final int BUFF_SIZE_BYTES = 1024; // Размер буфера для аудиоданных в байтах
    public static final int BUFF_SIZE = 1024; // Размер буфера для аудиоданных

    private final byte[] bufferBytes = new byte[BUFF_SIZE_BYTES]; // Буфер для хранения аудиоданных
    private CircularBuffer circularBuffer; // Кольцевой буфер для хранения аудиоданных

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    // Конструктор класса
    public AudioPlayerSecond(File musicFile) {
        this.currentMusicFile = musicFile;
        this.isPlaying = false;
        this.isPaused = false;
        this.isStopped = false;
        // Инициализация кольцевого буфера с заданным размером
        this.circularBuffer = new CircularBuffer(BUFF_SIZE + 1);
        init(); // Инициализация аудиопотока и звуковой карты
    }

    // Инициализация аудиопотока и звуковой карты
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
        if (isPlaying && !isPaused) {
            return; // Если уже воспроизводится и не на паузе, ничего не делаем
        }

        if (isPlaying) { // Если уже воспроизводится, но на паузе
            resume(); // Возобновляем воспроизведение
            return;
        }

        isPlaying = true;
        isStopped = false;
        isPaused = false;

        playThread = new Thread(() -> {
            try {
                int bytesRead;
                while (!isStopped) {
                    if (isPaused) {
                        synchronized (this) {
                            while (isPaused && !isStopped) {
                                wait(); // Ожидаем возобновления воспроизведения
                            }
                        }
                    }

                    // Чтение данных и добавление их в кольцевой буфер
                    if ((bytesRead = audioStream.read(bufferBytes)) != -1 && !isPaused) {
                        circularBuffer.enqueue(bufferBytes, bytesRead);
                    }

                    // Воспроизведение данных из кольцевого буфера
                    while (!circularBuffer.isEmpty() && !isPaused && !isStopped) {
                        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
                        while (!circularBuffer.isEmpty() && bufferStream.size() < BUFF_SIZE) {
                            bufferStream.write(circularBuffer.dequeue());
                        }
                        byte[] dataToPlay = bufferStream.toByteArray(); // Преобразуем данные в массив
                        sourceDataLine.write(dataToPlay, 0, dataToPlay.length); // Воспроизводим
                    }
                }
                sourceDataLine.drain();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeResources(); // Закрываем ресурсы после окончания воспроизведения
            }
        });
        playThread.start();
    }

    // Метод для постановки воспроизведения на паузу
    public void pause() {
        isPaused = true;
    }

    // Метод для возобновления воспроизведения
    public synchronized void resume() {
        isPaused = false;
        notifyAll(); // Возобновляем поток воспроизведения
    }

    // Метод для остановки воспроизведения
    public void stop() {
        isStopped = true;
        isPlaying = false;
        resume(); // Если на паузе, возобновляем для корректной остановки
    }

    // Метод для закрытия ресурсов
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
}
