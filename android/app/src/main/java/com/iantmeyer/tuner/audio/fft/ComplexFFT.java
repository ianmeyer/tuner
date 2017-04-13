package com.iantmeyer.tuner.audio.fft;

public class ComplexFFT implements FFT {

    private static final String TAG = "ComplexFFT";

    private final int mSize;
    private final int[] mRevBitTable;
    private final int mBitLength;

    private final double[] mCosTable;
    private final double[] mSinTable;

    /**
     * @param size - must be a power of 2
     */
    public ComplexFFT(int size) {
        if ((size & (size - 1)) != 0) {
            throw new RuntimeException("ComplexFFT input array size must be a power of 2");
        }
        mSize = size;
        mBitLength = (int) (Math.log(size) / Math.log(2));

        // Compute bit reversal values
        mRevBitTable = new int[mSize];
        for (int i = 0; i < mSize; i++) {
            mRevBitTable[i] = reverseBits(i, mBitLength);
        }

        // Compute sine and cosine values
        mCosTable = new double[mSize / 2];
        mSinTable = new double[mSize / 2];
        for (int idx = 0; idx < mSize / 2; idx++) {
            mCosTable[idx] = Math.cos(2 * Math.PI * idx / mSize);
            mSinTable[idx] = Math.sin(2 * Math.PI * idx / mSize);
        }
    }

    @Override
    public void fft(Complex[] data) {
        dft(data, false);
    }

    @Override
    public void ifft(Complex[] data) {
        dft(data, true);
    }

    private void dft(Complex[] data, boolean inverse) {
        danielsonLanczos(data, inverse);
        bitReversal(data);
    }

    private void bitReversal(Complex[] data) {
        Complex temp;
        int revIdx;
        for (int idx = 0; idx < mSize; idx++) {
            revIdx = mRevBitTable[idx];
            if (revIdx > idx) {
                temp = data[idx];
                data[idx] = data[revIdx];
                data[revIdx] = temp;
            }
        }
    }

    private static int reverseBits(int integer, int bitLength) {
        int j2;
        int reverse = 0;
        for (int i = 1; i <= bitLength; i++) {
            j2 = integer / 2;
            reverse = 2 * reverse + integer - 2 * j2;
            integer = j2;
        }
        return reverse;
    }

    private void danielsonLanczos(Complex[] data, boolean inverse) {
        int sign = inverse ? -1 : 1;

        int k = 0;
        int n1 = mSize / 2;
        int nu1 = mBitLength - 1;
        int p;
        Complex temp;
        double cos, sin;
        for (int l = 1; l <= mBitLength; l++) {
            while (k < mSize) {
                for (int i = 1; i <= n1; i++) {
                    p = mRevBitTable[k >> nu1];

                    cos = mCosTable[p];             // cos(-x) => cos(x)
                    sin = sign * mSinTable[p];      // sin(-x) => -sin(x)

                    // TODO IM - Avoid object creation here?
                    temp = new Complex(
                            cos * data[k + n1].re() + sin * data[k + n1].im(),
                            cos * data[k + n1].im() - sin * data[k + n1].re()
                    );

                    data[k + n1] = Complex.subtract(data[k], temp);
                    data[k] = Complex.add(data[k], temp);
                    k++;
                }
                k += n1;
            }
            k = 0;
            nu1--;
            n1 /= 2;
        }
    }
}