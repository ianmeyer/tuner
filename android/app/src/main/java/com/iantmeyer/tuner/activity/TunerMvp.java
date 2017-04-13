package com.iantmeyer.tuner.activity;

interface TunerMvp {
    interface View {
        void setVolume(double decibels);

        void setNote(String note);

        void setFrequency(double frequencyHz);

        void setReference(double referenceHz);
    }

    interface Presenter {
        void subscribe();

        void unsubscribe();
    }
}