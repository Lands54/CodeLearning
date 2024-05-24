//byh
package org.example.ClientBlock;

public abstract class QuestionList{

    public static Question get(int questionID){
        String[] strings = null;
        try {
            String s = String.valueOf(questionID);
            strings = Client.clientDecoder.commandHandle("watchQuestion", s);
            return new Question(Integer.parseInt(s), strings[2], strings[3], strings[4], strings[5], 1);
        } catch (Exception e) {
            System.out.println(strings[2]);
            return null;
        }
    }

    public static Question[] getList(int start, int range){
        String[] temp = Client.clientDecoder.commandHandle("watchQuestionPage", start + "@" + range);
        Question[] questions = new Question[100];
        for (int i = 0;i<Integer.parseInt(temp[2]);i++){
            questions[i] = new Question(Integer.parseInt(temp[3 + 2* i]), "null", "null", temp[4 + 2* i], "null", 0);
        }
        return questions;
    }

    public static void main(String[] args) {
        Test.main(new String[]{"2", "2"});
    }
}
