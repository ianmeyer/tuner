package com.iantmeyer.tuner.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.iantmeyer.tuner.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TunerActivity extends Activity implements TunerMvp.View {

    private static final String TAG = "TunerActivity";

    @BindView(R.id.text_volume_db)
    protected TextView mVolumeDbTv;

    @BindView(R.id.text_note)
    protected TextView mNoteTv;

    @BindView(R.id.text_frequency)
    protected TextView mFrequencyTv;

    @BindView(R.id.text_reference_note)
    protected TextView mReferenceTv;

    private TunerMvp.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new TunerPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setVolume(double decibels) {
        mVolumeDbTv.setText("" + Math.round(decibels) + " db");
    }

    @Override
    public void setNote(String note) {
        mNoteTv.setText(note);
    }

    @Override
    public void setFrequency(double frequencyHz) {
        mFrequencyTv.setText("" + Math.round(frequencyHz) + " Hz");
    }

    @Override
    public void setReference(double referenceHz) {
        mReferenceTv.setText("" + Math.round(referenceHz) + " Hz");
    }
}