package component;

import entity.interpreter.Node;
import entity.interpreter.Token;
import java.util.ArrayList;
import java.util.List;

public class Syntactic {

    private List<Token> tokens;

    private Node AbstractSyntaxTree;

    private int index;

    private boolean sentenceError;

    private List<Integer> errorList;

    //Start
    public Node Parse(List<Token> tokens) {
        index = 0;
        errorList = new ArrayList<>();
        this.tokens = tokens;
        AbstractSyntaxTree = new Node("root");

        boolean hadPlay = false;
        for (index = 0; index < tokens.size(); index++) {
            if (tokens.get(index).getType() == 6) {
                hadPlay = true;
                //break;
            }
            if (hadPlay && tokens.get(index).getType() == 8) {
                if (index + 1 < tokens.size()) {
                    errorList.add(tokens.get(index + 1).getLine());
                    AbstractSyntaxTree.addChild(new Node("Error", "Line:" + tokens.get(index + 1).getLine() + "  乐谱请写在play语句之前"));
                    return AbstractSyntaxTree;
                }
                break;
            }
        }
        if (!hadPlay) {
            errorList.add(0);
            AbstractSyntaxTree.addChild(new Node("Error", "缺少play函数"));
            return AbstractSyntaxTree;
        }

        index = 0;

        while (index < tokens.size() && tokens.get(index).getType() != 6) {
            Node paragraph = parseParagraph();
            AbstractSyntaxTree.addChild(paragraph);
        }


        if (index < tokens.size()) {
            Node execution = parseExecution();
            AbstractSyntaxTree.addChild(execution);
        }

//        if (index<tokens.size()) {
//            errorList.add(tokens.get(index).getLine());
//            AbstractSyntaxTree.addChild(new Node("Error","Line:" + tokens.get(index).getLine() + "  乐谱请写再play语句之前！"));
//        }

        return AbstractSyntaxTree;
    }

