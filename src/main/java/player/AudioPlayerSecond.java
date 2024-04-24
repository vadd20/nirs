package player;

import effect.CircularBuffer;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;


public class AudioPlayerSecond {
    private final File currentMusicFile;

    public File getCurrentMusicFile() {
        return currentMusicFile;
    }

    public AudioInputStream getAudioStream() {
        return audioStream;
    }

    public void setAudioStream(AudioInputStream audioStream) {
        this.audioStream = audioStream;
    }

    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    public void setSourceDataLine(SourceDataLine sourceDataLine) {
        this.sourceDataLine = sourceDataLine;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public Thread getPlayThread() {
        return playThread;
    }

    public void setPlayThread(Thread playThread) {
        this.playThread = playThread;
    }

    public byte[] getBufferBytes() {
        return bufferBytes;
    }

    private AudioInputStream audioStream;
    private SourceDataLine sourceDataLine;
    private volatile boolean isPlaying;
    private volatile boolean isPaused;
    private volatile boolean isStopped;
    private Thread playThread;
    public static final int BUFF_SIZE_BYTES = 4096;
    public static final int BUFF_SIZE = 4096;
    private final byte[] bufferBytes = new byte[BUFF_SIZE_BYTES];
    private CircularBuffer circularBuffer; // Добавляем поле для кольцевого буфера

    public AudioPlayerSecond(File musicFile) {
        this.currentMusicFile = musicFile;
        this.isPlaying = false;
        this.isPaused = false;
        this.isStopped = false;
        // Инициализация кольцевого буфера с выбранным размером
        this.circularBuffer = new CircularBuffer(BUFF_SIZE + 1); // Размер буфера, +1 -- костыль
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
                                wait(); // Ожидаем возобновления
                            }
                        }
                    }

                    // Чтение данных и добавление их в буфер
                    if ((bytesRead = audioStream.read(bufferBytes)) != -1 && !isPaused) {
                        circularBuffer.enqueue(bufferBytes, bytesRead);
                    }

                    // Воспроизведение данных из буфера
                    while (!circularBuffer.isEmpty() && !isPaused && !isStopped) {
                        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
                        while (!circularBuffer.isEmpty() && bufferStream.size() < BUFF_SIZE) {
                            bufferStream.write(circularBuffer.dequeue());
                        }
                        byte[] dataToPlay = bufferStream.toByteArray(); // Преобразуем накопленные данные в массив
                        sourceDataLine.write(dataToPlay, 0, dataToPlay.length); // Воспроизводим
                    }
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



    public void pause() {
        isPaused = true;
    }

    public synchronized void resume() {
        isPaused = false;
        notifyAll(); // Возобновляем поток воспроизведения
    }

    public void stop() {
        isStopped = true;
        isPlaying = false;
        resume(); // Если на паузе, возобновляем для корректной остановки
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
    }
}
