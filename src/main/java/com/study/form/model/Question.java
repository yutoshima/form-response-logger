package com.study.form.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 質問データモデル
 */
public class Question {
    private String text;
    private List<String> choices;
    
    public Question() {
        this.choices = new ArrayList<>();
    }
    
    public Question(String text, List<String> choices) {
        this.text = text;
        this.choices = new ArrayList<>(choices);
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public List<String> getChoices() {
        return choices;
    }
    
    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
    
    public void addChoice(String choice) {
        this.choices.add(choice);
    }
}