    //paragraph -> 'paragraph' identifier speed tone { sentence } 'end'
    private Node parseParagraph() {
        Node paragraph = new Node("score");
        Node terminalNode;

        //statement
        Node statement = new Node("statement");

        //paragraph
        if (tokens.get(index).getType() != 2) {
            nextLine();
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  未知符号，请检查是否缺少paragraph声明");
        }
        index++;


        //identifier(段落名)
        if (tokens.get(index).getType() != 100) {
            if (!isAttributeIdentifier() && !isMelodyElement())
                nextLine();
            statement.addChild(new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少标识符"));
            errorList.add(tokens.get(index - 1).getLine());
        } else {
            terminalNode = new Node("identifier", tokens.get(index).getContent(), tokens.get(index).getLine());
            statement.addChild(terminalNode);
            index++;
        }
        paragraph.addChild(statement);


        int tempSyn = tokens.get(index).getType();
        boolean hadSpeed = false, hadTone = false, hadInstrument = false, hadVolume = false;
        while (!hadReadToEnd() && !paragraphHadEnd() && !isMelodyElement()) {            //提前遇到paragraph或play
            if (tempSyn == 2 | tempSyn == 6) {
                errorList.add(tokens.get(index).getLine());
                paragraph.addChild(new Node("Error", "Line: " + tokens.get(index).getLine() + "  缺少end标识"));
                return paragraph;
            }
            switch (tempSyn) {
                case 3:
                    //speed
                    if (hadSpeed) {
                        nextLine();
                        errorList.add(tokens.get(index - 1).getLine());
                        paragraph.addChild(new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  重复定义速度"));
                        break;
                    }
                    Node speed = parseSpeed();
                    paragraph.addChild(speed);
                    hadSpeed = true;
                    break;
                case 4:
                    //tone
                    if (hadTone) {
                        nextLine();
                        errorList.add(tokens.get(index - 1).getLine());
                        paragraph.addChild(new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  重复定义调"));
                        break;
                    }
                    Node tone = parseTone();
                    paragraph.addChild(tone);
                    hadTone = true;
                    break;
                case 20:
                    //instrument
                    if (hadInstrument) {
                        nextLine();
                        errorList.add(tokens.get(index - 1).getLine());
                        paragraph.addChild(new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  重复定义乐器"));
                        break;
                    }
                    Node instrument = parseInstrument();
                    paragraph.addChild(instrument);
                    hadInstrument = true;
                    break;
                case 21:
                    //volume
                    if (hadVolume) {
                        nextLine();
                        errorList.add(tokens.get(index - 1).getLine());
                        paragraph.addChild(new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  重复定义强度"));
                        break;
                    }
                    Node volume = parseVolume();
                    paragraph.addChild(volume);
                    hadVolume = true;
                    break;
                default:
                    nextLine();
                    errorList.add(tokens.get(index - 1).getLine());
                    paragraph.addChild(new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  未知标识符"));
                    break;
            }

            tempSyn = tokens.get(index).getType();
        }
        if (!hadSpeed) {
            Node speed = parseSpeed();
            paragraph.addChild(speed);
        }
        if (!hadTone) {
            Node tone = parseTone();
            paragraph.addChild(tone);
        }
        if (!hadInstrument) {
            Node instrument = parseInstrument();
            paragraph.addChild(instrument);
        }
        if (!hadVolume) {
            Node volume = parseVolume();
            paragraph.addChild(volume);
        }


        //{ sentence }
        while (!hadReadToEnd() && (tokens.get(index).getType() != 5)) {
            //没遇到end就遇到play或paragraph
            if (tokens.get(index).getType() == 6 | tokens.get(index).getType() == 2) {
                paragraph.addChild(new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少end标识"));
                errorList.add(tokens.get(index - 1).getLine());
                return paragraph;
            }

            sentenceError = false;

            Node sentence = parseSentence();
            paragraph.addChild(sentence);
        }

        //'end',因为上一步sentence判断遇到end才会跳出，所以这一步肯定是end
        terminalNode = new Node("end paragraph", "end", tokens.get(index).getLine());
        paragraph.addChild(terminalNode);
        index++;

        return paragraph;
    }

    //instument
    private Node parseInstrument() {
        Node instrument = new Node("instrument");
        Node terminalNode;
        if (tokens.get(index).getType() != 20) {
            //乐器编号
            terminalNode = new Node("instrumentValue", "0");
            instrument.addChild(terminalNode);

            return instrument;
        }

        index++;
        //乐器编号
        terminalNode = new Node("instrumentValue", tokens.get(index).getContent(), tokens.get(index).getLine());
        instrument.addChild(terminalNode);
        index++;

        return instrument;
    }

    private Node parseVolume() {
        Node volume = new Node("volume");
        Node terminalNode;
        if (tokens.get(index).getType() != 21) {
            //乐器编号
            terminalNode = new Node("volumeValue", "127");
            volume.addChild(terminalNode);

            return volume;
        }

        index++;
        //乐器编号
        terminalNode = new Node("volumeValue", tokens.get(index).getContent(), tokens.get(index).getLine());
        volume.addChild(terminalNode);
        index++;

        return volume;
    }

    //speed -> 'speed=' speedNum
    private Node parseSpeed() {
        //若当前token不为速度标识，设置默认速度
        if (tokens.get(index).getType() != 3) {
            return getSpeed();
        }
        //否则，获取速度数值
        Node speed = new Node("speed");
        Node terminalNode;

        terminalNode = new Node("speedValue", tokens.get(++index).getContent(), tokens.get(index).getLine());
        speed.addChild(terminalNode);
        index++;

        return speed;
    }

    //tone -> ([#|b] toneValue)|toneValue
    private Node parseTone() {
        //若当前token不为调性标识，设置默认调性
        if (tokens.get(index).getType() != 4) {
            return getTone();
        }

        //否则，获取调性
        Node tone = new Node("tonality");
        Node terminalNode;
        index++;

        //#|b
        if (tokens.get(index).getType() == 18 | tokens.get(index).getType() == 19) {
            terminalNode = new Node("lift mark", tokens.get(index).getContent(), tokens.get(index).getLine());
            tone.addChild(terminalNode);
            index++;
        }

        //tone value
        if (tokens.get(index).getType() != 95) {
            nextLine();
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  调号不正确");
        }
        terminalNode = new Node("tone value", tokens.get(index).getContent());
        tone.addChild(terminalNode);
        index++;

        return tone;
    }

    //sentence -> melody rhythm
    private Node parseSentence() {
        Node sentence = new Node("sentence");

        //melody
        Node melody = parseMelody();
        sentence.addChild(melody);
        if (sentenceError) {
            return sentence;
        }

        //rhythm
        Node rhythm = parseRhythm();
        sentence.addChild(rhythm);

        return sentence;
    }

    //melody -> { NotesInEight }
    private Node parseMelody() {
        Node melody = new Node("melody");

        int group = 0;
        int updown = 0;
        boolean doubleMeantime = true;

        while (index < tokens.size() && (tokens.get(index).getType() != 13)) {
            //'(',低八度左括号
            if (tokens.get(index).getType() == 7) {
                if (group > 0) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  八度转换错误");
                }
                group--;
                melody.addChild(new Node("lower left parentheses", "(", tokens.get(index).getLine()));
                index++;
                continue;
            }
            //')',低八度右括号
            if (tokens.get(index).getType() == 8) {
                if (group >= 0) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  八度转换错误");
                }
                if (tokens.get(index - 1).getType() == 7) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  括号内不能为空");
                }
                group++;
                melody.addChild(new Node("lower right parentheses", ")", tokens.get(index).getLine()));
                index++;
                continue;
            }
            //'['，高八度左括号
            if (tokens.get(index).getType() == 9) {
                if (group < 0) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  八度转换错误");
                }
                group++;
                melody.addChild(new Node("higher left parentheses", "[", tokens.get(index).getLine()));
                index++;
                continue;
            }
            //']',高八度右括号
            if (tokens.get(index).getType() == 10) {
                if (group <= 0) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  八度转换错误");
                }
                if (tokens.get(index - 1).getType() == 9) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  括号内不能为空");
                }
                group--;
                melody.addChild(new Node("higher right parentheses", "]", tokens.get(index).getLine()));
                index++;
                continue;
            }

            //同时音符号
            if (tokens.get(index).getType() == 22) {
                doubleMeantime = !doubleMeantime;
                if (doubleMeantime && (tokens.get(index - 1).getType() == 22 || tokens.get(index - 2).getType() == 22)) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  无意义同时音符号");
                }
                melody.addChild(new Node("meantime symbol", "|", tokens.get(index).getLine()));
                index++;
                continue;
            }

            //音符
            Node note = parseNotes();
            if (sentenceError) {
                errorList.add(tokens.get(index - 1).getLine());
                return new Node("Error", note.getContent());
            }

            melody.addChild(note);

        }//end while
        if (group != 0) {
            sentenceError = true;
            //isError = true;
            errorList.add(tokens.get(index - 1).getLine());
            nextLine();
            return new Node("Error", "Line: " + (tokens.get(index - 1).getLine()) + " 八度转换错误");
        }

        if (!doubleMeantime) {
            sentenceError = true;
            //isError = true;
            errorList.add(tokens.get(index - 1).getLine());
            nextLine();
            return new Node("Error", "Line: " + (tokens.get(index - 1).getLine()) + " 同时音符号数量不正确");
        }

        if (tokens.get(index - 1).getType() == 18 | tokens.get(index - 1).getType() == 19) {
            sentenceError = true;
            //isError = true;
            errorList.add(tokens.get(index - 1).getLine());
            nextLine();
            return new Node("Error", "Line: " + (tokens.get(index - 1).getLine()) + " 升降号后面没有音符");
        }

        return melody;
    }

