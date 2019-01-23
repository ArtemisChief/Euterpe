package component;

import javax.sound.midi.*;
import java.io.File;

public class MidiPlayer {
    public Synthesizer synthesizer;
    public Sequencer sequencer;
    private long microsecondPosition;

    public MidiPlayer() {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();

            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSoundBank(File soundFontFile) {
        try {
            synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
            synthesizer.loadAllInstruments(MidiSystem.getSoundbank(soundFontFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMidiFile(File midiFile) {
        try {
            sequencer.setSequence(MidiSystem.getSequence(midiFile));
            microsecondPosition = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        sequencer.setMicrosecondPosition(microsecondPosition);
        sequencer.start();
    }

    public void pause() {
        microsecondPosition = sequencer.getMicrosecondPosition();
        sequencer.stop();
    }

    public void stop() {
        microsecondPosition = 0;
        sequencer.stop();
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public boolean getIsRunning() {
        return sequencer.isRunning();
    }
}