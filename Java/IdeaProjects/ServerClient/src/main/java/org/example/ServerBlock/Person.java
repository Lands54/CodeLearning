//zgx
package org.example.ServerBlock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Person implements CommenUserAction {
    static PersonSQL personSQL;
    static Server server;

    String userName;
    String gender;
    String birthday;
    String AccountID;
    String identity;
    protected void messageSend(String message1, String message2){
        Person.server.putMessage(this.AccountID, message1, message2);
        this.refresh();
    }

    public static void register(String accountID, String password){
        try {
            personSQL.UserBasicInsert(accountID, password);
            personSQL.UserInformationInsert(accountID, "'unknown'", "'Swimer'", "'1990-1-1'", "'unknown'");
            server.putMessage(accountID, "register", "True");
        } catch (Exception e) {
            server.putMessage(accountID, "register", "False");
            e.printStackTrace();
        }
    }

    public static void login(String accountID, String password) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        boolean temp = Objects.equals(personSQL.UserBasicSelect(accountID), password);
        String job;
        if (temp){
            job = personSQL.UserInformationSelect(accountID, "Identity");
            Class user = null;
            try {
                if (job.equals("Asker") | job.equals("Teacher"))
                    user = Class.forName("org.example.ServerBlock." + job);
                else {
                    user = Class.forName("org.example.ServerBlock.Swimer");
                }
            } catch (Exception e) {
                user = Class.forName("org.example.ServerBlock.Swimer");
            }
            //init
            Person person = (Person) user.getConstructor().newInstance();
            person.AccountID = accountID;
            person.refresh();
            ServerCommandHandler.accountTable.put(accountID, person);
        }
        server.putMessage(accountID, "login", temp? "True":"False");
    }

    public void initUserInformation(){
        personSQL.UserInformationInsert(this.AccountID, "'unknown'", "'Swimer'", "'1990-1-1'", "10");
        messageSend("initUserInformation", "Finished");
    }

    public void watchQuestionPage(String pageStart, String onePageNumber){
        StringBuffer message = new StringBuffer();
        int size = 0;
        for(int i = Integer.parseInt(pageStart); i < Integer.parseInt(pageStart) + Integer.parseInt(onePageNumber); i++){
            String temp = personSQL.QuestionSelect(Integer.toString(i), "QuestionTitle");
            if (temp == null){
                break;
            }
            size++;
            message.append(i + "@");
            message.append(temp);
            message.append('@');
        }
        try {
            message.deleteCharAt(message.lastIndexOf("@"));
        } catch (Exception e) {
            messageSend("watchQuestionPage", String.valueOf(size));
            return;
        }
        messageSend("watchQuestionPage", size + "@" + message.toString());
    }

    public void watchQuestion(String questionID){
        StringBuffer message = new StringBuffer();
        message.append(personSQL.QuestionSelect(questionID, "AccountID"));
        message.append('@');
        message.append(personSQL.UserInformationSelect(personSQL.QuestionSelect(questionID, "AccountID"), "UserName"));
        message.append('@');
        message.append(personSQL.QuestionSelect(questionID, "QuestionTitle"));
        message.append('@');
        message.append(personSQL.QuestionSelect(questionID, "QuestionBody"));
        messageSend("watchQuestion", message.toString());
    }

    public void firstInformation(String userName, String identity, String birthday, String gender){
        personSQL.UserInformationInsert(this.AccountID, userName, identity, birthday, gender);
        refresh();
        messageSend("firstInformation", "Finished");
    }

    public void changeUserName(String userName){
        personSQL.UserInformationUpdate(this.AccountID, "UserName", userName);
        refresh();
        messageSend("changeUserName", userName);
    }

    public void changeIdentity(String identity){
        personSQL.UserInformationUpdate(this.AccountID, "Identity", identity);
        String idt = personSQL.UserInformationSelect(AccountID, "Identity");
        Class user = null;
        try {
            user = Class.forName("org.example.ServerBlock." + idt);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //init
        Person person = null;
        try {
            person = (Person) user.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        person.AccountID = AccountID;
        ServerCommandHandler.accountTable.put(this.AccountID, person);
        person.refresh();
        messageSend("changeIdentity", identity);
    }

    public void changeBirthday(String birthday){
        personSQL.UserInformationUpdate(this.AccountID, "Birthday", birthday);
        refresh();
        messageSend("changeBirthday", birthday);
    }

    public void changeGender(String gender){
        personSQL.UserInformationUpdate(this.AccountID, "Gender", gender);
        refresh();
        messageSend("changeGender", gender);
    }

    public void like(String answerID){
        //getlike
        int likeNumber = Integer.parseInt(personSQL.AnswerSelect(answerID, "LikeNumber")) + 1;
        //addlike
        personSQL.AnswerUpdate(answerID, "LikeNumber", String.valueOf(likeNumber));
        refresh();
        messageSend("like", String.valueOf(likeNumber));
    }

    //not send
    public void refresh(){
        this.userName = personSQL.UserInformationSelect(this.AccountID, "UserName");
        this.identity = personSQL.UserInformationSelect(this.AccountID, "Identity");
        this.birthday = personSQL.UserInformationSelect(this.AccountID, "Birthday");
        this.gender = personSQL.UserInformationSelect(this.AccountID, "Gender");
    }

    public void watchSelfInformation(){
        StringBuffer message = new StringBuffer();
        this.refresh();
        message.append(userName);
        message.append("@");
        message.append(identity);
        message.append("@");
        message.append(birthday);
        message.append("@");
        message.append(gender);
        messageSend("watchSelfInformation", String.valueOf(message));
    }

    public void watchInformation(String accountID){
        String tuserName = personSQL.UserInformationSelect(accountID, "UserName");
        String tidentity = personSQL.UserInformationSelect(accountID, "Identity");
        String tbirthday = personSQL.UserInformationSelect(accountID, "Birthday");
        String tgender = personSQL.UserInformationSelect(accountID, "Gender");
        StringBuffer message = new StringBuffer();
        this.refresh();
        message.append(tuserName);
        message.append("@");
        message.append(tidentity);
        message.append("@");
        message.append(tbirthday);
        message.append("@");
        message.append(tgender);
        messageSend("watchInformation", String.valueOf(message));
    }

    public void watchQuestionAnswer(String questionID, String start, String size){
        StringBuilder builder = new StringBuilder();
        ArrayList<String> temp = personSQL.AnswerSelectQuestion(questionID, Integer.parseInt(start) + Integer.parseInt(size));
        int len = temp.size() - Integer.parseInt(start) + 1 > 0 ? temp.size() - Integer.parseInt(start) + 1:0;
        builder.append(len);
        builder.append("@");
        int num = Integer.parseInt(size);
        for(int i = 0; i < len; i++){
            builder.append(temp.get(Integer.parseInt(start) + i - 1));
        }
        builder.deleteCharAt(builder.lastIndexOf("@"));
        messageSend("watchQuestionAnswer", builder.toString());
    }

}