    //NotesInEight -> '(' Notes ')' | '[' Notes ']' | Notes
    private Node parseNotesInEight() {
        Node notesInEight = new Node("NotesInEight");

        switch (tokens.get(index).getType()) {
            case 7:
                notesInEight.addChild(new Node("lower left parentheses", "(", tokens.get(index).getLine()));
                index++;

                notesInEight.addChild(parseNotes());
                if (sentenceError)
                    return notesInEight;

                if (tokens.get(index).getType() != 8) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少右括号");
                }
                notesInEight.addChild(new Node("lower right parentheses", ")", tokens.get(index).getLine()));
                index++;
                break;
            case 9:
                notesInEight.addChild(new Node("lower left parentheses", "[", tokens.get(index).getLine()));
                index++;

                notesInEight.addChild(parseNotes());
                if (sentenceError)
                    return notesInEight;

                if (tokens.get(index).getType() != 10) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少右括号");
                }
                notesInEight.addChild(new Node("lower right parentheses", "]", tokens.get(index).getLine()));
                index++;
                break;
            default:
                notesInEight.addChild(parseNotes());
        }

        return notesInEight;
    }

    //Notes -> ([#|b] notesValue) | notesValue | 0
    private Node parseNotes() {
        Node notes;

        if (tokens.get(index).getType() == 2 | tokens.get(index).getType() == 5 | tokens.get(index).getType() == 6) {
            sentenceError = true;
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少节奏");
        }

        //'0',休止符
        if (tokens.get(index).getType() == 94) {
            notes = new Node("Notes", "0", tokens.get(index).getLine());
            index++;
            return notes;
        }

        //#
        if (tokens.get(index).getType() == 18) {
            if (tokens.get(index - 1).getType() == 19) {
                nextLine();
                sentenceError = true;
                errorList.add(tokens.get(index - 1).getLine());
                return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  降号后面紧跟升号");
            }
            notes = new Node("lift mark", tokens.get(index).getContent(), tokens.get(index).getLine());
            index++;

            return notes;
        }

        //b
        if (tokens.get(index).getType() == 19) {
            if (tokens.get(index - 1).getType() == 18) {
                nextLine();
                sentenceError = true;
                errorList.add(tokens.get(index - 1).getLine());
                return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  升号后面紧跟降号");
            }
            notes = new Node("lift mark", tokens.get(index).getContent(), tokens.get(index).getLine());
            index++;

            return notes;
        }

        //notesValue
        if (tokens.get(index).getType() != 98) {
            nextLine();
            sentenceError = true;
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  音符不正确");
        }
        notes = new Node("notes value", tokens.get(index).getContent(), tokens.get(index).getLine());
        index++;

        return notes;
    }

    //rhythm -> '<' length '>'
    private Node parseRhythm() {
        Node rhythm = new Node("rhythm");
        Node terminalNode;

        //'<'
        if (tokens.get(index).getType() != 13) {
            nextLine();
            sentenceError = true;
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少节奏");
        }
        terminalNode = new Node("left Angle brackets", "<", tokens.get(index).getLine());
        rhythm.addChild(terminalNode);
        index++;

        //length
        boolean inCurlyBraces = false;
        while (!hadReadToEnd() && (tokens.get(index).getType() != 14)) {
            //'{'，连音左括号
            if (tokens.get(index).getType() == 11) {
                if (inCurlyBraces) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  连音括号中出现连音括号");
                }
                inCurlyBraces = true;
                terminalNode = new Node("leftCurlyBrace", "{", tokens.get(index).getLine());
                rhythm.addChild(terminalNode);
                index++;
                continue;
            }

            //'}',连音右括号
            if (tokens.get(index).getType() == 12) {
                if (!inCurlyBraces) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少连音左括号");
                }
                if (tokens.get(index - 1).getType() == 11 | tokens.get(index - 2).getType() == 11) {
                    nextLine();
                    sentenceError = true;
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  无意义连音括号");
                }
                inCurlyBraces = false;
                terminalNode = new Node("rightCurlyBrace", "}", tokens.get(index).getLine());
                rhythm.addChild(terminalNode);
                index++;
                continue;
            }

            //音符长度
            if (tokens.get(index).getType() != 99) {
                nextLine();
                sentenceError = true;
                errorList.add(tokens.get(index - 1).getLine());
                return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  节奏格式错误");
            }

            String len = "";
            len += tokens.get(index).getContent();
            index++;

            //附点
            if (tokens.get(index).getType() == 15) {
                len += "*";
                index++;
            }
            terminalNode = new Node("length", len, tokens.get(index).getLine());
            rhythm.addChild(terminalNode);
        }
        if (inCurlyBraces) {
            sentenceError = true;
            errorList.add(tokens.get(index - 1).getLine());
            nextLine();
            return new Node("Error", "Line: " + (tokens.get(index - 1).getLine()) + " 连音符号错误");
        }

        //'>'
        if (tokens.get(index).getType() != 14) {
            nextLine();
            sentenceError = true;
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少右尖括号");
        }
        terminalNode = new Node("left Angle brackets", ">", tokens.get(index).getLine());
        rhythm.addChild(terminalNode);
        index++;

        return rhythm;
    }

    //get default speed if never set
    private Node getSpeed() {
        Node speed = new Node("speed");
        Node terminalNode;

        terminalNode = new Node("speed value", "90", tokens.get(index).getLine());
        speed.addChild(terminalNode);

        return speed;
    }

    //get default tone if never set
    private Node getTone() {
        Node tone = new Node("tonality");
        Node terminalNode;

        terminalNode = new Node("tone value", "C", tokens.get(index).getLine());
        tone.addChild(terminalNode);

        return tone;
    }

    //execution -> play ( playlist )
    private Node parseExecution() {
        Node execution = new Node("execution");

        //play
        if (tokens.get(index).getType() != 6) {
            nextLine();
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少Play执行语句");
        }
        index++;

        //leftParentheses,(
        if (tokens.get(index).getType() != 7) {
            nextLine();
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少左小括号");
        }
        index++;

        //playlist
        Node playlist = parsePlayList();
        execution.addChild(playlist);

        //rightParentheses,(
        if (tokens.get(index).getType() != 8) {
            nextLine();
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少右小括号");
        }
        index++;

        return execution;
    }

    //playlist -> identifier { [&|,] identifier }
    private Node parsePlayList() {
        Node playlist = new Node("playlist");
        Node terminalNode;

        //identifier(段落名)
        if (tokens.get(index).getType() != 100) {
            nextLine();
            errorList.add(tokens.get(index - 1).getLine());
            return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少标识符");
        }
        terminalNode = new Node("identifier", tokens.get(index).getContent(), tokens.get(index).getLine());
        playlist.addChild(terminalNode);
        index++;

        while (!hadReadToEnd() && (tokens.get(index).getType() != 8)) {
            // "&" or ","
            switch (tokens.get(index).getType()) {
                case 16:
                    terminalNode = new Node("comma", ",", tokens.get(index).getLine());
                    break;
                case 17:
                    terminalNode = new Node("and", "&", tokens.get(index).getLine());
                    break;
                default:
                    errorList.add(tokens.get(index - 1).getLine());
                    return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少逗号或&符号");
            }
            playlist.addChild(terminalNode);
            index++;

            if (tokens.get(index).getType() != 100) {
                nextLine();
                errorList.add(tokens.get(index - 1).getLine());
                return new Node("Error", "Line: " + tokens.get(index - 1).getLine() + "  缺少标识符");
            }
            terminalNode = new Node("identifier", tokens.get(index).getContent(), tokens.get(index).getLine());
            playlist.addChild(terminalNode);
            index++;
        }

        return playlist;
    }

    //判断当前token是否为段落属性的标识
    private boolean isAttributeIdentifier() {
        int syn = tokens.get(index).getType();
        return syn == 3 | syn == 4 | syn == 20 | syn == 21;
    }

    //判断当前token是否为旋律部分的元素
    private boolean isMelodyElement() {
        int syn = tokens.get(index).getType();
        return (syn >= 7 && syn <= 10) | syn == 18 | syn == 19 | syn == 22 | syn == 94 | syn == 98;
    }

    //判断当前token是否为节奏部分的元素
    public boolean isRhythmElement() {
        int syn = tokens.get(index).getType();
        return (syn >= 11 && syn <= 15) | syn == 99;
    }

    //判断段落是否已结束
    private boolean paragraphHadEnd() {
        return tokens.get(index).getType() == 5;
    }

    //判断是否已经读到末尾token
    private boolean hadReadToEnd() {
        return !(index < tokens.size());
    }

    //换到下一行
    private void nextLine() {
        while (index < tokens.size() - 1 && tokens.get(index).getLine() == tokens.get(++index).getLine()) {
        }
        if (index == tokens.size() - 1) {
            while (tokens.get(index).getLine() == tokens.get(--index).getLine()) {
            }
            index++;
        }
    }

    public String getErrors(Node curNode) {
        StringBuilder errorsInfo = new StringBuilder();

        if (curNode.getType().equals("Error"))
            errorsInfo.append(curNode.getContent()).append("\n");

        for (Node child : curNode.getChildren()) {
            errorsInfo.append(getErrors(child));
        }
        return errorsInfo.toString();
    }

    public boolean getIsError() {
        return !errorList.isEmpty();
    }

    public List<Integer> getErrorList() {
        return errorList;
    }

}