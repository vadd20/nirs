package equalizer;
import java.util.concurrent.Callable;

public class Filter implements Callable<short[]> {
    private short[] inputSignal; // Входной сигнал
    private short[] outputSignal; // Выходной сигнал
    private double gain; // Коэффициент усиления
    private double[] coffsNumFilter; // Коэффициенты фильтра
    private int count_coffs; // Количество коэффициентов фильтра

    // Метод для установки настроек фильтрации
    public void settings(final short[] inputSignal, final double[] coffsNumFilter) {
        this.inputSignal = inputSignal;
        this.coffsNumFilter = coffsNumFilter;
        this.outputSignal = new short[inputSignal.length];
        this.count_coffs = coffsNumFilter.length;
    }

    // Метод для вычисления свертки сигнала с фильтром
    private void convolution() {
        double tmp;
        for(int i = 0; i < this.inputSignal.length; i++) {
            tmp = 0;
            for(int j = 0; j < this.count_coffs; j++) {
                if(i - j >= 0)
                    tmp += coffsNumFilter[j] * this.inputSignal[i - j];
            }
            // Умножение на коэффициент усиления и добавление к выходному сигналу
            this.outputSignal[i] += (short) (this.gain * (short)(tmp / 10));
            // Делим на 10, чтобы избежать перегрузки на пересечении фильтров
        }
    }

    // Метод для установки коэффициента усиления
    public void setGain(double gain) {
        this.gain = gain;
    }

    @Override
    // Метод, вызываемый при вызове экземпляра в качестве задачи
    public short[] call() {
        this.convolution();
        return this.outputSignal;
    }
}
