package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FailedAnswer implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime collected;

    private boolean answered;

    @OneToOne
    private QuestionAnswer questionAnswer;

    @ManyToOne
    private StudentDashboard studentDashboard;

    public FailedAnswer() {
    }

    public FailedAnswer(StudentDashboard studentDashboard, QuestionAnswer questionAnswer, LocalDateTime collected) {
        if (studentDashboard.getCourseExecution() != questionAnswer.getQuizAnswer().getQuiz().getCourseExecution()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_FAILED_ANSWER);
        }

        if (studentDashboard.getStudent() != questionAnswer.getQuizAnswer().getStudent()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_FAILED_ANSWER);
        }

        if (!questionAnswer.getQuizAnswer().isCompleted() || questionAnswer.isCorrect()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_FAILED_ANSWER);
        }

        setCollected(collected);
        setAnswered(questionAnswer.isAnswered());
        setQuestionAnswer(questionAnswer);
        setDashboard(studentDashboard);
    }

    public void remove() {
        studentDashboard.getFailedAnswers().remove(this);
        studentDashboard = null;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getCollected() {
        return collected;
    }

    public void setCollected(LocalDateTime collected) {
        this.collected = collected;
    }

    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public QuestionAnswer getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public StudentDashboard getDashboard() {
        return studentDashboard;
    }

    public void setDashboard(StudentDashboard studentDashboard) {
        this.studentDashboard = studentDashboard;
        this.studentDashboard.addFailedAnswer(this);
    }

    @Override
    public void accept(Visitor visitor) {
        // only used to generate XML
    }

    @Override
    public String toString() {
        return "FailedAnswer{" +
                "id=" + id +
                ", answered=" + answered +
                ", questionAnswer=" + questionAnswer +
                "}";
    }
}