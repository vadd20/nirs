package equalizer;

import java.util.concurrent.Callable;

public class FilterThird implements Callable<short[]> {
    private short[] inputSignal;
    private short[] outputSignal;
    private double gain;
    private double[] coffsNumFilter;
    private int count_coffs;


    private void convolution() {
        double tmp;
        for(int i = 0; i <  this.inputSignal.length; i++) {
            tmp = 0;
            for(int j = 0; j < this.count_coffs; j++) {
                if(i - j >= 0)
                    tmp += coffsNumFilter[j] * this.inputSignal[i - j];
            }
            this.outputSignal[i] += (short) (this.gain * (short)(tmp / 10)); //делим на 10, чтобы не было перегруза на пересечении фильтров
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
