package entity.interpreter;

/**
 * 符号类
 * 用于记录特殊符号及其位置
 * 以便对特殊符号进行处理
 *
 * 0    连音左括号
 * 1    同时音符
 * 2    连音右括号
 */

public class Symbol {

    private int symbol;

    private int position;

    public Symbol(int symbol, int position) {
        this.position = position;
        this.symbol = symbol;
    }

    public int getSymbol() {
        return symbol;
    }

    public int getPosition() {
        return position;
    }

}
