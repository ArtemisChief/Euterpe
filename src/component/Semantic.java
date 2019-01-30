package component;

import entity.interpreter.Node;
import entity.interpreter.Note;
import entity.interpreter.Paragraph;
import entity.midi.MidiFile;
import entity.midi.MidiTrack;
import javafx.util.Pair;

import java.util.*;

public class Semantic {

    private Node AbstractSyntaxTree;

    private List<Integer> errorLines;
    private StringBuilder errorInfo;

    private int toneOffset;
    private int haftToneOffset;

    private Map<String, Paragraph> paragraphMap;

    private MidiFile midiFile;
    private List<MidiTrack> midiTracks;
    private byte channel;

    public String ConvertToMidi(Node abstractSyntaxTree) {
        AbstractSyntaxTree = abstractSyntaxTree;

        errorLines = new ArrayList<>();
        errorInfo = new StringBuilder();

        toneOffset = 0;
        haftToneOffset = 0;
        channel = 0;

        paragraphMap = new HashMap<>();

        midiTracks = new ArrayList<>();

        DFS_Midi(AbstractSyntaxTree, null);

        midiFile = new MidiFile(midiTracks);
        midiFile.construct();

        if (getIsError())
            return null;

        return midiFile.toString();
    }

    private void DFS_Midi(Node curNode, Paragraph para) {
        Paragraph paragraph = para;
        List<Integer> noteList;
        List<Integer> durationList;
        int lineNoteCount = 0;
        int lineRhythmCount = 0;

        for (Node child : curNode.getChildren()) {
            switch (child.getType()) {
                case "score":
                    DFS_Midi(child, paragraph);
                    break;

                case "execution":
                    DFS_Midi(child, paragraph);
                    break;

                case "statement":
                    if (paragraphMap.containsKey(child.getChild(0).getContent())) {
                        errorInfo.append("Line: " + child.getChild(0).getLine() + "\t重复声明的段落名" + child.getChild(0).getContent() + "\n");
                        errorLines.add(child.getChild(0).getLine());
                    }
                    paragraph = new Paragraph();
                    paragraphMap.put(child.getChild(0).getContent(), paragraph);
                    break;

                case "instrument":
                    if (child.getChild(0).getContent().length() < 4 && Integer.parseInt(child.getChild(0).getContent()) >= 0 && Integer.parseInt(child.getChild(0).getContent()) < 128) {
                        paragraph.setInstrument(Byte.parseByte(child.getChild(0).getContent()));
                    } else {
                        errorInfo.append("Line: " + child.getChild(0).getLine() + "\t乐器声明超出范围（0-127）\n");
                        errorLines.add(child.getChild(0).getLine());
                    }
                    break;

                case "volume":
                    if (child.getChild(0).getContent().length() < 4 && Integer.parseInt(child.getChild(0).getContent()) >= 0 && Integer.parseInt(child.getChild(0).getContent()) < 128) {
                        paragraph.setVolume(Byte.parseByte(child.getChild(0).getContent()));
                    } else {
                        errorInfo.append("Line: " + child.getChild(0).getLine() + "\t音量声明超出范围（0-127）\n");
                        errorLines.add(child.getChild(0).getLine());
                    }
                    break;

                case "speed":
                    if (child.getChild(0).getContent().length() < 4) {
                        paragraph.setSpeed(Float.parseFloat(child.getChild(0).getContent()));
                    } else {
                        errorInfo.append("Line: " + child.getChild(0).getLine() + "\t速度声明超出范围（0-999）\n");
                        errorLines.add(child.getChild(0).getLine());
                    }
                    break;

                case "tonality":
                    toneOffset = 0;
                    for (Node tonality : child.getChildren()) {
                        switch (tonality.getContent()) {
                            case "#":
                                toneOffset += 1;
                                break;
                            case "b":
                                toneOffset -= 1;
                                break;
                            case "C":
                                break;
                            case "D":
                                toneOffset += 2;
                                break;
                            case "E":
                                toneOffset += 4;
                                break;
                            case "F":
                                toneOffset += 5;
                                break;
                            case "G":
                                toneOffset += 7;
                                break;
                            case "A":
                                toneOffset -= 3;
                                break;
                            case "B":
                                toneOffset -= 1;
                                break;
                        }
                    }
                    break;

                case "sentence":
                    DFS_Midi(child, paragraph);
                    break;

                case "end paragraph":
                    break;

                case "melody":
                    noteList = paragraph.getNoteList();
                    for (Node tone : child.getChildren()) {
                        switch (tone.getContent()) {
                            case "(":
                                toneOffset -= 12;
                                break;
                            case ")":
                                toneOffset += 12;
                                break;
                            case "[":
                                toneOffset += 12;
                                break;
                            case "]":
                                toneOffset -= 12;
                                break;
                            case "|":
                                paragraph.getSymbolQueue().offer(new Pair<>(0, noteList.size()));
                                break;
                            case "#":
                                haftToneOffset = 1;
                                break;
                            case "b":
                                haftToneOffset = -1;
                                break;
                            case "0":
                                lineNoteCount++;
                                noteList.add(0);
                                break;
                            case "1":
                                lineNoteCount++;
                                noteList.add(60 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "2":
                                lineNoteCount++;
                                noteList.add(62 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "3":
                                lineNoteCount++;
                                noteList.add(64 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "4":
                                lineNoteCount++;
                                noteList.add(65 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "5":
                                lineNoteCount++;
                                noteList.add(67 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "6":
                                lineNoteCount++;
                                noteList.add(69 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "7":
                                lineNoteCount++;
                                noteList.add(71 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                        }
                    }
                    break;

                case "rhythm":
                    durationList = paragraph.getDurationList();
                    Integer line = child.getChild(0).getLine();
                    for (Node rhythm : child.getChildren()) {
                        switch (rhythm.getContent()) {
                            case "{":
                                paragraph.getSymbolQueue().offer(new Pair<>(1, durationList.size()));
                                break;
                            case "}":
                                paragraph.getSymbolQueue().offer(new Pair<>(2, durationList.size()));
                                break;
                            case "1":
                                lineRhythmCount++;
                                durationList.add(480);
                                break;
                            case "1*":
                                lineRhythmCount++;
                                durationList.add(720);
                                break;
                            case "2":
                                lineRhythmCount++;
                                durationList.add(240);
                                break;
                            case "2*":
                                lineRhythmCount++;
                                durationList.add(360);
                                break;
                            case "4":
                                lineRhythmCount++;
                                durationList.add(120);
                                break;
                            case "4*":
                                lineRhythmCount++;
                                durationList.add(180);
                                break;
                            case "8":
                                lineRhythmCount++;
                                durationList.add(60);
                                break;
                            case "8*":
                                lineRhythmCount++;
                                durationList.add(90);
                                break;
                            case "g":
                                lineRhythmCount++;
                                durationList.add(30);
                                break;
                            case "g*":
                                lineRhythmCount++;
                                durationList.add(45);
                                break;
                            case "w":
                                lineRhythmCount++;
                                durationList.add(15);
                                break;
                            case "w*":
                                lineRhythmCount++;
                                errorInfo.append("Line: " + line + "\t不支持32分附点音符，即w*\n");
                                errorLines.add(line);
                                break;
                        }
                    }

                    if (lineNoteCount != lineRhythmCount) {
                        errorInfo.append("Line: " + line + "\t该句音符与时值数量不相同\n");
                        errorLines.add(line);
                    }

                    break;

                case "playlist":
                    String paraName = "";
                    int index = 0;
                    int totalDuration = 0;

                    for (Node playList : child.getChildren()) {
                        switch (playList.getContent()) {
                            case "&":
                                index++;
                                break;

                            case ",":
                                if (!paragraphMap.containsKey(paraName))
                                    break;

                                index = 0;

                                List<Integer> duration = paragraphMap.get(paraName).getDurationList();

                                for (int dura : duration)
                                    totalDuration += dura;

                                break;

                            default:
                                paraName = playList.getContent();

                                if (!paragraphMap.containsKey(paraName)) {
                                    errorInfo.append("Line: " + playList.getLine() + "\t未声明的段落名" + paraName + "\n");
                                    errorLines.add(playList.getLine());
                                    break;
                                }

                                MidiTrack midiTrack;

                                if (index > midiTracks.size() - 1) {
                                    midiTrack = constuctMidiTrackPart(paragraphMap.get(paraName), totalDuration);
                                    midiTracks.add(midiTrack);
                                } else {
                                    midiTrack = constuctMidiTrackPart(paragraphMap.get(paraName), 0);
                                    midiTracks.get(index).merge(midiTrack);
                                }

                                break;
                        }
                    }

                    for (MidiTrack midiTrack : midiTracks) {
                        midiTrack.setEnd();
                    }
            }
        }
    }

    private MidiTrack constuctMidiTrackPart(Paragraph paragraph, int duration) {
        if (getIsError())
            return null;

        MidiTrack midiTrack = new MidiTrack();
        midiTrack.setBpm(paragraph.getSpeed());
        midiTrack.setInstrument(channel, paragraph.getInstrument());
        midiTrack.addController(channel, (byte) 0x07, paragraph.getVolume());

        if (duration != 0)
            midiTrack.setDuration(duration);

        List<Integer> noteList = paragraph.getNoteList();
        List<Integer> durationList = paragraph.getDurationList();

        Queue<Note> bufferNotes = new PriorityQueue<>(Comparator.comparingInt(Note::getDeltaTime));

        Queue<Pair<Integer, Integer>> symbolQueue = paragraph.getSymbolQueue();

        byte channel;
        byte channelLast = -1;

        int count = noteList.size();
        for (int i = 0; i < count; i++) {
            if (!symbolQueue.isEmpty() && symbolQueue.peek().getValue() == i) {
                //i为符号后一个音符
                switch (symbolQueue.poll().getKey()) {
                    case 0:
                        if (symbolQueue.peek().getKey() != 0) {
                            //同时音中存在连音，语义错误
                            break;
                        }
                        do {
                            //添加同时音的noteOn
                            i++;
                        } while (symbolQueue.peek().getValue() != i);

                        //读到第二个|，插入noteOff
                        break;

                    case 1:
                        if (symbolQueue.peek().getKey() != 2) {
                            //连音符号中间存在同时音，语义错误
                            break;
                        }
                        //处理连音左括号
                        break;

                    case 2:
                        //处理连音右括号
                        break;
                }
            }

            if (noteList.get(i) != 0) {
                if (bufferNotes.isEmpty()) {
                    channel = 0;

                    if (channel == channelLast)
                        midiTrack.insertNoteOn(0, channel, noteList.get(i).byteValue(), (byte) 120, true);
                    else
                        midiTrack.insertNoteOn(0, channel, noteList.get(i).byteValue(), (byte) 120, false);

                    midiTrack.insertNoteOff(durationList.get(i), channel, noteList.get(i).byteValue(), true);

                    channelLast = channel;
                } else {
                    Note note = bufferNotes.poll();

                    if (note.getNote() == 0) {
                        int deltaTime = note.getDeltaTime();

                        while (!bufferNotes.isEmpty() && bufferNotes.peek().getNote() == 0)
                            deltaTime += bufferNotes.poll().getDeltaTime();

                        channel = 0;

                        if (channel == channelLast)
                            midiTrack.insertNoteOn(deltaTime, channel, noteList.get(i).byteValue(), (byte) 120, true);
                        else
                            midiTrack.insertNoteOn(deltaTime, channel, noteList.get(i).byteValue(), (byte) 120, false);

                        midiTrack.insertNoteOff(durationList.get(i), channel, noteList.get(i).byteValue(), true);

                        channelLast = channel;
                    } else {
                        //存在同时音
                    }
                }
            } else {
                Note zero = new Note(durationList.get(i), (byte) 0, (byte) 0, (byte) 0, true);
                bufferNotes.offer(zero);
            }
        }


//        todo 插入每一个音符前检测音符队列中队首音符的持续时间是否减为0，是则插入noteOff，否则直接插入这个音符的noteOn，并将noteOff和持续时间加入队列
//        for (int i = 0; i < noteList.size(); i++) {
//            midiTrack.insertNote(channel, noteList.get(i).byteValue(), durationList.get(i));
//        }
//        channel：同一音轨上的不同通道，同时音需设置两个channel值


        return midiTrack;
    }

    public boolean getIsError() {
        return !errorLines.isEmpty();
    }

    public List<Integer> getErrorLines() {
        return errorLines;
    }

    public String getErrors() {
        return errorInfo.toString();
    }

    public MidiFile getMidiFile() {
        return midiFile;
    }
}