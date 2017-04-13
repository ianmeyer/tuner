package com.iantmeyer.tuner.audio.music;

public class NoteHelper {

    private double mMinFrequency = -1;
    private double mMaxFrequency = -1;
    private double mDecibleThreshold = 0;

    private static final String[] NOTES = {
            "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"
    };

    private final double mReferenceFrequency;

    public NoteHelper(double referenceFrequency) {
        mReferenceFrequency = referenceFrequency;
    }

    public void setFrequencyRange(double minFrequency, double maxfrequency) {
        mMinFrequency = minFrequency;
        mMaxFrequency = maxfrequency;
    }

    public void setDecibleThreshold(double decibels) {
        mDecibleThreshold = decibels;
    }

    public Note getNote(double frequency, double decibels) {
        return new Note(frequency, decibels);
    }

    public class Note {
        public final String note;
        public final int noteIdx;
        public final int noteOctave;
        public final double freq;
        public final double referenceFreq;
        public final double decibels;
        public final boolean outsideRange;

        private Note(double freq, double decibels) {
            // dominant frequency detected
            this.freq = freq;
            double noteDbl = 12 * Math.log(freq / mReferenceFrequency) / Math.log(2);

            this.decibels = decibels;

            // index of note, relative to middle A
            this.noteIdx = (int) Math.round(noteDbl);

            // Octave of note

            this.noteOctave = (int) Math.floor(noteIdx / 12d);

            // Note letter
            int noteLookup = noteIdx % 12;
            while (noteLookup < 0) {
                noteLookup += 12;
            }
            this.note = NOTES[noteLookup];

            // Definitive frequency of note
            this.referenceFreq = mReferenceFrequency * Math.pow(2, (noteIdx / 12d));

            if (decibels < mDecibleThreshold
                    || (mMinFrequency != -1 && freq < mMinFrequency)
                    || (mMaxFrequency != -1 && freq > mMaxFrequency)) {
                outsideRange = true;
            } else {
                outsideRange = false;
            }
        }
    }
}
