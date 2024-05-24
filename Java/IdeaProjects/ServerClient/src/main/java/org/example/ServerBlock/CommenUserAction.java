//yqn
package org.example.ServerBlock;

public interface CommenUserAction {
    public void firstInformation(String userName, String identity, String birthday, String gender);
    void changeUserName(String username);
    void changeIdentity(String identity);
    void changeBirthday(String birthday);
    void changeGender(String gender);
    void watchQuestionPage(String pageStart, String onePageNumber);
    void watchQuestion(String questionID);
    void like(String answerID);
    void watchSelfInformation();
}
