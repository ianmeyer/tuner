package com.iantmeyer.tuner.audio.fft;

public class Complex {
    private double real;
    private double imag;

    public Complex() {
        this.real = 0.0;
        this.imag = 0.0;
    }

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public double re() {
        return real;
    }

    public double im() {
        return imag;
    }

    public Complex set(Complex complex) {
        this.set(complex.real, complex.imag);
        return this;
    }

    public Complex set(double real, double imag) {
        this.real = real;
        this.imag = imag;
        return this;
    }

    public Complex plus(Complex complex) {
        this.real += complex.real;
        this.imag += complex.imag;
        return this;
    }

    public Complex minus(Complex complex) {
        this.real -= complex.real;
        this.imag -= complex.imag;
        return this;
    }

    public Complex times(Complex complex) {
        this.real = this.real * complex.real - this.imag * this.imag;
        this.imag = this.real * complex.imag + this.imag * complex.real;
        return this;
    }

    public double abs() {
        return Math.sqrt(Math.pow(real, 2) + Math.pow(imag, 2));
    }

    public static Complex add(Complex complex1, Complex complex2) {
        return new Complex(complex1.real + complex2.real, complex1.imag + complex2.imag);
    }

    public static Complex subtract(Complex complex1, Complex complex2) {
        return new Complex(complex1.real - complex2.real, complex1.imag - complex2.imag);
    }

    public static Complex multiply(Complex complex1, Complex complex2) {
        return new Complex(
                complex1.real * complex2.real - complex1.imag * complex2.imag,
                complex1.real * complex2.imag + complex1.imag * complex2.real
        );
    }

    public static Complex[] fromArrays(double[] reals, double[] imaginaries) {
        if (reals.length != imaginaries.length) {
            throw new RuntimeException("Real and imaginary arrays must have the same length");
        }
        Complex[] output = new Complex[reals.length];
        for (int idx = 0; idx < output.length; idx++) {
            output[idx] = new Complex(reals[idx], imaginaries[idx]);
        }
        return output;
    }

    public static Complex[] fromArray(double[] real) {
        Complex[] output = new Complex[real.length];
        for (int idx = 0; idx < output.length; idx++) {
            output[idx] = new Complex(real[idx], 0);
        }
        return output;
    }

    public static void fill(Complex[] complexData, double[] real) {
        for (int idx = 0; idx < complexData.length; idx++) {
            complexData[idx].set(real[idx], 0d);
        }
    }
}