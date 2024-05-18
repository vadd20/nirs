package equalizer;

import java.util.concurrent.Callable;

public class FilterIir implements Callable<short[]> {
    private short[] inputSignal;
    private short[] outputSignal;
    private double[] feedbackSignal;
    private double gain;
    private double[] coffsNumFilter;
    private double[] coffsDenFilter;
    private int count_coffs;

    public void settings(final short[] inputSignal, final double[] coffsNumFilter, final double[] coffsDenFilter) {
        this.inputSignal = inputSignal;
        this.coffsNumFilter = coffsNumFilter;
        this.coffsDenFilter = coffsDenFilter;
        this.outputSignal = new short[inputSignal.length];
        this.count_coffs = coffsNumFilter.length;
        this.feedbackSignal =  new double[inputSignal.length];
    }

    private void convolution() {
        // Инициализация временной переменной для хранения промежуточных результатов свертки.
        double tmp;
        // Проходим по всем элементам входного сигнала.
        for(int i = 0; i <  this.inputSignal.length; i++) {
            tmp = 0;
            // Выполняем свертку для каждого элемента входного сигнала.
            for(int j = 0; j < this.count_coffs; j++) {
                // Проверяем, что задержка от обратной связи не приводит к отрицательному индексу.
                if (i - j >= 0) {
                    // Вычисляем свертку с учетом коэффициентов фильтра и обратной связи.
                    tmp += coffsNumFilter[j] * this.inputSignal[i - j];
                    tmp -= this.coffsDenFilter[j] * this.feedbackSignal[i - j];
                }
            }
            // Сохраняем результат свертки в обратную связь для использования на следующем шаге.
            this.feedbackSignal[i] = tmp;
            // Применяем коэффициент усиления к результату свертки и добавляем его к выходному сигналу.
            this.outputSignal[i] += (short) (this.gain * (short)(tmp / 10)); // Делим на 10, чтобы избежать перегрузки на пересечении фильтров.
        }
    }


    public void setGain(double gain) {
        this.gain = gain;
    }

    @Override
    public short[] call() {
        this.convolution();
        return this.outputSignal;
    }
}
