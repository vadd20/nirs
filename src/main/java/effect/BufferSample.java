package effect;

import static player.AudioPlayer.BUFF_SIZE;

public class BufferSample {
    // Константа, определяющая размер каждого отдельного сэмпла в буфере
    public static final int SAMPLE_SIZE = BUFF_SIZE / 2;

    // Массив для хранения отсчетов амплитуд звуковых сэмплов
    private final short[][] sampleDelays;

    // Размер массива (количество сэмплов)
    private final int sizeArray;

    // Индекс текущего элемента (сэмпла) в буфере
    private int indexCurrentElement;

    // Конструктор класса BufferSample
    public BufferSample(int sizeArray) {
        // Инициализация массива для хранения сэмплов
        this.sampleDelays = new short[sizeArray][SAMPLE_SIZE];

        // Установка размера массива (количества сэмплов)
        this.sizeArray = sizeArray;

        // Установка начального значения индекса текущего элемента
        this.indexCurrentElement = 0;
    }

    // Метод для получения индекса текущего элемента
    public int getIndexCurrentElement() {
        return indexCurrentElement;
    }

    // Метод для добавления нового сэмпла в буфер
    public void add(short[] sample) {
        // Обновление индекса текущего элемента с учетом кольцевого буфера
        this.indexCurrentElement = (this.indexCurrentElement + 1) % this.sizeArray;

        // Копирование амплитуд сэмпла в соответствующий элемент массива
        System.arraycopy(sample, 0, this.sampleDelays[this.indexCurrentElement], 0, SAMPLE_SIZE);

        // Уменьшение амплитуд каждого отсчета во всех сэмплах
        for (int i = 0; i < this.sizeArray; i++) {
            for (int j = 0; j < SAMPLE_SIZE; j++) {
                this.sampleDelays[i][j] *= 0.9;
            }
        }
    }

    // Метод для получения амплитуды задержки конкретного сэмпла по его индексу и индексу амплитуды
    public short getAmplitudeSampleDelay(int indexSample, int indexAmplitude) {
        return sampleDelays[indexSample][indexAmplitude];
    }
}
