import java.util.Iterator;
import java.util.LinkedList;

public class Token {
    String atr;
    String val;

    Token(String atr, String val){
        this.atr = atr;
        this.val = val;
    }

    static LinkedList<Token> toTokenList(String tokenList){
        String[] strings = tokenList.split("\n");
        LinkedList<Token> tokens = new LinkedList<>();
        for (String s : strings){
            String[] bundle = s.split(":");
            tokens.add(new Token(bundle[0], bundle[1]));
        }
        return tokens;
    }

    static Iterator<String> toAttributeList(String tokenList){
        LinkedList<String> linkedList = new LinkedList<>();
        for(Token token : toTokenList(tokenList)){
            linkedList.add(token.atr);
        }
        return linkedList.iterator();
    }
}
