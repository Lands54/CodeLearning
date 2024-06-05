import java.util.LinkedList;
import java.util.Stack;

public class TreeNode {
    Grammar grammar;
    static Stack<TreeNode> nodeStack = new Stack<>();
    static Stack<Token> tokenStack = new Stack<>();
    String V;
    String D;
    LinkedList<TreeNode> children = new LinkedList<>();
    TreeNode(String V, Grammar grammar){
        this.V = V;
        this.grammar = grammar;
    }
    static TreeNode buildRoot(String V, Grammar grammar){
        TreeNode root = nodeStack.push(new TreeNode(V, grammar));
        return root;
    }

    void loadToken(LinkedList<Token> tokens){
        while (!tokens.isEmpty())
            tokenStack.push(tokens.pollLast());
    }

    LinkedList<TreeNode> expand(LinkedList<String> production){
        for (int i = 0; i < production.size(); i++){
            this.children.add(new TreeNode(production.get(i), this.grammar));
        }
        return this.children;
    }

    void deduce(LinkedList production){
        if (production.get(0).equals("ACC")) {
            tokenStack.pop();
            return;
        }
        parse();
        LinkedList<String> right = (LinkedList<String>) production.get(1);
        LinkedList<TreeNode> list = nodeStack.pop().expand(right);
        for (int i = list.size() - 1; i >= 0; i--) {
            if (!right.get(i).equals(grammar.Epsilon))
                nodeStack.push(list.get(i));
        }
    }

    void parse(){
        if (nodeStack.peek().V.equals(tokenStack.peek().atr)){
            nodeStack.pop().D = tokenStack.pop().val;
            parse();
        }
    }

    void printTree(int deep){
        for (int i = 0; i < deep; i++) {
            System.out.print("  ");
        }
        System.out.print(this.V);
        if (grammar.Vt.contains(this.V))
            System.out.print("{value = " + this.D + "}");
        System.out.println();
        for(TreeNode child : this.children)
            child.printTree(deep + 1);
    }
}
