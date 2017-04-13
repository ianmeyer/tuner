package com.iantmeyer.tuner.audio.fft;

public interface FFT {
    void fft(Complex[] data);

    void ifft(Complex[] data);
}