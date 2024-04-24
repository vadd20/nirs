package effect;

import player.AudioPlayerAlternative;

public class AlternativeBuffer {

    private final byte[] primaryBuffer;
    private final byte[] secondaryBuffer;
    private int writeIndex = 0;
    private int readIndex = 0;
    private boolean primaryActive = true;
    private boolean writeBufferFull = false;
    private boolean readBufferEmpty = true;

    public AlternativeBuffer() {
        primaryBuffer = new byte[AudioPlayerAlternative.BUFF_SIZE_BYTES];
        secondaryBuffer = new byte[AudioPlayerAlternative.BUFF_SIZE_BYTES];
    }

    public synchronized void write(byte[] data, int length) throws InterruptedException {
        byte[] activeBuffer = primaryActive ? primaryBuffer : secondaryBuffer;
        while (writeBufferFull) {
            wait(); // Ожидаем, пока не будет прочитан буфер
        }
        System.arraycopy(data, 0, activeBuffer, writeIndex, length);
        writeIndex += length;
        if (writeIndex >= activeBuffer.length) {
            writeIndex = 0;
            writeBufferFull = true;
            readBufferEmpty = false;
            notifyAll();
        }
    }

    public synchronized byte[] read(int length) throws InterruptedException {
        while (readBufferEmpty) {
            wait(); // Ожидаем, пока буфер не заполнится
        }
        byte[] activeBuffer = primaryActive ? secondaryBuffer : primaryBuffer;
        byte[] data = new byte[length];
        System.arraycopy(activeBuffer, readIndex, data, 0, length);
        readIndex += length;
        if (readIndex >= activeBuffer.length) {
            readIndex = 0;
            readBufferEmpty = true;
            writeBufferFull = false;
            primaryActive = !primaryActive; // Переключаем буферы
            notifyAll();
        }
        return data;
    }

    public synchronized boolean hasMoreData() {
        return !readBufferEmpty;
    }
}
