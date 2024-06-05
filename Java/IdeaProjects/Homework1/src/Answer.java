public class Answer {
    Person user;
    String body;
    Answer(Person user, String body){
        this.user = user;
        this.body = body;
    }

    void ShowAnswer(){
        System.out.println(user.userName + ":\n" + body);
    }
}
