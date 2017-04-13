package com.iantmeyer.tuner.audio.process;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        int bufferSize = 4096;

        FrequencyProcessor freqProcessor = new FrequencyProcessor(sampleRate, bufferSize);
        // TODO IM - mock eventbus?
        final FrequencyProcessor.Callback callback = mock(FrequencyProcessor.Callback.class);
        ArgumentCaptor<Double> freqCaptor = ArgumentCaptor.forClass(double.class);
        ArgumentCaptor<Double> decibelCaptor = ArgumentCaptor.forClass(double.class);
        freqProcessor.setCallback(callback);
        double frequency;
        Signal signal = new Signal(bufferSize);

        frequency = 500d;
        signal.clear();
        signal.addSine(1.0d, sampleRate, frequency, -5.0d);
        freqProcessor.process(signal.mSignal[0]);

        frequency = 250d;
        signal.clear();
        signal.addSine(1.0d, sampleRate, frequency, -220.0d);
        freqProcessor.process(signal.mSignal[0]);

        frequency = 350d;
        signal.addSine(5.0d, sampleRate, frequency, 3.0d);
        freqProcessor.process(signal.mSignal[0]);

        frequency = 200d;
        signal.clear();
        signal.addCosine(12.0d, sampleRate, frequency, 48.0d);
        freqProcessor.process(signal.mSignal[0]);

        frequency = 600d;
        signal.addCosine(15.0d, sampleRate, frequency, 0.0d);
        freqProcessor.process(signal.mSignal[0]);

        verify(callback, times(5)).frequencyFound(freqCaptor.capture(), decibelCaptor.capture());

        List<Double> capturedFreqs = freqCaptor.getAllValues();
        Assert.assertEquals(500d, capturedFreqs.get(0), .02 * 500d);
        Assert.assertEquals(250d, capturedFreqs.get(1), .02 * 250d);
        Assert.assertEquals(350d, capturedFreqs.get(2), .02 * 350d);
        Assert.assertEquals(200d, capturedFreqs.get(3), .02 * 200d);
        Assert.assertEquals(600d, capturedFreqs.get(4), .02 * 600d);
    }

    private class Signal {
        final double[][] mSignal;
//        final Complex[] mComplexSignal;

        Signal(int length) {
            mSignal = new double[2][length];
//            mComplexSignal = new Complex[length];
        }

        void clear() {
            for (int idx = 0; idx < mSignal[0].length; idx++) {
                mSignal[0][idx] = 0d;
                mSignal[1][idx] = 0d;
//                mComplexSignal[idx] = new Complex(0d, 0d);
            }
        }

        void addSine(double amplitude, double sampleRate, double frequency, double phaseRads) {
            addSine(amplitude, sampleRate / frequency, phaseRads);
        }

        void addSine(double amplitude, double period, double phaseRads) {
            for (int idx = 0; idx < mSignal[0].length; idx++) {
                double value = amplitude * Math.sin(2 * Math.PI * idx / period + phaseRads);
                mSignal[0][idx] += value;
//                mComplexSignal[idx].plus(new Complex(value, 0));
            }
        }

        void addCosine(double amplitude, double sampleRate, double frequency, double phaseRads) {
            addCosine(amplitude, sampleRate / frequency, phaseRads);
        }

        void addCosine(double amplitude, double period, double phaseRads) {
            for (int idx = 0; idx < mSignal[0].length; idx++) {
                double value = amplitude * Math.cos(2 * Math.PI * idx / period + phaseRads);
                mSignal[0][idx] += value;
//                mComplexSignal[idx].plus(new Complex(value, 0));
            }
        }
    }
}