package com.iantmeyer.tuner.audio.process;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class VolumeProcessor implements AudioProcessor {

    private static final String TAG = "VolumeProcessor";

    private double mDecibels = 0;

    private double mSmoothing = 2;

    private static double BASE_POWER = 1.0d;

    private static double OFFSET = 0.0d;

    public VolumeProcessor() {
    }

    @Override
    public void process(double[] audioData) {
        long start = System.nanoTime();
        double sum = 0;
        double sqsum = 0;
        for (int idx = 0; idx < audioData.length; idx++) {
            sum += audioData[idx];
            sqsum += audioData[idx] * audioData[idx];
        }
        double power = (sqsum - sum * sum / audioData.length) / audioData.length;

        double decibels = 20d * Math.log10(power / BASE_POWER) + OFFSET;        // TODO IM: how to calibrate?

        mDecibels += (decibels - mDecibels) / mSmoothing;
        if (Double.isNaN(mDecibels)) {
            mDecibels = 0d;
        }

        EventBus.getDefault().post(new DecibelUpdateEvent(mDecibels));
        Log.v(TAG, "Timer: " + (System.nanoTime() - start) / 1000L + " ms");
    }

    public static class DecibelUpdateEvent {
        public final double decibels;

        DecibelUpdateEvent(double dbs) {
            this.decibels = dbs;
        }
    }
}
