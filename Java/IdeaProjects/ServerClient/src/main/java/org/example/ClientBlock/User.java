//qby
package org.example.ClientBlock;

import static java.lang.Thread.sleep;

public class User implements CommenUserAction {
    public String accountID;
    public String userName;
    public String identity;
    public String gender;
    public String birthday;
    public Question nowQuestion;
    public Answer[] nowAnswerList;
    public boolean register(String accountID, String password){
        this.accountID = accountID;
        accountID = accountID.replace("@", "~a~");
        password = password.replace("@", "~a~");
        String[] temp = Client.clientDecoder.commandHandle("register", accountID + "@" + password);
        if(temp[2].equals("True")) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean login(String accountID, String password){
        this.accountID = accountID;
        accountID = accountID.replace("@", "~a~");
        password = password.replace("@", "~a~");
        String[] temp = Client.clientDecoder.commandHandle("login", accountID + "@" + password);
        if(temp[2].equals("True")) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void firstInformation(String userName, String identity, String birthday, String gender) {
        String[] temp = Client.clientDecoder.commandHandle("firstInformation", userName + "@" + identity + "@" + birthday + "@" + gender);
        this.userName = userName;
        this.identity = identity;
        this.birthday = birthday;
        this.gender = gender;
    }

    @Override
    public void changeUserName(String username) {
        username = username.replace("@", "~a~");
        String[] temp = Client.clientDecoder.commandHandle("changeUserName", "'" + username + "'");
        this.userName = temp[2].split("'")[1];
    }

    @Override
    public void changeIdentity(String identity) {
        identity = identity.replace("@", "~a~");
        String[] temp = Client.clientDecoder.commandHandle("changeIdentity", "'" + identity + "'");
        this.identity = temp[2].split("'")[1];
    }

    @Override
    public void changeBirthday(String birthday) {
        birthday = birthday.replace("@", "~a~");
        String[] temp = Client.clientDecoder.commandHandle("changeBirthday", "'" + birthday + "'");
        this.birthday = temp[2].split("'")[1];
    }

    @Override
    public void changeGender(String gender) {
        gender = gender.replace("@", "~a~");
        String[] temp = Client.clientDecoder.commandHandle("changeGender", "'" + gender + "'");
        this.gender = temp[2].split("'")[1];
    }

    @Override
    public void watchQuestionPage(String pageStart, String onePageNumber) {
        QuestionList.getList(Integer.parseInt(pageStart), Integer.parseInt(onePageNumber));
    }

    @Override
    public void watchQuestion(String questionID) {
        nowQuestion = QuestionList.get(Integer.parseInt(questionID));
    }

    @Override
    public void like(String answerID) {
        String[] temp = Client.clientDecoder.commandHandle("like", answerID);
    }

    @Override
    public void watchSelfInformation() {
        String[] temp = Client.clientDecoder.commandHandle("watchSelfInformation", "");
        this.userName = temp[2];
        this.identity = temp[3];
        this.birthday = temp[4];
        this.gender = temp[5];
    }
    public User watchInformation(int accountID){
        String s[] = Client.clientDecoder.commandHandle("watchInformation", String.valueOf(accountID));
        User temp = new User();
        temp.userName = s[2];
        temp.identity = s[3];
        temp.birthday = s[4];
        temp.gender = s[5];
        return temp;
    }

    public void ask(String title, String body){
        title = title.replace("@", "~a~");
        body = body.replace("@", "~a~");
        Client.clientDecoder.commandHandle("ask", "'" + title + "'" + "@" + "'" + body + "'");
    }

    public void answer(int questionID, String body){
        body = body.replace("@", "~a~");
        Client.clientDecoder.commandHandle("answer", "'" + questionID + "'" + "@" + "'" + body + "'");
    }

    public static void main(String[] args) throws InterruptedException {
        User user = Client.open("127.0.0.1", 9999);
        user.login("1", "1");
        user.changeIdentity("Teacher");
        Answer[] answers = QuestionList.get(1).getAnswer(1, 20);
        System.out.println();
    }

}
