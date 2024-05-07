//package effect;
//import java.util.Random;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import static effect.Delay.DEFAULT_SIZE_BUFFER;
//public class Envelope extends Effect {
//    ExecutorService pool;
//    public static final int DEFAULT_COUNT_DELAYS = 4;
//    private final int countDelays;
//    private Delay[] delays;
//    private void createDelays() {
//        Random random = new Random();
//        this.delays = new Delay[countDelays];
//        for (int i = 0; i < this.countDelays; i++) {
//            this.delays[i] = new Delay(random.nextInt(2, DEFAULT_SIZE_BUFFER +
//                    1));
//        }
//    }
//    public Envelope(int countDelays) {
//        super();
//        this.countDelays = countDelays;
//        createDelays();
//        pool = Executors.newFixedThreadPool(this.countDelays);
//    }
//    public Envelope() {
//        super();
//        this.countDelays = DEFAULT_COUNT_DELAYS;
//        createDelays();
//        pool = Executors.newFixedThreadPool(this.countDelays);
//    }
//    public void setInputSampleStream(short[] inputAudioStream) {
//        this.inputAudioStream = inputAudioStream;
//        for (int i = 0; i < this.countDelays; i++) {
//            this.delays[i].inputAudioStream = inputAudioStream;
//        }
//    }
//    @Override
//    public synchronized short[] createEffect() throws InterruptedException,
//            ExecutionException {
//        Future<short[]>[] delays = new Future[this.countDelays];
//        for (int i = 0; i < this.countDelays; i++) {
//            delays[i] = pool.submit(this.delays[i]);
//        }
//        for (int i = 0; i < this.countDelays; i++) {
//            for (int j = 0; j < this.inputAudioStream.length; j++) {
//                this.inputAudioStream[j] += delays[i].get()[j] /
//                        this.countDelays;
//            }
//        }
//        return this.inputAudioStream;
//    }
//}

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