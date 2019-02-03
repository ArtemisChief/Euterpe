package entity.interpreter;

public class Symbol {
    private int symbol;
    private int position;

    public Symbol(int symbol,int position){
        this.position=position;
        this.symbol=symbol;
    }

    public int getSymbol() {
        return symbol;
    }

    public int getPosition() {
        return position;
    }
}
