package com.iantmeyer.tuner.audio.process;

import com.iantmeyer.tuner.audio.fft.Complex;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

public class FrequencyProcessorTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void process() throws Exception {
        int sampleRate = 44100;

        FrequencyProcessor freqProcessor = new FrequencyProcessor(sampleRate, 4096);
        // TODO IM - mock eventbus
//        final FrequencyProcessor.Callback callback = mock(FrequencyProcessor.Callback.class);
        ArgumentCaptor<Double> freqCaptor = ArgumentCaptor.forClass(double.class);
//        freqProcessor.setCallback(callback);
        double frequency;
        Signal signal = new Signal(4096);

        frequency = 500;
        signal.clear();
        signal.addSine(1.0, sampleRate, frequency, 0.0);
        freqProcessor.process(signal.mSignal[0]);

        frequency = 250;
        signal.clear();
        signal.addSine(1.0, sampleRate, frequency, 0.0);
        freqProcessor.process(signal.mSignal[0]);

        frequency = 350;
        signal.addSine(5.0, sampleRate, frequency, 0.0);
        freqProcessor.process(signal.mSignal[0]);

        frequency = 200;
        signal.addCosine(3.0, sampleRate, frequency, 0.0);
        freqProcessor.process(signal.mSignal[0]);

        frequency = 600;
        signal.addCosine(8.0, sampleRate, frequency, 0.0);
        freqProcessor.process(signal.mSignal[0]);

        // TODO IM - mock eventbus
//        verify(callback, times(5)).frequencyFound(freqCaptor.capture());

        List<Double> capturedFreqs = freqCaptor.getAllValues();
        Assert.assertEquals(500, capturedFreqs.get(0), .02 * 500);
        Assert.assertEquals(250, capturedFreqs.get(1), .02 * 250);
        Assert.assertEquals(350, capturedFreqs.get(2), .02 * 350);
        Assert.assertEquals(350, capturedFreqs.get(3), .02 * 350);
        Assert.assertEquals(600, capturedFreqs.get(4), .02 * 600);
    }

    private class Signal {
        final double[][] mSignal;
        final Complex[] mComplexSignal;

        Signal(int length) {
            mSignal = new double[2][length];
            mComplexSignal = new Complex[length];
        }

        void clear() {
            for (int idx = 0; idx < mSignal.length; idx++) {
                mSignal[0][idx] = 0d;
                mSignal[1][idx] = 0d;
                mComplexSignal[idx] = new Complex(0d, 0d);
            }
        }

        void addSine(double amplitude, double sampleRate, double frequency, double phaseRads) {
            addSine(amplitude, sampleRate / frequency, phaseRads);
        }

        void addSine(double amplitude, double period, double phaseRads) {
            for (int idx = 0; idx < mSignal.length; idx++) {
                double value = amplitude * Math.sin(2 * Math.PI * idx / period + phaseRads);
                mSignal[0][idx] += value;
                mComplexSignal[idx].plus(new Complex(value, 0));
            }
        }

        void addCosine(double amplitude, double sampleRate, double frequency, double phaseRads) {
            addCosine(amplitude, sampleRate / frequency, phaseRads);
        }

        void addCosine(double amplitude, double period, double phaseRads) {
            for (int idx = 0; idx < mSignal.length; idx++) {
                double value = amplitude * Math.cos(2 * Math.PI * idx / period + phaseRads);
                mSignal[0][idx] += value;
                mComplexSignal[idx].plus(new Complex(value, 0));
            }
        }
    }
}