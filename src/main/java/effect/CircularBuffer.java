package effect;

public class CircularBuffer {
    private byte[] buffer; // Массив для хранения данных
    private int head = 0; // Индекс начала данных в буфере
    private int tail = 0; // Индекс конца данных в буфере
    private int capacity; // Общая ёмкость буфера
    private boolean isEmpty = true; // Флаг, указывающий, пустой ли буфер

    // Конструктор для инициализации буфера заданной ёмкостью
    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new byte[capacity]; // Создание массива заданной ёмкости
    }

    // Метод для добавления одного элемента в буфер
    public synchronized void enqueue(byte data) {
        buffer[tail] = data; // Добавляем данные в конец буфера
        tail = (tail + 1) % capacity; // Увеличиваем индекс конца, с учётом цикличности буфера
        if (tail == head) { // Если индексы конца и начала сравнялись, значит буфер заполнился и мы перезаписываем "старые" данные
            head = (head + 1) % capacity; // Перемещаем индекс начала дальше, теряя самые "старые" данные
        }
        isEmpty = false; // Устанавливаем флаг, что буфер точно не пустой после добавления элемента
    }

    // Метод для добавления массива данных в буфер
    public synchronized void enqueue(byte[] data, int length) {
        for (int i = 0; i < length; i++) { // Добавляем каждый элемент массива в буфер
            enqueue(data[i]);
        }
    }

    // Метод для извлечения одного элемента из буфера
    public synchronized byte dequeue() {
        if (!isEmpty()) { // Проверяем, не пуст ли буфер
            byte data = buffer[head]; // Извлекаем данные из начала буфера
            head = (head + 1) % capacity; // Увеличиваем индекс начала, с учётом цикличности буфера
            if (head == tail) { // Если индексы конца и начала сравнялись, значит буфер опустошен
                isEmpty = true; // Устанавливаем флаг, что буфер пуст
            }
            return data; // Возвращаем извлечённые данные
        } else {
            throw new IllegalStateException("Buffer is empty"); // Если буфер пуст, выбрасываем исключение
        }
    }

    // Метод для проверки, пуст ли буфер
    public synchronized boolean isEmpty() {
        return isEmpty; // Возвращаем флаг, указывающий, пуст ли буфер
    }
}
