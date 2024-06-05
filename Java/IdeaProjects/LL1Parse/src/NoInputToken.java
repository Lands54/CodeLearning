public class NoInputToken extends Exception{
    LL1Machine ll1Machine;
    NoInputToken(LL1Machine ll1Machine){
        this.ll1Machine = ll1Machine;
    }

    void inputToken(String s){
        ll1Machine.in(s);
    }
}
