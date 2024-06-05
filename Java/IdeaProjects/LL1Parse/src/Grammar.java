import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import static java.lang.Math.min;

public class Grammar {
    HashSet<String> Vt = new HashSet<>();
    HashSet<String> Vn = new HashSet<>();
    HashSet<String> S = new HashSet<>();
    HashMap<String, HashSet<LinkedList<String>>> Production = new HashMap<>();
    String End = "SEMI";
    String Epsilon = "Epsilon";
    HashMap<LinkedList<String>, HashSet<String>> FirstSet = new HashMap<>();
    HashMap<String, HashSet<String>> FollowSet = new HashMap<>();
    HashMap<String, String> wordToVn = new HashMap<>();
    void addVt(String s){
        if (!Epsilon.equals(s))
            this.Vt.add(s);
    }

    void addVn(String s){
        this.Vn.add(s);
    }

    void addS(String s){
        if (Vn.contains(s))
            this.S.add(s);
    }

    void addProduction(String left, LinkedList<String> right){
        if (right.size() == 0){
            addProduction(left, Epsilon);
            return;
        }
        String[] newRight = new String[right.size()];
        int index = 0;
        for(String s : right){
            newRight[index++] = s;
        }
        addProduction(left, newRight);
    }

    void initWord(){
        this.wordToVn.put("id", "IDN");
        this.wordToVn.put("DEC", "DEC");
        this.wordToVn.put("OCT", "OCT");
        this.wordToVn.put("HEX", "HEX");
        this.wordToVn.put("+", "ADD");
        this.wordToVn.put("-", "SUB");
        this.wordToVn.put("*", "MUL");
        this.wordToVn.put("/", "DIV");
        this.wordToVn.put(">", "GT");
        this.wordToVn.put("<", "LT");
        this.wordToVn.put("=", "EQ");
        this.wordToVn.put(">=", "GE");
        this.wordToVn.put("<=", "LE");
        this.wordToVn.put("<>", "NEQ");
        this.wordToVn.put("(", "SLP");
        this.wordToVn.put(")", "SRP");
        this.wordToVn.put(";", "SEMI");
        this.wordToVn.put("if", "IF");
        this.wordToVn.put("else", "ELSE");
        this.wordToVn.put("then", "THEN");
        this.wordToVn.put("while", "WHILE");
        this.wordToVn.put("do", "DO");
        this.wordToVn.put("begin", "BEGIN");
        this.wordToVn.put("end", "END");
        this.wordToVn.put("ILOCT", "ILOCT");
        this.wordToVn.put("ILHEX", "ILHEX");
    }

    void addProduction(String left, String... right){
        LinkedList<String> newProduction = new LinkedList<>();
        for(int i = 0; i<right.length; i++){
            if (wordToVn.containsKey(right[i]))
                right[i] = wordToVn.get(right[i]);
        }
        addVn(left);
        for(String s : right)
            if (!Vn.contains(s))
                addVt(s);
        Vt.remove(left);
        for (String s : right){
            newProduction.add(s);
        }
        if (Production.get(left) == null) {
            HashSet<LinkedList<String>> productions = new HashSet<>();
            productions.add(newProduction);
            Production.put(left, productions);
        }
        else {
            Production.get(left).add(newProduction);
        }
    }

    void eliminateLeftRecursion(){
        HashMap<String, HashSet> tempMap = new HashMap<>();
        for(String left : this.Production.keySet()){
            HashSet<LinkedList<String>> alpha = new HashSet<>();
            HashSet<LinkedList<String>> beta = new HashSet<>();
            for (LinkedList right : this.Production.get(left)) {
                if (left.equals(right.get(0))){
                    LinkedList<String> subS = new LinkedList<>(right.subList(1, right.size()));
                    subS.add(left + "'");
                    alpha.add(subS);
                } else {
                    LinkedList<String> subS = (LinkedList<String>) right.clone();
                    if (Epsilon.equals(subS.get(0)))
                        subS.pop();
                    subS.add(left + "'");
                    beta.add(subS);
                }
            }

            if (!alpha.isEmpty()){
                //add Epsilon
                LinkedList<String> temp = new LinkedList<>();
                temp.add(Epsilon);
                alpha.add(temp);
                //addVn
                Vn.add(left + "'");
                //beta
                tempMap.put(left, beta);
                //alpha
                tempMap.put(left + "'", alpha);
            }
        }
        for(String s : tempMap.keySet())
            Production.put(s, tempMap.get(s));
    }

