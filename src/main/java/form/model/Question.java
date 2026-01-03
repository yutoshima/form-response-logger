package form.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 質問データモデル
 */
public class Question {
    private String text;
    private List<String> choices;
    private String modelImage;
    private String studentImage;
    private String type;
    private Integer min;
    private Integer max;

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

    public String getModelImage() {
        return modelImage;
    }

    public void setModelImage(String modelImage) {
        this.modelImage = modelImage;
    }

    public String getStudentImage() {
        return studentImage;
    }

    public void setStudentImage(String studentImage) {
        this.studentImage = studentImage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }
}
