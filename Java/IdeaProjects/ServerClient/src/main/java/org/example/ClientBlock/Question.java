//byh
package org.example.ClientBlock;

public class Question {
    public int questionID;
    public String accountID;
    public String userName;
    public String title;
    public String body;
    public long lastTime;
    public Question(int questionID, String accountID, String userName, String title, String body, long lastTime){
        this.questionID = questionID;
        this.accountID = accountID;
        this.userName = userName;
        this.title = title;
        this.body = body;
        this.lastTime = lastTime;
    }

    public Answer[] getAnswer(int start, int size){
        String body = questionID + "@" + start + "@" + size;
        String[] strings = Client.clientDecoder.commandHandle("watchQuestionAnswer", body);
        Answer[] answers = new Answer[Integer.parseInt(strings[2])];
        try {
            for (int i = 0 ;i < Integer.parseInt(strings[2]); i++){
                answers[i] = new Answer(Integer.parseInt(strings[3 + 4 * i]), strings[4 + 4 * i], strings[5 + 4 * i], Integer.parseInt(strings[6 + 4 * i]));
            }
        } catch (Exception e) {
            return getAnswer(start, size);
        }
        return answers;
    }
}
