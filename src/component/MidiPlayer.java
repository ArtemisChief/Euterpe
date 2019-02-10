package component;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import java.io.File;

public class MidiPlayer {

    private Synthesizer synthesizer;

    private Sequencer sequencer;

    private Soundbank soundbank;

    private long microsecondPosition;

    private String title;

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
            if (soundbank == null)
                synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
            else
                synthesizer.unloadAllInstruments(soundbank);

            synthesizer.loadAllInstruments(soundbank = MidiSystem.getSoundbank(soundFontFile));
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

    public void setTitle(String title) {
        int length = title.length();

        title += " ]";

        int count = 58 - title.length();

        StringBuilder titleBuilder = new StringBuilder(title);
        for (int i = 0; i < count; i++)
            titleBuilder.append(" ");

        title = titleBuilder.toString();

        if (length < 5) {
            count = 6 - length;
            for (int i = 0; i < count; i++)
                titleBuilder.append(" ");
        }

        this.title = title;
    }

    public String getGraphicPlayer() {
        return "============================================\n" +
                "\t\t      Midi 播放器\n" +
                "--------------------------------------------------------------------------\n" +
                "* 文字式可视化播放进度显示\n\n" +
                "1.操作说明\n" +
                "\t1）操作皆需要光标位于左侧面板闪烁时进行\n" +
                "\t2）使用左箭头键前进一秒，右箭头键后退一秒\n" +
                "\t3）使用Ctrl+左箭头键前进五秒，Ctrl+右箭头键后退五秒\n\n" +
                "2.附加说明\n" +
                "\t1）进度条与所有音符持续时间总值相关，不与时间直接相关\n" +
                "\t2）即若一段音乐前后结构相同，但是前后的速度不同\n" +
                "\t3）表现为进度条到一半时，播放进度刚好为前一段结束\n" +
                "\t4）播放过程中无法使用其他占用本面板的功能\n" +
                "--------------------------------------------------------------------------" +
                "\n\n\n============================================\n|\t\t\t\t\t\t  |\n| \t\t       " +
                "Midi Player\t\t\t  |\n|\t\t\t\t\t\t  |\n|   " +
                "[ Now Playing: " + title + "\t  |\n|   " +
                "[ Time Line: " + getPlayTime() + " ]\t\t\t\t\t  |\n|\t\t\t\t\t\t  |\n|   " +
                "||" + getPlayLine() + "||\t  |\n|\t\t\t\t\t\t  |\n============================================";
    }

    private String getPlayTime() {
        StringBuilder out = new StringBuilder();

        int minute = (int) (sequencer.getMicrosecondPosition() / 1000000 / 60);
        int second = (int) (sequencer.getMicrosecondPosition() / 1000000 % 60);

        if (minute < 10)
            out.append("0");

        out.append(minute).append(":");

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

        for (int i = 0; i < count - 1; i++)
            out.append("-");

        return out.toString();
    }

}