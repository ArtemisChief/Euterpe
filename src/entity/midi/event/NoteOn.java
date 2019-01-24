package entity.midi.event;

public class NoteOn extends MidiEvent {
    public NoteOn(int channel, int note) {
        super(0, 0x9, channel, note, 0x78);
    }
}