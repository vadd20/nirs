package equalizer;

import java.util.concurrent.Callable;

public class FilterIir implements Callable<short[]> {
    private short[] inputSignal;
    private short[] outputSignal;
    private double[] feedbackSignal;
    private double gain;
    private double[][] coffsNumFilter;
    private double[][] coffsDenFilter;
    private int count_coffs;

    public void settings(final short[] inputSignal, final double[][] coffsNumFilter, final double[][] coffsDenFilter) {
        this.inputSignal = inputSignal;
        this.coffsNumFilter = coffsNumFilter;
        this.coffsDenFilter = coffsDenFilter;
        this.outputSignal = new short[inputSignal.length];
        this.count_coffs = coffsNumFilter.length;
        this.feedbackSignal =  new double[inputSignal.length];

    }

    private void convolution() {
        int numSections = this.count_coffs;
        int inputLength = this.inputSignal.length;

        // Проходим по всем элементам входного массива
        for (int i = 0; i < inputLength; i++) {
            // Инициализируем выходное значение текущего элемента
            double y = 0;

            // Проходим по всем секциям фильтра
            for (int j = 1; j < numSections; j += 2) {
                // Получаем коэффициенты для текущей секции
                double b0 = this.coffsNumFilter[j][0];
                double b1 = this.coffsNumFilter[j][1];
                double b2 = this.coffsNumFilter[j][2];
                double a0 = this.coffsDenFilter[j][0];
                double a1 = this.coffsDenFilter[j][1];
                double a2 = this.coffsDenFilter[j][2];

                // Вычисляем выходное значение текущей секции
                if (i >= 2) {
                    y = b0 * this.inputSignal[i] + b1 * this.inputSignal[i - 1] + b2 * this.inputSignal[i - 2]
                            - a1 * this.outputSignal[i - 1] - a2 * this.outputSignal[i - 2];
                } else if (i == 1) {
                    y = b0 * this.inputSignal[i] + b1 * this.inputSignal[i - 1]
                            - a1 * this.outputSignal[i - 1];
                } else {
                    y = b0 * this.inputSignal[i];
                }

                // Обновляем выходное значение для текущего элемента
                this.outputSignal[i] += this.gain * (short)(y / numSections / 6);
            }
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
