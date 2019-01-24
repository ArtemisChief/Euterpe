package entity.midi;

import util.MidiUtil;

import java.io.*;
import java.util.List;

public class MidiFile {

    private byte[] midiFileData;

    private List<MidiTrack> midiTracks;

    private final static byte[] MIDI_HEADER = new byte[]{0x4D, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06, 0x00, 0x01};

    public MidiFile(List<MidiTrack> midiTracks) {
        midiFileData = MIDI_HEADER;
        this.midiTracks = midiTracks;
    }

    public void construct() {
        int trackCount = midiTracks.size();
        midiFileData = MidiUtil.mergeByte(midiFileData, MidiUtil.intToBytes(trackCount, 2));
        midiFileData = MidiUtil.mergeByte(midiFileData, new byte[]{0x00, 0x78});
        for (MidiTrack midiTrack : midiTracks) {
            midiFileData = MidiUtil.mergeByte(midiFileData, midiTrack.getMidiTrackData());
        }
    }

    public boolean writeToFile(java.io.File midiFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(midiFile);
            fileOutputStream.write(midiFileData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(MidiUtil.bytesToHex(midiFileData));
        for (int i = 48; i < stringBuilder.length(); i += 49) {
            if (i < stringBuilder.length())
                stringBuilder.replace(i, i, "\n");
        }
        return stringBuilder.toString();
    }

}