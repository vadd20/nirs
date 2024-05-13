package player;

import effect.Reverb;
import effect.Vibrato;
import equalizer.Equalizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class AudioPlayer {

    private final File currentMusicFile;
    private AudioInputStream audioStream;
    private SourceDataLine sourceDataLine;
    public static final int BUFF_SIZE = 1000000;
    private final byte[] bufferBytes = new byte[BUFF_SIZE];
    private short[] bufferShort = new short[BUFF_SIZE / 2];
    private boolean pauseStatus;
    private boolean stopStatus;
    private double gain;
    private final Equalizer equalizer;
    private final Reverb reverb;
    private final Vibrato vibrato;
    private boolean isVibrato;
    private boolean reverbEnabled = false;
    public static boolean isIirEnabled = false;

    public void setIirEnabled(boolean iirEnabled) {
        isIirEnabled = iirEnabled;
    }

    public boolean isIirEnabled() {
        return isIirEnabled;
    }


    //Clipping amplitude
    public static final short MAX_AMPLITUDE = 50;


    public AudioPlayer(File musicFile) {
        this.currentMusicFile = musicFile;
        this.equalizer = new Equalizer();
        this.gain = 1.0;
        this.reverb = new Reverb();
        this.isVibrato = false;
        this.vibrato = new Vibrato();
    }

    public Reverb getReverb() {
        return reverb;
    }

    private void Vibrato(short[] inputSamples) throws ExecutionException,
            InterruptedException {
        this.vibrato.setInputSampleStream(inputSamples);
        this.bufferShort = this.vibrato.createEffect();
    }

    public boolean isVibratoActive() {
        return this.isVibrato;
    }

    public void setVibrato(boolean b) {
        this.isVibrato = b;
    }

    public boolean isReverbEnabled() {
        return reverbEnabled;
    }

    public void setReverbEnabled(boolean reverbEnabled) {
        this.reverbEnabled = reverbEnabled;
    }

    public void play() {
        try {
            this.audioStream = AudioSystem.getAudioInputStream(currentMusicFile);
            AudioFormat audioFormat = audioStream.getFormat();
            this.sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            this.sourceDataLine.open(audioFormat);
            this.sourceDataLine.start();
            this.pauseStatus = false;
            this.stopStatus = false;

            while ((this.audioStream.read(this.bufferBytes) != -1)) {
                this.ByteArrayToShortArray();

                if (this.pauseStatus) {
                    this.pause();
                }

                if (this.stopStatus) {
                    break;
                }

                equalizer.setInputSignal(this.bufferShort);
                this.equalizer.equalization();
                this.bufferShort = equalizer.getOutputSignal();

                // Применение эффекта reverb
                if (reverbEnabled) {
                    reverb.setInputAudioStream(this.bufferShort);  // Устанавливаем входной поток для Reverberation
                    this.bufferShort = reverb.createEffect();
                }

                if (this.isVibrato) {
                    this.Vibrato(this.bufferShort);
                }

                this.ShortArrayToByteArray();
                this.sourceDataLine.write(this.bufferBytes, 0, this.bufferBytes.length);
            }
            this.sourceDataLine.drain();
            this.sourceDataLine.close();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException |
                 ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void pause() {
        if (this.pauseStatus) {
            while (true) {
                try {
                    if (!this.pauseStatus || this.stopStatus) {
                        break;
                    }
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public void setPauseStatus(boolean pauseStatus) {
        this.pauseStatus = pauseStatus;
    }

    public void setStopStatus(boolean stopStatus) {
        this.stopStatus = stopStatus;
    }

    public boolean getStopStatus() {
        return this.stopStatus;
    }

    public void close() {
        if (this.audioStream != null) {
            try {
                this.audioStream.close();
            } catch (IOException e) {
            }
        }
        if (this.sourceDataLine != null) {
            this.sourceDataLine.close();
        }
    }

    private void ByteArrayToShortArray() {
        for (int i = 0, j = 0; i < this.bufferBytes.length; i += 2, j++) {
            this.bufferShort[j] = (short) (
                    (ByteBuffer.wrap(this.bufferBytes, i, 2).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                            .getShort() / 2) * this.gain);
        }
    }

    private void ShortArrayToByteArray() {
        for (int i = 0, j = 0; i < this.bufferShort.length && j < this.bufferBytes.length; i++, j += 2) {
            ByteBuffer buffer = ByteBuffer.allocate(2).order(java.nio.ByteOrder.LITTLE_ENDIAN).putShort(bufferShort[i]);
            this.bufferBytes[j] = buffer.array()[0];
            this.bufferBytes[j + 1] = buffer.array()[1];
        }
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public Equalizer getEqualizer() {
        return this.equalizer;
    }

}
