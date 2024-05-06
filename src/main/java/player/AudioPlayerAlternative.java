package player;

import effect.AlternativeBuffer;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class AudioPlayerAlternative {

    private final File currentMusicFile;

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    private AudioInputStream audioStream;
    private SourceDataLine sourceDataLine;
    private volatile boolean isPlaying;
    private volatile boolean isPaused;
    private volatile boolean isStopped;
    private Thread playThread;
    public static final int BUFF_SIZE_BYTES = 4096; //Todo
    private final AlternativeBuffer buffer;

    public AudioPlayerAlternative(File musicFile) {
        this.currentMusicFile = musicFile;
        this.isPlaying = false;
        this.isPaused = false;
        this.isStopped = false;
        this.buffer = new AlternativeBuffer(); // используем альтернативный буфер
        init();
    }

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

    // Методы для управления состояниями
    public synchronized boolean isPlaying() {
        return isPlaying;
    }

    public synchronized boolean isPaused() {
        return isPaused;
    }

    public void play() {
        if (isStopped) {
            init();  // Переинициализация ресурсов если остановлено
            isStopped = false;
        }
        if (!isPlaying) {
            isPlaying = true;
            playThread = new Thread(() -> {
                try {
                    byte[] localBuffer = new byte[BUFF_SIZE_BYTES];
                    int bytesRead;
                    while (!isStopped && !Thread.interrupted()) {
                        synchronized (this) {
                            while (isPaused) {
                                wait();  // Ожидание возобновления
                            }
                        }
                        if (isStopped) break;  // Выход, если остановлено

                        bytesRead = audioStream.read(localBuffer, 0, localBuffer.length);
                        if (bytesRead == -1) break;  // Конец файла

                        buffer.write(localBuffer, bytesRead);  // Запись в буфер
                        byte[] dataToPlay = buffer.read(BUFF_SIZE_BYTES);
                        sourceDataLine.write(dataToPlay, 0, dataToPlay.length);
                    }
                    sourceDataLine.drain();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // Восстановление прерванного статуса
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeResources();
                }
            });
            playThread.start();
        }
    }

    public synchronized void pause() {
        if (isPlaying && !isPaused) {
            isPaused = true;
        }
    }


    public synchronized void resume() {
        if (isPlaying && isPaused) {
            isPaused = false;
            notifyAll();  // Уведомляем поток, что он может продолжить воспроизведение
        }
    }


    public synchronized void stop() {
        isStopped = true;
        isPlaying = false;
        notifyAll();  // Уведомляем поток воспроизведения, чтобы он мог корректно завершиться
        closeResources();  // Закрываем ресурсы
    }

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
        audioStream = null;
        sourceDataLine = null;
    }
}
