package effect;

public class Vibrato extends Effect {
    private static final double DEFAULT_DEPTH = 0.005; // Глубина модуляции (в долях частоты)
    private static final double DEFAULT_RATE = 5.0; // Скорость модуляции в Гц

    private double depth = DEFAULT_DEPTH;
    private double rate = DEFAULT_RATE;
    private double sampleRate = 48000; // Частота дискретизации аудио

    public Vibrato() {
    }

    // Метод для установки входного аудиопотока
    public void setInputSampleStream(short[] inputAudioStream) {
        this.inputAudioStream = inputAudioStream;
    }

    // Создание эффекта вибрато
    @Override
    public synchronized short[] createEffect() {
        if (inputAudioStream == null) {
            throw new IllegalStateException("Input sample stream must be set before calling createEffect.");
        }

        short[] outputAudioStream = new short[inputAudioStream.length];
        double omega = 2 * Math.PI * this.rate / this.sampleRate;
        double maxDelay = this.depth * this.sampleRate;

        for (int i = 0; i < inputAudioStream.length; i++) {
            double currentDelay = Math.sin(omega * i) * maxDelay;
            int previousSampleIndex = (int) Math.floor(i - currentDelay);
            int nextSampleIndex = previousSampleIndex + 1;
            double fractional = i - currentDelay - previousSampleIndex;

            if (previousSampleIndex < 0 || nextSampleIndex >= inputAudioStream.length) {
                outputAudioStream[i] = 0; // Handle boundary cases by setting to zero or mirroring
            } else {
                // Linear interpolation for a smoother effect
                outputAudioStream[i] = (short) (
                        inputAudioStream[previousSampleIndex] * (1 - fractional) +
                                inputAudioStream[nextSampleIndex] * fractional
                );
            }
        }

        return outputAudioStream;
    }

}
