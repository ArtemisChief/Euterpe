package entity.interpreter;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {

    private Float Speed;
    private int volume;
    private int instrument;
    private List<Integer> NoteList;
    private List<Integer> DurationList;

    public Paragraph() {
        Speed = 0.0F;
        instrument = 0;
        volume = 0;
        NoteList = new ArrayList<>();
        DurationList = new ArrayList<>();
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    public Float getSpeed() {
        return Speed;
    }

    public void setSpeed(Float speed) {
        Speed = speed;
    }

    public List<Integer> getNoteList() {
        return NoteList;
    }

    public void setNoteList(List<Integer> noteList) {
        NoteList = noteList;
    }

    public List<Integer> getDurationList() {
        return DurationList;
    }

    public void setDurationList(List<Integer> durationList) {
        DurationList = durationList;
    }

}