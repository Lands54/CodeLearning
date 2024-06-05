import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class LL1Machine {
    Grammar grammar;
    LL1Table ll1Table;
    LinkedList<String> inputBuffer = new LinkedList<>();
    Stack<String> VnBuffer = new Stack<>();

    LL1Machine(Grammar grammar){
        this.grammar = grammar;
        this.ll1Table = grammar.buildTable();
        reSet();
    }

    LinkedList deduce() throws NoInputToken, ErrorInputToken {
        LinkedList linkedList = new LinkedList();
        //DEDUCE
        matchUp();
        if (this.VnBuffer.isEmpty()) {
            LinkedList list = new LinkedList();
            list.add("ACC");
            reSet();
            return list;
        }
        String Vn = this.VnBuffer.peek();
        String Vt = this.inputBuffer.peek();
        if (Vt == null) {
            throw new NoInputToken(this);
        }
        LinkedList production = this.ll1Table.get(Vn, Vt);
        if (production == null) {
            throw new ErrorInputToken(Vn, Vt);
        }
        replace(production);
        linkedList.add(Vn);
        linkedList.add(production);
        return linkedList;
    }

    void reSet(){
        Iterator iterator = grammar.S.iterator();
        this.VnBuffer.push(grammar.End);
        this.VnBuffer.push((String) iterator.next());
    }

    void setInputBuffer(LinkedList<String> strings){
        for(int i = 0; i < strings.size(); i++){
            this.inputBuffer.add(strings.get(i));
        }
    }

    void setInputBuffer(String... strings){
        for(int i = 0; i < strings.length; i++){
            this.inputBuffer.add(strings[i]);
        }
    }

    private void replace(LinkedList right){
        this.VnBuffer.pop();
        if (right.get(0).equals(grammar.Epsilon))
            return;
        for(int i = right.size() - 1; i>=0; i--){
            VnBuffer.add((String) right.get(i));
        }
    }

    void in(String s){
        this.inputBuffer.add(s);
    }

    private void matchUp() {
        if (this.inputBuffer.isEmpty() | this.VnBuffer.isEmpty())
            return;
        if (this.inputBuffer.peek().equals(this.VnBuffer.peek())) {
            this.inputBuffer.poll();
            this.VnBuffer.pop();
            matchUp();
        }
    }

    LinkedList<TreeNode> generate(String tokenList){
        LinkedList<TreeNode> rootList = new LinkedList<>();
        LinkedList<Token> linkedList = Token.toTokenList(tokenList);
        Iterator<String> iterator = Token.toAttributeList(tokenList);
        TreeNode root = TreeNode.buildRoot("P", grammar);
        root.loadToken(linkedList);
        while (true) {
            try {
                LinkedList list = this.deduce();
                System.out.println(list);
                root.deduce(list);
                if (list.get(0).equals("ACC")) {
                    root.printTree(0);
                    rootList.add(root);
                    if (!iterator.hasNext())
                        break;
                    root = TreeNode.buildRoot("P", grammar);
                }
            } catch (NoInputToken e) {
                if (iterator.hasNext())
                    e.inputToken(iterator.next());
            } catch (ErrorInputToken e) {
                e.printToken();
                throw new RuntimeException();
            }
        }
        return rootList;
    }
}
