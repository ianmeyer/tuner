package com.iantmeyer.tuner.audio.process;

import com.iantmeyer.tuner.audio.fft.Complex;
import com.iantmeyer.tuner.audio.fft.ComplexFFT;

import org.greenrobot.eventbus.EventBus;

public class FrequencyProcessor implements AudioProcessor {

    private static final String TAG = "FrequencyProcessor";

    private int mSampleRate;

    // private MyFFT mFft;
    private ComplexFFT mFft;

    private Complex[] mData;

    public FrequencyProcessor(int sampleRateHz, int bufferSize) {
        mSampleRate = sampleRateHz;
//        mFft = new MyFFT(bufferSize);
        mFft = new ComplexFFT(bufferSize);
        mData = new Complex[bufferSize];
        for (int idx = 0; idx < bufferSize; idx++) {
            mData[idx] = new Complex();
        }
    }

    @Override
    public void process(double[] audioData) {
//        double[][] data = new double[2][];
//        data[0] = audioData;
//        data[1] = new double[audioData.length];
//        Complex[] data = Complex.fromArray(audioData);
        Complex.fill(mData, audioData);
        mFft.fft(mData);
        double currentMag;
        double max = 0;
        int maxIdx = -1;
        for (int idx = 0; idx < audioData.length; idx++) {
//            currentMag = Math.sqrt(Math.pow(data[0][idx], 2) + Math.pow(data[1][idx], 2));
            currentMag = mData[idx].abs();
            if (currentMag > max) {
                max = currentMag;
                maxIdx = idx;
            }
        }
        double decibels = 20d * Math.log10(max);
        if (maxIdx == -1) {
            return;
        }
        double frequencyHz = maxIdx * mSampleRate / audioData.length;
        EventBus.getDefault().post(new FrequencyUpdateEvent(frequencyHz, decibels));
    }

    public static class FrequencyUpdateEvent {
        public final double frequencyHz;
        public final double decibels;

        FrequencyUpdateEvent(double frequencyHz, double decibels) {
            this.frequencyHz = frequencyHz;
            this.decibels = decibels;
        }
    }
}
