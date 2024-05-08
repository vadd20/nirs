package effect;

public class Envelope extends Effect {
    private double degrees = 0;

    public Envelope() {
    }

    public void setInputSampleStream(short[] inputAudioStream) {
        this.inputAudioStream = inputAudioStream;
    }

    @Override
    public synchronized short[] createEffect() {
        for (int i = 0; i < this.inputAudioStream.length; i++) {
            this.inputAudioStream[i] *= Math.sin(this.degrees);
        }
        this.degrees += 30;
        return this.inputAudioStream;
    }
}