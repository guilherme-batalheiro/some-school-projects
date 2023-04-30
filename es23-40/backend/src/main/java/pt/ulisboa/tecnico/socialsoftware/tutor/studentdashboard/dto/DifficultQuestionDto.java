package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import java.io.Serializable;

public class DifficultQuestionDto implements Serializable {
    private Integer id;

    private int percentage;

    private QuestionDto questionDto;

    public DifficultQuestionDto() {
    }

    public DifficultQuestionDto(DifficultQuestion difficultQuestion) {
        setId(difficultQuestion.getId());
        setPercentage(difficultQuestion.getPercentage());
        setQuestionDto(new QuestionDto(difficultQuestion.getQuestion()));
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public QuestionDto getQuestionDto() {
        return questionDto;
    }

    public void setQuestionDto(QuestionDto questionDto) {
        this.questionDto = questionDto;
    }

    @Override
    public String toString() {
        return "DifficultQuestion{" +
                "id=" + id +
                ", percentage=" + percentage +
                ", questionDto=" + questionDto +
                "}";
    }
}