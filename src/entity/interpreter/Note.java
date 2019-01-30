package entity.interpreter;

public class Note {
    private int deltaTime;
    private byte channel;
    private byte note;
    private byte velocity;
    private boolean isPrimary;

    public Note(int deltaTime, byte channel, byte note, byte velocity, boolean isPrimary) {
        this.deltaTime = deltaTime;
        this.channel = channel;
        this.note = note;
        this.velocity = velocity;
        this.isPrimary = isPrimary;
    }

    public void setDeltaTime(int deltaTime) {
        this.deltaTime = deltaTime;
    }

    public int getDeltaTime() {
        return deltaTime;
    }

    public byte getChannel() {
        return channel;
    }

    public byte getNote() {
        return note;
    }

    public byte getVelocity() {
        return velocity;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }
}