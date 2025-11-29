package form.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 回答データモデル
 */
public class Response {
    private String respondentId;
    private String timestamp;
    private int questionNum;
    private String questionText;
    private List<String> selectedChoices;
    private String reason;
    
    public Response() {
        this.selectedChoices = new ArrayList<>();
    }

    public Response(String respondentId, String timestamp, int questionNum,
                    String questionText, List<String> selectedChoices, String reason) {
        this.respondentId = respondentId;
        this.timestamp = timestamp;
        this.questionNum = questionNum;
        this.questionText = questionText;
        this.selectedChoices = new ArrayList<>(selectedChoices);
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
    
    public List<String> getSelectedChoices() {
        return selectedChoices;
    }

    public void setSelectedChoices(List<String> selectedChoices) {
        this.selectedChoices = selectedChoices;
    }

    public String getSelectedChoice() {
        if (selectedChoices == null || selectedChoices.isEmpty()) {
            return "";
        }
        return String.join("; ", selectedChoices);
    }

    public void setSelectedChoice(String selectedChoice) {
        if (selectedChoice == null || selectedChoice.isEmpty()) {
            this.selectedChoices = new ArrayList<>();
        } else {
            this.selectedChoices = new ArrayList<>();
            this.selectedChoices.add(selectedChoice);
        }
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
