//zgx
package org.example.ServerBlock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class PersonSQL extends SQLite {

    public void initDataBase(){
        try {
            this.createDB("iASK");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            this.setStmt();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            this.createTable(
                    "UserBasic",
                    new String[]{
                            "AccountID INTEGER PRIMARY KEY,",
                            "Password TEXT NOT NULL"
                    }
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            this.createTable(
                    "UserInformation",
                    new String[]{
                            "InformationID INTEGER PRIMARY KEY AUTOINCREMENT,",
                            "AccountID INTEGER UNIQUE,",
                            "UserName TEXT NOT NULL,",
                            "Identity TEXT NOT NULL,",
                            "Birthday TEXT NOT NULL,",
                            "Gender INTEGER NOT NULL,",
                            "FOREIGN KEY (AccountID) REFERENCES UserBasic(AccountID)"
                    }
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            this.createTable(
                    "Question",
                    new String[]{
                        "QuestionID INTEGER PRIMARY KEY AUTOINCREMENT,",
                        "AccountID INTEGER NOT NULL,",
                        "QuestionTitle TEXT NOT NULL,",
                        "QuestionBody TEXT NOT NULL,",
                        "QuestionTime INTEGER NOT NULL,",
                        "LastTime INTEGER NOT NULL,",
                        "FOREIGN KEY (AccountID) REFERENCES UserBasic(AccountID)"
                    }
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            this.createTable(
                    "Answer",
                    new String[]{
                        "AnswerID INTEGER PRIMARY KEY AUTOINCREMENT,",
                        "AccountID INTEGER NOT NULL,",
                        "QuestionID INTEGER NOT NULL,",
                        "AnswerBody TEXT NOT NULL,",
                        "AnswerTime INTEGER NOT NULL,",
                        "LikeNumber INTEGER NOT NULL,",
                        "FOREIGN KEY (AccountID) REFERENCES UserBasic(AccountID),",
                        "FOREIGN KEY (QuestionID) REFERENCES Question(QuestionID)"
                    }
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void UserBasicInsert(String accountID, String password){
        try {
            this.insertDate("UserBasic", "AccountID, Password", new String[]{accountID + "," +password});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String UserBasicSelect(String accountID){
        ResultSet resultSet;
        String password;
        try {
            resultSet = this.get("SELECT Password FROM UserBasic WHERE AccountID = " + accountID + ";");
            password = resultSet.getString("Password");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return password;
    }

    public void UserBasicUpdate(String accountId, String password){
        try {
            this.write("UPDATE UserBasic SET Password = " + password + " WHERE AccountID = " + accountId + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void UserInformationInsert(String accountID, String userName, String identity, String birthday, String gender){
        StringBuffer VALUES = new StringBuffer();
        VALUES.append(accountID);
        VALUES.append(',');
        VALUES.append(userName);
        VALUES.append(',');
        VALUES.append(identity);
        VALUES.append(',');
        VALUES.append(birthday);
        VALUES.append(',');
        VALUES.append(gender);
        try {
            this.insertDate("UserInformation", "AccountID, UserName, Identity, Birthday, Gender", new String[]{VALUES.toString()});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String UserInformationSelect(String accountID, String attribute){
        ResultSet resultSet;
        String atb;
        try {
            resultSet = this.get("SELECT " + attribute + " FROM UserInformation WHERE AccountID = " + accountID + ";");
            atb = resultSet.getString(attribute);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return atb;
    }

    public void UserInformationUpdate(String accountId, String attribute, String Value){
        try {
            this.write("UPDATE UserInformation SET " + attribute + " = " + Value + " WHERE AccountID = " + accountId + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void QuestionInsert(String accountID, String questionTitle, String questionBody, long questionTime, long lastTime){
        StringBuffer VALUES = new StringBuffer();
        VALUES.append(accountID);
        VALUES.append(',');
        VALUES.append(questionTitle);
        VALUES.append(',');
        VALUES.append(questionBody);
        VALUES.append(',');
        VALUES.append(questionTime);
        VALUES.append(',');
        VALUES.append(lastTime);
        try {
            this.insertDate("Question", "AccountID, QuestionTitle, QuestionBody, QuestionTime, LastTime", new String[]{VALUES.toString()});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String QuestionSelect(String questionID, String attribute){
        ResultSet resultSet;
        String atb;
        try {
            resultSet = this.get("SELECT " + attribute + " FROM Question WHERE QuestionID = " + questionID + ";");
            atb = resultSet.getString(attribute);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return atb;
    }

    public void QuestionUpdate(String questionID, String attribute, String Value){
        try {
            this.write("UPDATE Question SET " + attribute + " = '" + Value + "' WHERE AccountID = " + questionID + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void AnswerInsert(String accountID, String questionID, String answerBody, long answerTime, int likeNumber){
        StringBuffer VALUES = new StringBuffer();
        VALUES.append(accountID);
        VALUES.append(',');
        VALUES.append(questionID);
        VALUES.append(',');
        VALUES.append(answerBody);
        VALUES.append(',');
        VALUES.append(answerTime);
        VALUES.append(',');
        VALUES.append(likeNumber);
        try {
            this.insertDate("Answer", "AccountID, QuestionID, AnswerBody, AnswerTime, LikeNumber", new String[]{VALUES.toString()});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String AnswerSelect(String answerID, String attribute){
        ResultSet resultSet;
        String atb;
        try {
            resultSet = this.get("SELECT " + attribute + " FROM Answer WHERE AnswerID = " + answerID + ";");
            atb = resultSet.getString(attribute);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return atb;
    }

    public void AnswerUpdate(String answerID, String attribute, String Value){
        try {
            this.write("UPDATE Answer SET " + attribute + " = " + Value + " WHERE answerID = " + answerID + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> AnswerSelectQuestion(String questionID, int size) {
        size--;
        ArrayList<String> result = new ArrayList<>();
        try {
            String[] stbl = {"AnswerID", "AccountID", "answerBody", "LikeNumber"};
            ResultSet resultSet = this.get("select answerID, answer.accountID, answerbody, likenumber from answer, question where answer.questionid = question.questionid and answer.questionID = " + questionID + ";");
            if (!resultSet.next())
                return result;
            StringBuilder atb = new StringBuilder();
            do {
                for(String attribute:stbl) {
                    atb.append(resultSet.getString(attribute));
                    atb.append("@");
                }
                result.add(atb.toString());
                atb = new StringBuilder();
            }while (resultSet.next() & size-- > 0);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return result;
    }
}
