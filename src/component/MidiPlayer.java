package component;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
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

    public long getMicrosecondPosition() {
        return sequencer.getMicrosecondPosition();
    }

    public void setMicrosecondPosition(long microsecondPosition) {
        sequencer.setMicrosecondPosition(microsecondPosition);
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public boolean getIsRunning() {
        return sequencer.isRunning();
    }

    public String getGraphicPlayer(String name) {
        name += " ]";

        int count = 57 - name.length();

        for (int i = 0; i < count; i++) {
            name += " ";
        }

        return "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "============================================\n" +
                "|\t\t\t\t\t\t  |\n" +
                "| \t\t       Midi Player\t\t\t  |\n" +
                "|\t\t\t\t\t\t  |\n" +
                "|   [ Now Playing: " + name + "\t  |\n" +
                "|   [ Time Line: " + getPlayTime() + " ]\t\t\t\t\t  |\n" +
                "|\t\t\t\t\t\t  |\n" +
                "|   ||" + getPlayLine() + "||\t  |\n" +
                "|\t\t\t\t\t\t  |\n" +
                "============================================";
    }

    private String getPlayTime() {
        StringBuilder out = new StringBuilder();

        int minute = (int) (sequencer.getMicrosecondPosition() / 1000000 / 60);
        int second = (int) (sequencer.getMicrosecondPosition() / 1000000 % 60);

        if (minute < 10)
            out.append("0");

        out.append(minute + ":");

        if (second < 10)
            out.append("0");

        out.append(second);

        return out.toString();
    }

    private String getPlayLine() {
        StringBuilder out = new StringBuilder();

        int count = (int) ((float) sequencer.getTickPosition() / sequencer.getTickLength() * 65);

        for (int i = 0; i <= count; i++)
            out.append("/");

        count = 65 - count;

        for (int i = 0; i < count-1; i++)
            out.append("-");

        return out.toString();
    }
}