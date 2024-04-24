package effect;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Clipping extends Effect implements Callable<short[]> {

    // Метод для применения эффекта клиппинга к аудио сэмплам
    public short[] applyClipping(short[] audioSamples, short maxAmplitude) {
        short[] processedSamples = new short[audioSamples.length];

        for (int i = 0; i < audioSamples.length; i++) {
            // Если амплитуда сэмпла выше максимального порога, устанавливаем сэмпл на максимальное значение
            if (audioSamples[i] > maxAmplitude) {
                processedSamples[i] = maxAmplitude;
            }
            // Если амплитуда сэмпла ниже минимального порога, устанавливаем сэмпл на минимальное значение
            else if (audioSamples[i] < -maxAmplitude) {
                processedSamples[i] = (short) -maxAmplitude;
            }
            // В противном случае сэмпл остается без изменений
            else {
                processedSamples[i] = audioSamples[i];
            }
        }

        return processedSamples;
    }

    @Override
    public short[] createEffect() throws InterruptedException, ExecutionException {
        return new short[0];
    }

    @Override
    public short[] call() throws Exception {
        return new short[0];
    }

    public Clipping() {

    }
}
