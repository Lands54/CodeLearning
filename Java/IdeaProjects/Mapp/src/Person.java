import java.util.ArrayList;

public class Person {
    static int globalID = 0;
    static ArrayList<Person> allUser = new ArrayList<>();
    String userName;
    int ID;
    Person(String userName){
        this.userName = userName;
        this.ID = globalID++;
        allUser.add(this);
    }
    Question Ask(String header, String body){
        return new Question(this, header, body);
    }

    void Answer(Question question, String body){
        question.answerList.add(new Answer(this, body));
    }

    public static void main(String[] args){
        Person A = new Person("A");
        Person B = new Person("B");
        Person C = new Person("C");
        //Ask
        A.Ask("this is a question", "this is question body");
        B.Ask("this is b question", "this is question body");
        Question.ShowAllQuestion();
        //Answer question 1
        Question temp = Question.getQuestion(1);
        B.Answer(temp, "this is b answer");
        C.Answer(temp, "this is another  c answer");
        temp.ShowDetail();
        //Answer question 2
        temp = Question.getQuestion(2);
        B.Answer(temp, "this is b answer too");
        C.Answer(temp, "this is another c answer too");
        temp.ShowDetail();
    }
}
