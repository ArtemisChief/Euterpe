package entity.interpreter;

/**
 * 音符类
 * 用于处理同时音操作时存入优先队列
 * 以便按时值排序
 */

public class Note {

    private int deltaTime;

    private byte note;

    private boolean isPrimary;

    public Note(int deltaTime, byte note, boolean isPrimary) {
        this.deltaTime = deltaTime;
        this.note = note;
        this.isPrimary = isPrimary;
    }

    public void setDeltaTime(int deltaTime) {
        this.deltaTime = deltaTime;
    }

    public int getDeltaTime() {
        return deltaTime;
    }

    public byte getNote() {
        return note;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

}