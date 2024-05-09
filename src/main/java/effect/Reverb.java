package effect;

public class Reverb extends Effect {
    private static final double FEEDBACK = 0.5; // Коэффициент обратной связи
    private static final double MIX = 0.3; // Уровень смешения оригинального сигнала с эффектом
    private static final int DELAY_LENGTH = 48000; // Задержка в сэмплах

    private short[] delayBuffer;
    private int delayIndex;

    public Reverb() {
        this.delayBuffer = new short[DELAY_LENGTH];
        this.delayIndex = 0;
    }

    // Метод для установки входного аудиопотока
    public void setInputAudioStream(short[] inputStream) {
        this.inputAudioStream = inputStream;
    }

    @Override
    public synchronized short[] createEffect() {
        short[] outputAudioStream = new short[inputAudioStream.length];

        for (int i = 0; i < inputAudioStream.length; i++) {
            int delayedIndex = (delayIndex + i) % DELAY_LENGTH;
            short delayedSample = delayBuffer[delayedIndex];
            short inputSample = inputAudioStream[i];

            // Смешиваем задержанный сигнал с текущим сэмплом
            short newSample = (short)((inputSample + delayedSample * MIX) / (1 + MIX));
            outputAudioStream[i] = newSample;

            // Обновляем буфер задержки с учетом обратной связи
            delayBuffer[delayedIndex] = (short)(newSample + delayedSample * FEEDBACK);
        }

        delayIndex = (delayIndex + inputAudioStream.length) % DELAY_LENGTH;

        return outputAudioStream;
    }
}
