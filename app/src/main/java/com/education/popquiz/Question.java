package com.education.popquiz;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for Question object
 */
public class Question {
    private String question;
    private String answer;
    private List<String> options;
    private int type;

    public Question(String question, String answer, int type, List<String> options) {
        this.question = question;
        this.answer = answer;
        this.type = type;
        this.options = options;
    }

    public Question(String question, String answer, int type) {
        this.question = question;
        this.answer = answer;
        this.type = type;
        this.options = new ArrayList<String>();
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getQuestion() {
        return question;
    }

    public int getType() {
        return type;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