    void eliminateLeftFactor(){
        HashMap<String, HashMap<Integer, HashSet<LinkedList<String>>>> hashMap = new HashMap<>();
        for (String s : Vn){
            int index = 0;
            HashSet<LinkedList<String>> productions = this.Production.get(s);
            HashMap<Integer, HashSet<LinkedList<String>>> tempSet = new HashMap<>();
            for (LinkedList<String> production : productions) {
                int length = 0;
                for (LinkedList<String> otherProduction : productions) {
                    if (production.equals(otherProduction))
                        continue;
                    length = length > sameLength(production, otherProduction) ? length : sameLength(production, otherProduction);
                }
                if (!tempSet.containsKey(length))
                    tempSet.put(length, new HashSet<>());
                tempSet.get(length).add(production);
            }
            hashMap.put(s, tempSet);
        }

        HashMap<String, HashSet<LinkedList<String>>> newProductions = new HashMap<>();
        for (String s : Vn){
            HashMap<Integer, HashSet<LinkedList<String>>> numSet = hashMap.get(s);
            HashSet<LinkedList<String>> answerSet = new HashSet<>();
            //findMax
            int minLength = 99999;
            for(Integer length : numSet.keySet()){
                if (length == 0)
                    continue;
                minLength = minLength < length? minLength:length;
            }
            if (minLength == 99999)
                minLength = 0;
            //addTrue
            if (numSet.containsKey(0))
                answerSet.addAll(numSet.get(0));
            //
            if (numSet.containsKey(minLength)) {
                for (LinkedList<String> nowProduction : numSet.get(minLength)) {
                    if (minLength == 0)
                        break;
                    LinkedList<String> ModifyProduction = new LinkedList<>();
                    ModifyProduction.addAll(new LinkedList<>(nowProduction.subList(0, minLength)));
                    ModifyProduction.add(s + "*");
                    answerSet.add(ModifyProduction);
                    this.Production.put(s, answerSet);
                    if (!newProductions.containsKey(s + "*"))
                        newProductions.put(s + "*", new HashSet<>());
                    newProductions.get(s + "*").add(new LinkedList<>(nowProduction.subList(minLength, nowProduction.size())));
                }
            }
        }
        for (String left : newProductions.keySet()){
            HashSet<LinkedList<String>> rights = newProductions.get(left);
            for (LinkedList<String> right : rights){
                this.addProduction(left, right);
            }
        }
    }

    private int sameLength(LinkedList<String> s1, LinkedList<String> s2){
        int i;
        for(i = 0 ; i < min(s1.size(), s2.size()); i++){
            if (!s1.get(i).equals(s2.get(i)))
                return i;
        }
        return i;
    }

    HashSet<String> getFirstSet(LinkedList<String> string) {
        try {
            if (this.FirstSet.containsKey(string))
                return this.FirstSet.get(string);
            HashSet<String> hashSet = new HashSet();
            if (Vt.contains(string.get(0))) {
                hashSet.add(string.get(0));
            } else if (Vn.contains(string.get(0))) {
                Iterator iterator = this.Production.get(string.get(0)).iterator();
                while (iterator.hasNext()) {
                    LinkedList<String> temp = (LinkedList<String>) iterator.next();
                    HashSet<String> tempSet = (HashSet<String>) getFirstSet(temp).clone();
                    if (tempSet.contains(Epsilon) & string.size() > 1) {
                        tempSet.remove(Epsilon);
                        tempSet.addAll(getFirstSet(new LinkedList<>(string.subList(1, string.size()))));
                    }
                    hashSet.addAll(tempSet);
                }
            } else {
                hashSet.add(Epsilon);
            }
            this.FirstSet.put(string, hashSet);
            return hashSet;
        } catch (Exception e) {
            HashSet<String> hashSet = new HashSet<>();
            hashSet.add(Epsilon);
            return hashSet;
        }
    }

    HashSet<String> getFirstSet(String string) {
        HashSet<String> hashSet = new HashSet();
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add(string);
        if (Vn.contains(string) | Vt.contains(string)) {
            return getFirstSet(linkedList);
        }
        throw new RuntimeException("get First Set error in " + string);
    }

