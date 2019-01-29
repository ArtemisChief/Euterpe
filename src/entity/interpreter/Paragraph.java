package entity.interpreter;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {

    private Float Speed;
    private byte volume;
    private byte instrument;
    private List<Integer> noteList;
    private List<Integer> durationList;
    private int noteOffset;
    private int durationOffset;

    public Paragraph() {
        Speed = 0.0F;
        instrument = 0;
        volume = 0;
        noteList = new ArrayList<>();
        durationList = new ArrayList<>();
        noteOffset=0;
        durationOffset=0;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(byte instrument) {
        this.instrument = instrument;
    }

    public Float getSpeed() {
        return Speed;
    }

    public void setSpeed(Float speed) {
        Speed = speed;
    }

    public List<Integer> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Integer> noteList) {
        this.noteList = noteList;
    }

    public List<Integer> getDurationList() {
        return durationList;
    }

    public void setDurationList(List<Integer> durationList) {
        this.durationList = durationList;
    }

    public int getNoteOffset() {
        return noteOffset;
    }

    public void setNoteOffset(int noteOffset) {
        this.noteOffset = noteOffset;
    }

    public int getDurationOffset() {
        return durationOffset;
    }

    public void setDurationOffset(int durationOffset) {
        this.durationOffset = durationOffset;
    }
}