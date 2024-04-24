package equalizer;

import java.util.concurrent.Callable;

public class FilterIir implements Callable<short[]> {
    private short[] inputSignal;
    private short[] outputSignal;
    private short[] feedbackSignal; // Добавлено для хранения обратной связи
    private double gain;
    private double[] filterCoeffs; // коэффициенты прямой связи
    private double[] feedbackCoeffs; // коэффициенты обратной связи
    private int coeffsNumber;

    public void settings(final short[] inputSignal, final double[] filterCoeffs, final double[] feedbackCoeffs) {
        this.inputSignal = inputSignal;
        this.filterCoeffs = filterCoeffs;
        this.feedbackCoeffs = feedbackCoeffs;
        this.outputSignal = new short[inputSignal.length];
        this.feedbackSignal = new short[inputSignal.length]; // Инициализация с тем же размером
        this.coeffsNumber = filterCoeffs.length;
    }

    private void convolution() {
        double tmp;
        for(int i = 0; i <  this.inputSignal.length; i++) {
            tmp = 0;
            for(int j = 0; j < this.coeffsNumber; j++) {
                if(i - j >= 0)
                    tmp += filterCoeffs[j] * this.inputSignal[i - j];
            }
            this.outputSignal[i] += (short) (this.gain * (short)(tmp / 8)); //делим на 8, чтобы не было перегруза на пересечении фильтров
        }
    }

    private void convolutionIir() {
        double tmp;
        for(int i = 0; i < this.inputSignal.length; i++) {
            tmp = 0;
            for(int j = 0; j < coeffsNumber; j++) {
                if (i - j >= 0) {
                    tmp += filterCoeffs[j] * this.inputSignal[i - j];
                    if (j < feedbackCoeffs.length) {
                        tmp -= feedbackCoeffs[j] * this.feedbackSignal[i - j];
                    }
                }
            }
            this.feedbackSignal[i] = (short)tmp;
            this.outputSignal[i] = (short)(tmp * gain); // Умножение на gain для регулировки громкости
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
