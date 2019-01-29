package entity.midi;

import entity.midi.event.MidiEvent;
import util.MidiUtil;

import java.util.ArrayList;
import java.util.List;

public class MidiTrack {

    private int byteCount;

    private byte[] midiTrackData;

    private byte[] midiTrackContentData;

    private List<MidiEvent> midiEvents;

    private final static byte[] TRACK_HEADER = new byte[]{0x4D, 0x54, 0x72, 0x6B};

    private final static byte[] END_TRACK = new byte[]{0x00, (byte) 0xFF, 0x2F, 0x00};

    public MidiTrack() {
        midiTrackData = TRACK_HEADER;
        midiTrackContentData = new byte[0];
        midiEvents = new ArrayList<>();
        byteCount = 0;
    }

    public void setBpm(float bpm) {
        int microsecondPreNote = MidiUtil.bpmToMpt(bpm);
        byte[] tempo = new byte[]{0x00, (byte) 0xFF, 0x51, 0x03};
        tempo = MidiUtil.mergeByte(tempo, MidiUtil.intToBytes(microsecondPreNote, 3));

        byteCount += 7;
        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, tempo);
    }

    public void setDuration(Integer duration) {
        if (duration != 0) {
            byte[] note = new byte[]{0x00, (byte) 0x90, 0x3C, 0x00};

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, note);

            note = MidiUtil.buildBytes(duration);

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, note);

            byteCount += note.length;

            note = new byte[]{(byte) 0x80, 0x3C, 0x00};

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, note);

            byteCount += 7;
        }
    }

    public void addController(Integer channel, Integer type, Integer level) {
        byte[] controller = new byte[]{0x00, ((Integer) (176 + channel)).byteValue(), type.byteValue(), level.byteValue()};

        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, controller);

        byteCount += 4;
    }

    public void setInstrument(Integer channel, Integer type) {
        byte[] instrument = new byte[]{0x00, ((Integer) (192 + channel)).byteValue(), type.byteValue(),
                0x00, ((Integer) (176 + channel)).byteValue(), 0x0A, 0x40};

        midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, instrument);

        byteCount += 7;
    }

    public void insertNote(Integer channel, Integer pitch, Integer ticks) {
        byte[] noteOn;
        byte[] noteOff;

        if (pitch != 0) {
            noteOn = new byte[]{0x00, ((Integer) (144 + channel)).byteValue(), pitch.byteValue(), (byte) 0x78};

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOn);

            noteOff = MidiUtil.buildBytes(ticks);

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);

            byteCount += noteOff.length;

            noteOff = new byte[]{((Integer) (128 + channel)).byteValue(), pitch.byteValue(), 0x00};

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);

            byteCount += 7;
        } else {
            noteOn = new byte[]{0x00, ((Integer) (144 + channel)).byteValue(), 0X00, 0x00};

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOn);

            noteOff = MidiUtil.buildBytes(ticks);

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);

            byteCount += noteOff.length;

            noteOff = new byte[]{((Integer) (128 + channel)).byteValue(), 0X00, 0x00};

            midiTrackContentData = MidiUtil.mergeByte(midiTrackContentData, noteOff);

            byteCount += 7;
        }
    }

    public void append(MidiEvent midiEvent) {
        midiEvents.add(midiEvent);
        byteCount += midiEvent.getSize();
    }

    public void setEnd() {
        byteCount += 4;
        midiTrackData = MidiUtil.mergeByte(midiTrackData, MidiUtil.intToBytes(byteCount, 4));
        midiTrackData = MidiUtil.mergeByte(midiTrackData, midiTrackContentData);
        midiTrackData = MidiUtil.mergeByte(midiTrackData, END_TRACK);
    }

    public void merge(MidiTrack midiTrack) {
        this.byteCount += midiTrack.byteCount;
        this.midiTrackContentData = MidiUtil.mergeByte(this.midiTrackContentData, midiTrack.midiTrackContentData);
    }

    public byte[] getMidiTrackData() {
        return midiTrackData;
    }
}