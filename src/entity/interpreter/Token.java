package entity.interpreter;

/**
 * 词类
 * 用于词法分析构造token数组
 * 以便进行语法分析
 */

public class Token {
    private int type;
    private String content;
    private int line;

    public Token(int type, String content, int line) {
        this.type = type;
        this.content = content;
        this.line = line;
    }

    public String toString() {
        String type = "";
        switch (this.type) {
            case -1:
                type = "错误";
                break;
            case 2:
                type = "段落符号";
                break;
            case 3:
                type = "速度符号";
                break;
            case 4:
                type = "调性符号";
                break;
            case 5:
                type = "段落结束标识";
                break;
            case 6:
                type = "播放操作";
                break;
            case 7:
                type = "左括号";
                break;
            case 8:
                type = "右括号";
                break;
            case 9:
                type = "高八度左括号";
                break;
            case 10:
                type = "高八度右括号";
                break;
            case 11:
                type = "连音左括号";
                break;
            case 12:
                type = "连音右括号";
                break;
            case 13:
                type = "时长左括号";
                break;
            case 14:
                type = "时长右括号";
                break;
            case 15:
                type = "附点";
                break;
            case 16:
                type = "逗号";
                break;
            case 17:
                type = "同时播放符号";
                break;
            case 18:
                type = "升号";
                break;
            case 19:
                type = "降号";
                break;
            case 20:
                type = "乐器符号";
                break;
            case 21:
                type = "音量符号";
                break;
            case 94:
                type = "休止符";
                break;
            case 95:
                type = "调性";
                break;
            case 96:
                type = "常数";
                break;
            case 97:
                type = "换行符";
                break;
            case 98:
                type = "旋律音符";
                break;
            case 99:
                type = "音符时值";
                break;
            case 100:
                type = "标识符";
                break;
        }
        if (line == -1)
            return String.format("%-9s\t%-13s\t类型码:%s\n", content, type, this.type);
        return String.format("Line%-7s\t%-18s\t%-13s\t类型码:%s\n", line, content, type, this.type);
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public int getLine() {
        return line;
    }

}