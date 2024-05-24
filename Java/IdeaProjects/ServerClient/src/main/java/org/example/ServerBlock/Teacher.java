//yqn
package org.example.ServerBlock;

public class Teacher extends Person {
    public void answer(String questionID, String answerBody){
        personSQL.AnswerInsert(this.AccountID, questionID, answerBody,System.currentTimeMillis(), 0);
        this.messageSend("answer", "Finished");
    }

    //unused
    public void modify(String questionID, String answerBody){
        personSQL.AnswerUpdate(this.AccountID, "answerBody", answerBody);
        this.messageSend("answer", "Finished");
    }
}
