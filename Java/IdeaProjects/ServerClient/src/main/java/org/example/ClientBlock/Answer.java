//byh
package org.example.ClientBlock;

public class Answer {
    public int answerID;
    public String accountID;
    public String answerBody;
    public int like;

    public Answer(int answerID, String accountID, String answerBody, int like){
        this.answerID = answerID;
        this.accountID = accountID;
        this.answerBody = answerBody;
        this.like = like;
    }

    public void like(){
        Client.clientDecoder.commandHandle("like", String.valueOf(answerID));
        this.like++;
    }
}
