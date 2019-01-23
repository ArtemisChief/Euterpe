package entity.midi;

import util.MidiUtil;

import java.io.*;
import java.util.List;

public class MidiFile {

    private byte[] MidiFileData;

    private final static byte[] MIDI_HEADER = new byte[]{0x4D, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06, 0x00, 0x01};

    private List<MidiTrack> midiTracks;

    public MidiFile(List<MidiTrack> midiTracks) {
        MidiFileData = MIDI_HEADER;
        this.midiTracks = midiTracks;
    }

    public void construct() {
        int trackCount = midiTracks.size();
        MidiFileData = MidiUtil.mergeByte(MidiFileData, MidiUtil.intToBytes(trackCount, 2));
        MidiFileData = MidiUtil.mergeByte(MidiFileData, new byte[]{0x00, 0x78});
        for (MidiTrack midiTrack : midiTracks) {
            MidiFileData = MidiUtil.mergeByte(MidiFileData, midiTrack.getMidiTrackData());
        }
    }

    public boolean writeToFile(java.io.File midiFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(midiFile);
            fileOutputStream.write(MidiFileData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(MidiUtil.bytesToHex(MidiFileData));
        for (int i = 48; i < stringBuilder.length(); i += 49) {
            if (i < stringBuilder.length())
                stringBuilder.replace(i, i, "\n");
        }
        return stringBuilder.toString();
    }

}