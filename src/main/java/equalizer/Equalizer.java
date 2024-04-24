package equalizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import player.AudioPlayer;

public class Equalizer {
    private short[]outputSignal;
    private Filter[] filters;
    private FilterIir[] filtersIir;
    private final static char COUNT_OF_THREADS = 4;
    ExecutorService pool;

    public Equalizer() {
        pool = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        this.createFilters();
    }

    public void setInputSignal(short[] inputSignal) {
        this.outputSignal = new short[inputSignal.length];
        this.filters[0].settings(inputSignal, FilterInfo.COFFS_NUM_OF_BAND_0);
        this.filters[1].settings(inputSignal, FilterInfo.COFFS_NUM_OF_BAND_1);
        this.filters[2].settings(inputSignal, FilterInfo.COFFS_NUM_OF_BAND_2);
        this.filters[3].settings(inputSignal, FilterInfo.COFFS_NUM_OF_BAND_3);
        this.filters[4].settings(inputSignal, FilterInfo.COFFS_NUM_OF_BAND_4);
        this.filters[5].settings(inputSignal, FilterInfo.COFFS_NUM_OF_BAND_5);
        this.filtersIir[0].settings(inputSignal,FilterInfoIIR.COFFS_NUM_OF_BAND_0, FilterInfoIIR.COFFS_DEN_OF_BAND_0);
        this.filtersIir[1].settings(inputSignal,FilterInfoIIR.COFFS_NUM_OF_BAND_1, FilterInfoIIR.COFFS_DEN_OF_BAND_1);
        this.filtersIir[2].settings(inputSignal,FilterInfoIIR.COFFS_NUM_OF_BAND_2, FilterInfoIIR.COFFS_DEN_OF_BAND_2);
        this.filtersIir[3].settings(inputSignal,FilterInfoIIR.COFFS_NUM_OF_BAND_3, FilterInfoIIR.COFFS_DEN_OF_BAND_3);
        this.filtersIir[4].settings(inputSignal,FilterInfoIIR.COFFS_NUM_OF_BAND_4, FilterInfoIIR.COFFS_DEN_OF_BAND_4);
        this.filtersIir[5].settings(inputSignal,FilterInfoIIR.COFFS_NUM_OF_BAND_5, FilterInfoIIR.COFFS_DEN_OF_BAND_5);
    }

    private void createFilters() {
        this.filters = new  Filter [FilterInfo.COUNT_OF_BANDS];
        for (int i = 0; i < FilterInfo.COUNT_OF_BANDS; i++)
            this.filters[i] = new Filter();
        this.filtersIir = new FilterIir [FilterInfoIIR.COUNT_OF_BANDS];
        for (int i = 0; i < FilterInfoIIR.COUNT_OF_BANDS; i++) {
            this.filtersIir[i] = new FilterIir();
        }
    }

    public void equalization() throws InterruptedException, ExecutionException {
        Future<short[]>[] fs = new Future[FilterInfo.COUNT_OF_BANDS];
        try {
            for (int i = 0; i < FilterInfo.COUNT_OF_BANDS; i++) {
                if (!AudioPlayer.isIirEnabled) {
                    fs[i] = pool.submit(this.filters[i]);
                } else {
                    fs[i] = pool.submit(this.filtersIir[i]);
                }
            }

            for (int i = 0; i < this.outputSignal.length; i++) {
                this.outputSignal[i] = 0; // Обнуление сигнала перед добавлением
                for (int j = 0; j < fs.length; j++) {
                    this.outputSignal[i] += fs[j].get()[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Вывод ошибок в консоль для отладки
            throw e;
        }
    }


    public short[] getOutputSignal() {
        return this.outputSignal;
    }

    public Filter getFilter(int numberFilter) {
        return this.filters[numberFilter];
    }

    public FilterIir getFilterIir(int numberFilter) {
        return this.filtersIir[numberFilter];
    }

    public void close() {
        if(this.pool != null) {
            this.pool.shutdown();
        }
    }
}
