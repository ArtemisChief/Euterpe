package entity.midi;

import util.MidiUtil;

public class MidiTrack {

    private byte[] MidiTrackData;

    private byte[] MidiTrackContentData;

    private final static byte[] TRACK_HEADER = new byte[]{0x4D, 0x54, 0x72, 0x6B};

    private final static byte[] END_TRACK = new byte[]{0x00, (byte) 0xFF, 0x2F, 0x00};

    private int ByteCount;

    public MidiTrack() {
        MidiTrackData = TRACK_HEADER;
        MidiTrackContentData = new byte[0];
        ByteCount = 0;
    }

    public void setBpm(float bpm) {
        int microsecondPreNote = MidiUtil.bpmToMpt(bpm);
        byte[] tempo = new byte[]{0x00, (byte) 0xFF, 0x51, 0x03};
        tempo = MidiUtil.mergeByte(tempo, MidiUtil.intToBytes(microsecondPreNote, 3));

        ByteCount += 7;

        MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, tempo);
    }

    public void setDuration(Integer duration) {
        if (duration != 0) {
            byte[] note = new byte[]{0x00, (byte) 0x90, 0x3C, 0x00};

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, note);

            note = MidiUtil.buildBytes(duration);

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, note);

            ByteCount += note.length;

            note = new byte[]{(byte) 0x80, 0x3C, 0x00};

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, note);

            ByteCount += 7;
        }
    }

    public void addController(Integer channel, Integer type, Integer level) {
        byte[] controller = new byte[]{0x00, ((Integer) (176 + channel)).byteValue(), type.byteValue(), level.byteValue()};

        MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, controller);

        ByteCount += 4;
    }

    public void setInstrument(Integer channel, Integer type) {
        byte[] instrument = new byte[]{0x00, ((Integer) (192 + channel)).byteValue(), type.byteValue(),
                0x00, ((Integer) (176 + channel)).byteValue(), 0x0A, 0x40};

        MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, instrument);

        ByteCount += 7;
    }

    public void insertNote(Integer channel, Integer pitch, Integer ticks) {
        byte[] noteOn;
        byte[] noteOff;

        if (pitch != 0) {
            noteOn = new byte[]{0x00, ((Integer) (144 + channel)).byteValue(), pitch.byteValue(), (byte) 0x78};

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, noteOn);

            noteOff = MidiUtil.buildBytes(ticks);

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, noteOff);

            ByteCount += noteOff.length;

            noteOff = new byte[]{((Integer) (128 + channel)).byteValue(), pitch.byteValue(), 0x00};

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, noteOff);

            ByteCount += 7;
        } else {
            noteOn = new byte[]{0x00, ((Integer) (144 + channel)).byteValue(), 0X00, 0x00};

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, noteOn);

            noteOff = MidiUtil.buildBytes(ticks);

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, noteOff);

            ByteCount += noteOff.length;

            noteOff = new byte[]{((Integer) (128 + channel)).byteValue(), 0X00, 0x00};

            MidiTrackContentData = MidiUtil.mergeByte(MidiTrackContentData, noteOff);

            ByteCount += 7;
        }
    }

    public void setEnd() {
        ByteCount += 4;
        MidiTrackData = MidiUtil.mergeByte(MidiTrackData, MidiUtil.intToBytes(ByteCount, 4));
        MidiTrackData = MidiUtil.mergeByte(MidiTrackData, MidiTrackContentData);
        MidiTrackData = MidiUtil.mergeByte(MidiTrackData, END_TRACK);
    }

    public byte[] getMidiTrackData() {
        return MidiTrackData;
    }
}