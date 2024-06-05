public class ErrorInputToken extends Exception{
    String Vt;
    String Vn;
    ErrorInputToken(String Vn, String Vt){
        this.Vn = Vn;
        this.Vt = Vt;
    }

    void printToken(){
        System.out.println("Parse error in " + Vt);
        System.out.println("and " + Vn);
    }

}
