package com.iantmeyer.tuner.audio;

import android.media.AudioRecord;
import android.util.Log;

import com.iantmeyer.tuner.audio.filter.AudioFilter;
import com.iantmeyer.tuner.audio.process.AudioProcessor;

import java.util.ArrayList;
import java.util.List;

public class AudioCaptureManager {

    private static final String TAG = "AudioCaptureManager";

    private final List<AudioProcessor> mAudioProcessors = new ArrayList<>();

    private final int mAudioSource;

    private final int mSampleRateHz;

    private final int mChannelConfig;

    private final int mAudioFormat;

    private AudioCaptureThread mThread;

    private final int mBufferSize;

    private AudioFilter mAudioFilter;

    private final double[] mAudioDouble;

    public AudioCaptureManager(int audioSource, int sampleRateHz, int channelConfig,
                               int audioFormat, int bufferSize) {
        mAudioSource = audioSource;
        mSampleRateHz = sampleRateHz;
        mChannelConfig = channelConfig;
        mAudioFormat = audioFormat;
        mBufferSize = bufferSize;

        mAudioDouble = new double[mBufferSize];
    }

    public void setFilter(AudioFilter audioFilter) {
        mAudioFilter = audioFilter;
    }

    public void addAudioProcessor(AudioProcessor audioProcessor) {
        mAudioProcessors.add(audioProcessor);
    }

    public void record() {
        mThread = new AudioCaptureThread();
        mThread.start();
    }

    public void stop() {
        mThread.close();
        mThread = null;
    }

    private class AudioCaptureThread extends Thread {
        private boolean mRecording = false;

        @Override
        public void start() {
            super.start();
            mRecording = true;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            AudioRecord recorder;
            short[][] buffers = new short[256][mBufferSize];
            int ix = 0;

            try {
                int bufferSize = AudioRecord.getMinBufferSize(mSampleRateHz, mChannelConfig, mAudioFormat);

                recorder = new AudioRecord(
                        mAudioSource,
                        mSampleRateHz,
                        mChannelConfig,
                        mAudioFormat,
                        bufferSize * 16
                );
                recorder.startRecording();
            } catch (Throwable x) {
                Log.w(TAG, "Error reading voice audio", x);
                return;
            }

            while (mRecording) {
                short[] dataBuffer = buffers[ix++ % buffers.length];

                int bufferSize = recorder.read(dataBuffer, 0, dataBuffer.length);

                convertAudioToDouble(dataBuffer, mAudioDouble);
                if (mAudioFilter != null) {
                    mAudioFilter.filter(mAudioDouble);
                }
                int count = 0;
                for (AudioProcessor audioProcessor : mAudioProcessors) {
                    long start = System.nanoTime();
                    audioProcessor.process(mAudioDouble);
                    Log.v(TAG, "AudioProcessor #" + count + ": " + (System.nanoTime() - start) / 1000000L + " ms");
                    count++;
                }
            }
        }

        public void close() {
            mRecording = false;
        }
    }

    private static void convertAudioToDouble(short[] input, double[] output) {
        for (int idx = 0; idx < input.length; idx++) {
            output[idx] = (double) input[idx];
            output[idx] /= 32768.0d;
        }
    }
}