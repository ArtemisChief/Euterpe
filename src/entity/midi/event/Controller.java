package entity.midi.event;

public class Controller extends MidiEvent {
    public Controller(int channel, int controllerType, int controllerParam) {
        super(0, 0xB, channel, controllerType, controllerParam);
    }
}