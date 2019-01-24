package entity.midi.event;

public class NoteOff extends MidiEvent {
    private int duration;

    public NoteOff(int channel, int note) {
        super(0, 0x8, channel, note, 0x00);
    }

    public NoteOff(int delta, int channel, int note) {
        super(delta, 0x8, channel, note, 0x00);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}