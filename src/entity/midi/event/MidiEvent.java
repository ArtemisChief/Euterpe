package entity.midi.event;

import util.MidiUtil;

public class MidiEvent {
    private Integer delta;
    private Integer type;
    private Integer channel;
    private Integer param1;
    private Integer param2;

    byte[] MidiEventData;

    public MidiEvent(int delta, int type, int channel, int param1, int param2) {
        MidiEventData = new byte[0];
        this.delta = delta;
        this.type = type;
        this.channel = channel;
        this.param1 = param1;
        this.param2 = param2;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public byte[] getMidiEventData() {
        MidiEventData = MidiUtil.buildBytes(delta);
        MidiEventData = MidiUtil.mergeByte(MidiEventData, ((Integer) (type * 0x10 + channel)).byteValue());
        MidiEventData = MidiUtil.mergeByte(MidiEventData, param1.byteValue());

        if (param2 != 0) {
            MidiEventData = MidiUtil.mergeByte(MidiEventData, param2.byteValue());
        }

        return MidiEventData;
    }

    public int getSize() {
        return MidiEventData.length;
    }
}