package com.iantmeyer.tuner.audio.fft;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class ComplexFFTTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void reverseBits() throws Exception {
        Method method = ComplexFFT.class.getDeclaredMethod("reverseBits", Integer.TYPE, Integer.TYPE);
        method.setAccessible(true);

        /*
         Reverse bits for index

         idx    bits    rev-bits    rev-idx
         0      000     000         0
         1      001     100         4
         2      010     010         2
         3      011     110         6
         4      100     001         1
         5      101     101         5
         6      110     011         3
         7      111     111         7
        */
        int[] inputBits = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        int[] bitLength = new int[]{3, 3, 3, 3, 3, 3, 3, 3};
        int[] answerBits = new int[]{0, 4, 2, 6, 1, 5, 3, 7};

        for (int i = 0; i < inputBits.length; i++) {
            int reverseBits = (int) method.invoke(null, inputBits[i], bitLength[i]);
            Assert.assertEquals(answerBits[i], reverseBits);
        }

        /*
         idx    bits    rev-bits    rev-idx
         0      0000    0000        0
         1      0001    1000        8
         2      0010    0100        4
         3      0011    1100        12
         4      0100    0010        2
         5      0101    1010        10
         6      0110    0110        6
         7      0111    1110        14
         8      1000    0001        1
         9      1001    1001        9
         10     1010    0101        5
         11     1011    1101        13
         12     1100    0011        3
         13     1101    1011        11
         14     1110    0111        7
         15     1111    1111        15
        */
        inputBits = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        bitLength = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
        answerBits = new int[]{0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15};

        for (int i = 0; i < inputBits.length; i++) {
            int reverseBit = (int) method.invoke(null, inputBits[i], bitLength[i]);
            Assert.assertEquals(answerBits[i], reverseBit);
        }
    }

    @Test
    public void fft() throws Exception {
        double delta = 0.0001f;
        double[] input_r = new double[]{0, 1, 2, 3, 4, 5, 6, 7};
        double[] input_i = new double[]{0, 0, 0, 0, 0, 0, 0, 0};
        double[] answers_r = new double[]{28, -4, -4, -4, -4, -4, -4, -4};
        double[] answers_i = new double[]{0, 9.6569, 4.0000, 1.6569, 0, -1.6569, -4.0000, -9.6569};

        Complex[] input = Complex.fromArrays(input_r, input_i);

        ComplexFFT fft = new ComplexFFT(input_r.length);

        long start = System.nanoTime();
        fft.fft(input);
        System.out.print("FFT time = " + (System.nanoTime() - start) + " ns");

        for (int idx = 0; idx < input.length; idx += 2) {
            Assert.assertEquals(answers_r[idx], input[idx].re(), delta);
            Assert.assertEquals(answers_i[idx], input[idx].im(), delta);
        }
//        Assert.assertArrayEquals(answers_r, input_r, delta);
//        Assert.assertArrayEquals(answers_i, input_i, delta);
    }
    //******************

//        input_r = new double[]{0, 1, 2, 3, 4, 5, 6, 7};
//        input_i = new double[]{0, 0, 0, 0, 0, 0, 0, 0};
//
//        double[] combinedData = new double[input_r.length * 2];
//        for (int idx = 0; idx < input_r.length; idx += 2) {
//            combinedData[2 * idx] = input_r[idx];
//            combinedData[2 * idx + 1] = input_i[idx];
//        }
//        double[] outputData = mFft.four1(combinedData, 1);
//        for (int idx = 0; idx < outputData.length / 2; idx += 2) {
//            input_r[idx] = outputData[2 * idx];
//            input_i[idx] = outputData[2 * idx + 1];
//        }
//
//        Assert.assertArrayEquals(answers_r, input_r, delta);
//        Assert.assertArrayEquals(answers_i, input_i, delta);

    @Test
    public void ifft() throws Exception {
    }
}