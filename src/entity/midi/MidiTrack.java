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

    public void insertNoteOff(int deltaTime, byte note) {
        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, MidiUtil.buildBytes(deltaTime));

        byte[] noteOff = new byte[]{(byte) 0x80, note, 0x00};

        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);
    }

    public void insertNoteOn(byte note, byte velocity) {
        byte[] noteOn;

        if (note != 0)
            noteOn = new byte[]{0x00, (byte) 0x90, note, velocity};
        else
            noteOn = new byte[]{0x00, (byte) 0x90, note, 0x00};

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

    byte[] getMidiTrackData() {
        return midiTrackData;
    }

}