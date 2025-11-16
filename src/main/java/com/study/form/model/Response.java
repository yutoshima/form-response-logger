package com.study.form.model;

/**
 * 回答データモデル
 */
public class Response {
    private String respondentId;
    private String timestamp;
    private int questionNum;
    private String questionText;
    private String selectedChoice;
    private String reason;
    
    public Response() {}
    
    public Response(String respondentId, String timestamp, int questionNum,
                    String questionText, String selectedChoice, String reason) {
        this.respondentId = respondentId;
        this.timestamp = timestamp;
        this.questionNum = questionNum;
        this.questionText = questionText;
        this.selectedChoice = selectedChoice;
        this.reason = reason;
    }
    
    public String getRespondentId() {
        return respondentId;
    }
    
    public void setRespondentId(String respondentId) {
        this.respondentId = respondentId;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getQuestionNum() {
        return questionNum;
    }
    
    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getSelectedChoice() {
        return selectedChoice;
    }
    
    public void setSelectedChoice(String selectedChoice) {
        this.selectedChoice = selectedChoice;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
