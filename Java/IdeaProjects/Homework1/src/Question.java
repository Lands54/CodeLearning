import java.util.ArrayList;

public class Question{
    static ArrayList<Question> allQuestion = new ArrayList<>();
    Person user;
    String header;
    String body;
    ArrayList<Answer> answerList;
    Question(Person user, String header, String body){
        this.user = user;
        this.header = header;
        this.body = body;
        answerList = new ArrayList<>();
        allQuestion.add(this);
    }

    private void ShowQuestion(){
        System.out.println(user.userName + " ask:" + header);
    }

    void ShowDetail(){
        this.ShowQuestion();
        System.out.println(body);
        System.out.println("-----------------------");
        for (Answer x:this.answerList) {
            x.ShowAnswer();
            System.out.println("-----------------------");
        }
        System.out.println("=======================");
    }

    static void ShowAllQuestion(){
        System.out.println("=======================");
        int i = 0;
        for (Question x :allQuestion) {
            System.out.print(++i + ":");
            x.ShowQuestion();
            System.out.println("-----------------------");
        }
        System.out.println("=======================");
    }

    static Question getQuestion(int index){
        return allQuestion.get(index - 1);
    }

}
