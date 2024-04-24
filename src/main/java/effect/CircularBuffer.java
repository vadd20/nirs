package effect;

public class CircularBuffer {
    private byte[] buffer;
    private int head = 0;
    private int tail = 0;
    private int capacity;
    private boolean isEmpty = true;

    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new byte[capacity];
    }

    public synchronized void enqueue(byte data) {
        buffer[tail] = data;
        tail = (tail + 1) % capacity;
        if (tail == head) { // Если после добавления tail сравнялся с head, значит буфер был заполнен и мы перезаписали "старые" данные
            head = (head + 1) % capacity; // Перемещаем head дальше, теряя самые "старые" данные
        }
        isEmpty = false; // Буфер точно не пустой после добавления элемента
    }

    public synchronized void enqueue(byte[] data, int length) {
        for (int i = 0; i < length; i++) {
            enqueue(data[i]);
        }
    }

    public synchronized byte dequeue() {
        if (!isEmpty()) {
            byte data = buffer[head];
            head = (head + 1) % capacity;
            if (head == tail) {
                isEmpty = true;
            }
            return data;
        } else {
            throw new IllegalStateException("Buffer is empty");
        }
    }

    public synchronized boolean isEmpty() {
        return isEmpty;
    }

    public synchronized boolean isFull() {
        return !isEmpty && head == tail;
    }
}
