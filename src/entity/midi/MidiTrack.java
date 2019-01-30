package entity.midi;

import util.MidiUtil;

public class MidiTrack {
    private byte[] midiTrackData;

    private byte[] midiTrackContentData;

    private final static byte[] TRACK_HEADER = new byte[]{0x4D, 0x54, 0x72, 0x6B};

    private final static byte[] END_TRACK = new byte[]{0x00, (byte) 0xFF, 0x2F, 0x00};

    public MidiTrack() {
        midiTrackData = TRACK_HEADER;
        midiTrackContentData = new byte[0];
    }

    public void setBpm(float bpm) {
        int microsecondPreNote = MidiUtil.bpmToMpt(bpm);
        byte[] tempo = new byte[]{0x00, (byte) 0xFF, 0x51, 0x03};
        tempo = MidiUtil.mergeByte(tempo, MidiUtil.intToBytes(microsecondPreNote, 3));

        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, tempo);
    }

    public void setDuration(int duration) {
        if (duration != 0) {
            byte[] note = new byte[]{0x00, (byte) 0x90, 0x3C, 0x00};
            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, note);

            note = MidiUtil.buildBytes(duration);
            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, note);

            note = new byte[]{(byte) 0x80, 0x3C, 0x00};
            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, note);
        }
    }

    public void addController(byte channel, byte type, byte param) {
        byte[] controller = new byte[]{0x00, (byte) (0xB0 + channel), type, param};
        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, controller);
    }

    public void setInstrument(byte channel, byte type) {
        byte[] instrument = new byte[]{0x00, (byte) (0xC0 + channel), type,
                0x00, (byte) (0xB0 + channel), 0x0A, 0x40};
        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, instrument);
    }

//    public void insertNote(byte channel, byte pitch, int ticks) {
//        byte[] noteOn;
//        byte[] noteOff;
//
//        if (pitch != 0) {
//            noteOn = new byte[]{0x00, (byte) (0x90 + channel), pitch, 0x40};
//            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOn);
//
//            noteOff = MidiUtil.buildBytes(ticks);
//            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);
//
//            noteOff = new byte[]{(byte) (0x80 + channel), pitch, 0x00};
//            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);
//        } else {
//            noteOn = new byte[]{0x00, (byte) (0x90 + channel), 0X00, 0x00};
//            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOn);
//
//            noteOff = MidiUtil.buildBytes(ticks);
//            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);
//
//            noteOff = new byte[]{(byte) (0x80 + channel), 0X00, 0x00};
//            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);
//        }
//    }

    public void insertNoteOff(int deltaTime, byte channel, byte note, boolean sameChannel) {
        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, MidiUtil.buildBytes(deltaTime));

        byte[] noteOff;

        if (sameChannel)
            noteOff = new byte[]{note, 0x00};
        else
            noteOff = new byte[]{(byte) (0x80 + channel), note, 0x00};

        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);
    }

    public void insertNoteOn(int deltaTime, byte channel, byte note, byte velocity, boolean sameChannel) {
        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, MidiUtil.buildBytes(deltaTime));

        byte[] noteOn;

        if (sameChannel)
            noteOn = new byte[]{note, velocity};
        else
            noteOn = new byte[]{(byte) (0x90 + channel), note, velocity};

        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOn);
    }

    public void setEnd() {
        midiTrackData = MidiUtil.mergeByte(midiTrackData, MidiUtil.intToBytes(midiTrackContentData.length + 4, 4));
        midiTrackData = MidiUtil.mergeByte(midiTrackData, midiTrackContentData);
        midiTrackData = MidiUtil.mergeByte(midiTrackData, END_TRACK);
    }

    public void merge(MidiTrack midiTrack) {
        this.midiTrackContentData = MidiUtil.mergeByte(this.midiTrackContentData, midiTrack.midiTrackContentData);
    }

    public byte[] getMidiTrackData() {
        return midiTrackData;
    }
}