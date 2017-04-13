package com.iantmeyer.tuner.audio.music;

import com.iantmeyer.tuner.audio.music.NoteHelper;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NoteHelperTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getNote() throws Exception {
        String[] notes = {
                "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#",
                "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#",
                "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#",
                "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#",
                "A", "A#", "B", "C"
        };

        double[] freqs = {
                130.82, 138.59, 146.83, 155.56, 164.81, 174.61, 185, 196, 207.65,
                220.00, 233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392, 415.30,
                440, 466.16, 493.88, 523.25, 554.37, 587.33, 622.25, 659.26, 698.46, 739.99, 783.99, 830.61,
                880, 932.33, 987.77, 1046.5, 1108.73, 1174.66, 1244.51, 1318.51, 1396.91, 1479.98, 1567.98, 1661.22,
                1760, 1864.66, 1975.53, 2093
        };

        int[] noteIndices = {-21, -20, -19, -18, -17, -16, -15, -14, -13,
                -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
                24, 25, 26, 27};

        int[] noteOctaves = {-2, -2, -2, -2, -2, -2, -2, -2, -2,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                2, 2, 2, 2};

        NoteHelper noteHelper = new NoteHelper(440);
        for (int idx = 0; idx < notes.length; idx++) {
            NoteHelper.Note note = noteHelper.getNote(freqs[idx]);
            // System.out.print("Freq=" + freqs[idx] + ", Note=" + note.note + "\n");
            Assert.assertEquals(notes[idx], note.note);
            Assert.assertEquals(noteIndices[idx], note.noteIdx);
            Assert.assertEquals(noteOctaves[idx], note.noteOctave);
            Assert.assertEquals(freqs[idx], note.freq);
            Assert.assertEquals(freqs[idx], note.referenceFreq, 0.01d);
        }
    }
}