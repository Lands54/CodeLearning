//yqn
package org.example.ServerBlock;

public class Asker extends Person {
    public void ask(String questionTitle, String questionBody){
        Person.personSQL.QuestionInsert(this.AccountID, questionTitle, questionBody, System.currentTimeMillis(), System.currentTimeMillis());
        this.messageSend("ask", "Finished");
    }

    public void modifyProblem(String questionID, String questionTitle, String questionBody){
        Person.personSQL.QuestionUpdate(questionID, "questionTitle", questionTitle);
        Person.personSQL.QuestionUpdate(questionID, "questionBody", questionBody);
        this.messageSend("modifyProblem", "Finished");
    }
}
