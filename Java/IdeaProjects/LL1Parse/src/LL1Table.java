import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class LL1Table {
    HashMap<String, HashMap<String, LinkedList<String>>> table = new HashMap<>();
    void add(String Vn, String Vt, LinkedList<String> right){
        HashMap row;
        if (table.containsKey(Vn))
            row = table.get(Vn);
        else{
            row = new HashMap<>();
            table.put(Vn, row);
        }
        if (row.containsKey(Vt))
            throw new RuntimeException("存在左因子");
        row.put(Vt, right);
    }

    void safeAdd(String Vn, String Vt, LinkedList<String> right){
        HashMap row;
        if (table.containsKey(Vn))
            row = table.get(Vn);
        else{
            row = new HashMap<>();
            table.put(Vn, row);
        }
        if (row.containsKey(Vt))
            return;
        row.put(Vt, right);
    }

    void printTable(){
        System.out.println();
        for (String Vn : table.keySet()){
            int i = 0;
            for (String Vt : table.get(Vn).keySet()) {
                System.out.print("|%-30s|".formatted("(" + Vn + ", " + Vt + ")" + this.get(Vn, Vt).toString()));
                if (i++ == 3) {
                    i = 0;
                    System.out.println();
                }
            }
            System.out.println();
            for (i = 0 ; i < 150; i++){
                System.out.print("_");
            }
            System.out.println();
        }

    }

    LinkedList get(String Vn, String Vt){
        try {
            return table.get(Vn).get(Vt);
        } catch (Exception e) {
            System.out.println(Vn + " " + Vt);
            throw new RuntimeException(e);
        }
    }
}
