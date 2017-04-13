package com.iantmeyer.tuner.activity;

import android.media.AudioFormat;
import android.media.MediaRecorder;

import com.iantmeyer.tuner.audio.AudioCaptureManager;
import com.iantmeyer.tuner.audio.music.NoteHelper;
import com.iantmeyer.tuner.audio.process.FrequencyProcessor;
import com.iantmeyer.tuner.audio.process.VolumeProcessor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

class TunerPresenter implements TunerMvp.Presenter {

    private TunerMvp.View mView;

    private double mReferenceFreq = 440;

    private NoteHelper mNoteHelper;

    private AudioCaptureManager mAudioCaptureManager;

    TunerPresenter(TunerMvp.View view) {
        mView = view;
        mNoteHelper = new NoteHelper(mReferenceFreq);
        mNoteHelper.setFrequencyRange(125d, 2150d);
        mNoteHelper.setDecibleThreshold(10);
    }

    @Override
    public void subscribe() {
        EventBus.getDefault().register(this);

        int sampleRateHz = 44100;
        int bufferSize = 4096;
        mAudioCaptureManager = new AudioCaptureManager(
                MediaRecorder.AudioSource.MIC,
                sampleRateHz,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
        );
//        mAudioCaptureManager.addAudioProcessor(new VolumeProcessor());
        mAudioCaptureManager.addAudioProcessor(new FrequencyProcessor(sampleRateHz, bufferSize));
        mAudioCaptureManager.record();
    }

    @Override
    public void unsubscribe() {
        EventBus.getDefault().unregister(this);
        mAudioCaptureManager.stop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDecibelsUpdateEvent(VolumeProcessor.DecibelUpdateEvent event) {
        mView.setVolume(Math.round(event.decibels));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFrequencyUpdateEvent(FrequencyProcessor.FrequencyUpdateEvent event) {
        mView.setFrequency(Math.round(event.frequencyHz));
        NoteHelper.Note note = mNoteHelper.getNote(event.frequencyHz, event.decibels);
        String noteText = note.outsideRange ? "--" : note.note;
        mView.setNote(noteText);
        mView.setVolume(Math.round(event.decibels));

    }
}
