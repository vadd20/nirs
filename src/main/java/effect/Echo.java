package effect;

public class Echo extends Effect {
    private double degrees = 0;

    public Echo() {
    }

    public void setInputSampleStream(short[] inputAudioStream) {
        this.inputAudioStream = inputAudioStream;
    }

    @Override
    public synchronized short[] createEffect() {
        for (int i = 0; i < this.inputAudioStream.length; i++) {
            this.inputAudioStream[i] *= Math.sin(this.degrees);
        }
        this.degrees += 45;
        return this.inputAudioStream;
    }
}