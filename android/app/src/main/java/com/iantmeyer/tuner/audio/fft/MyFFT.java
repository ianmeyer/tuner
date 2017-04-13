package com.iantmeyer.tuner.audio.fft;

public class MyFFT {

    /*
        https://web.archive.org/web/20150922044939/http://www.wikijava.org/wiki/The_Fast_Fourier_Transform_in_Java_%28part_1%29
     */

    /*
        http://www.drdobbs.com/cpp/a-simple-and-efficient-fft-implementatio/199500857
     */

    private static final String TAG = "ComplexFFT";

    private final int mSize;
    private final int[] mRevBitTable;
    private final int mBitLength;

    private final double[] mCosTable;
    private final double[] mSinTable;

    /**
     * @param size - must be a power of 2
     */
    public MyFFT(int size) {
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

    public void fft(double[][] data) {
        dft(data, false);
    }

    public void ifft(double[][] data) {
        dft(data, true);
    }

    private void dft(double[][] data, boolean inverse) {
        danielsonLanczos(data, inverse);
        bitReversal(data[0]);
        bitReversal(data[1]);
    }

    private void bitReversal(double[] data) {
        double temp;
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

    private void danielsonLanczos(double[][] data, boolean inverse) {
        int sign = inverse ? -1 : 1;

        int k = 0;
        int n1 = mSize / 2;
        int nu1 = mBitLength - 1;
        int p;
        double temp_r, temp_i;
        double cos, sin;
        for (int l = 1; l <= mBitLength; l++) {
            while (k < mSize) {
                for (int i = 1; i <= n1; i++) {
                    p = mRevBitTable[k >> nu1];

                    cos = mCosTable[p];             // cos(-x) => cos(x)
                    sin = sign * mSinTable[p];      // sin(-x) => -sin(x)

                    // TODO IM - Avoid object creation here?
                    temp_r = cos * data[0][k + n1] + sin * data[1][k + n1];
                    temp_i = cos * data[1][k + n1] - sin * data[0][k + n1];

                    data[0][k + n1] = data[0][k] - temp_r;
                    data[1][k + n1] = data[1][k] - temp_i;
                    data[0][k] += temp_r;
                    data[1][k] += temp_i;
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