    HashSet getFollowSet(String str){
        return this.FollowSet.get(str);
    }

    void buildFollowSet(){
        for(String s : S)
            if (this.FollowSet.containsKey(s))
                this.FollowSet.get(s).add(this.End);
            else {
                HashSet<String> hashSet = new HashSet<>();
                hashSet.add(this.End);
                this.FollowSet.put(s, hashSet);
            }
        for (String left : this.Production.keySet()){
            for(LinkedList<String> right : this.Production.get(left)){
                for (int i = 0; i < right.size(); i++){
                    String symbol = right.get(i);
                    if (Vn.contains(symbol)){
                        HashSet<String> nextSet = (HashSet<String>) getFirstSet(new LinkedList<>(right.subList(i + 1, right.size()))).clone();
                        if (nextSet.contains(Epsilon)) {
                            nextSet.remove(Epsilon);
                        }
                        if (this.FollowSet.containsKey(symbol))
                            this.FollowSet.get(symbol).addAll(nextSet);
                        else {
                            this.FollowSet.put(symbol, nextSet);
                        }
                    }
                }
            }
        }
        while(true) {
            int size1 = 0;
            for (String key : this.FollowSet.keySet()){
                for(String follow : this.FollowSet.get(key))
                    size1++;
            }
            for (String left : this.Production.keySet()) {
                for (LinkedList<String> right : this.Production.get(left)) {
                    for (int i = 0; i < right.size(); i++) {
                        String symbol = right.get(i);
                        if (Vn.contains(symbol)) {
                            HashSet<String> nextSet = getFirstSet(new LinkedList<>(right.subList(i + 1, right.size())));
                            if (nextSet.contains(Epsilon)) {
                                if (this.FollowSet.containsKey(symbol)) {
                                    this.FollowSet.get(symbol).addAll(this.FollowSet.get(left));
                                } else {
                                    this.FollowSet.put(symbol, this.FollowSet.get(left));
                                }
                            }
                        }
                    }
                }
            }
            int size2 = 0;
            for (String key : this.FollowSet.keySet()) {
                for (String follow : this.FollowSet.get(key))
                    size2++;
            }
            if (size1 == size2)
                break;
        }
    }

    LL1Table buildTable(){
        for(int i = 0; i<100; i++){
            this.eliminateLeftRecursion();
            this.eliminateLeftFactor();
        }
        this.buildFollowSet();
        LL1Table ll1Table = new LL1Table();
        for (String n : Vn) {
            for (LinkedList right : Production.get(n))
                for (Object t : this.getFirstSet(right))
                    ll1Table.add(n, (String) t, right);
        }
        LinkedList temp = new LinkedList();
        temp.add(Epsilon);
        for (String n : Vn) {
            if (this.getFirstSet(n).contains(Epsilon))
                for (Object t : this.getFollowSet(n)) {
                    ll1Table.safeAdd(n, (String) t, temp);
                }
        }
        return ll1Table;
    }

    void init(){
        Grammar grammar = this;
        grammar.initWord();
        grammar.addProduction("P", "L");
        grammar.addProduction("P", "L", "P");
        grammar.addProduction("L", "S");
        grammar.addProduction("S", "id", "=", "E");
        grammar.addProduction("S", "if", "C", "then", "S");
        grammar.addProduction("S", "if", "C", "then", "S", "else", "S");
        grammar.addProduction("S", "while", "C", "do", "S");
        grammar.addProduction("C", "E", ">", "E");
        grammar.addProduction("C", "E", "<", "E");
        grammar.addProduction("C", "E", "=", "E");
        grammar.addProduction("C", "E", ">=", "E");
        grammar.addProduction("C", "E", "<=", "E");
        grammar.addProduction("C", "E", "<>", "E");
        grammar.addProduction("E", "E", "+", "T");
        grammar.addProduction("E", "E", "-", "T");
        grammar.addProduction("E", "T");
        grammar.addProduction("T", "F");
        grammar.addProduction("T", "T", "*", "F");
        grammar.addProduction("T", "T", "/", "F");
        grammar.addProduction("F", "(", "E", ")");
        grammar.addProduction("F", "id");
        grammar.addProduction("F", "OCT");
        grammar.addProduction("F", "DEC");
        grammar.addProduction("F", "HEX");
        grammar.addS("P");
    }

}


