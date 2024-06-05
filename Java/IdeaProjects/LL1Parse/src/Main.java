import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        //Grammar
        Grammar grammar = new Grammar();
        grammar.init();
        //FileRead
        TokenReader tokenReader = new TokenReader();
        String tokenList = tokenReader.read("output.txt");
        LL1Machine ll1Machine = new LL1Machine(grammar);
        ll1Machine.ll1Table.printTable();
        ll1Machine.generate(tokenList);
    }
}