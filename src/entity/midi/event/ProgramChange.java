package entity.midi.event;

public class ProgramChange extends MidiEvent{
    public ProgramChange(int channel,int instrument){
        super(0,0xC,channel,instrument,0);
    }
}
