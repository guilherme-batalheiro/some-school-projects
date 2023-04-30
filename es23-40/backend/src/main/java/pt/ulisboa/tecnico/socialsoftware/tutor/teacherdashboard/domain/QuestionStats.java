package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.Status;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;

@Entity
public class QuestionStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private CourseExecution execution;

    @ManyToOne
    private TeacherDashboard dashboard;

    private Integer numAvailable, answeredQuestionsUnique;
    private Float averageQuestionsAnswered;

    public QuestionStats () {}

    public QuestionStats (TeacherDashboard dashboard, CourseExecution execution) {
        setDashboard(dashboard);
        setCourseExecution(execution);
        numAvailable = 0;
        answeredQuestionsUnique = 0;
        averageQuestionsAnswered = 0.0f;        
    }

    public void setDashboard (TeacherDashboard dashboard) {
        this.dashboard = dashboard;
        dashboard.addQuestionStats(this);
    }

    public void setCourseExecution (CourseExecution execution) {
        this.execution = execution;
    }

    public CourseExecution getCourseExecution () {
        return this.execution;
    }

    public TeacherDashboard getTeacherDashboard () {
        return this.dashboard;
    }

    public Integer getId() {
        return this.id;
    }

    public void remove () {
        execution = null;
        dashboard.getQuestionStats().remove(this);
        dashboard = null;
    }

    public Integer getNumAvailable() {
        return this.numAvailable;
    }

    public Integer getAnsweredQuestionsUnique() {
        return this.answeredQuestionsUnique;
    }

    public Float getAverageQuestionsAnswered() {
        return this.averageQuestionsAnswered;
    }

    public void update() {
        // number of available questions
        this.numAvailable = (int) execution.getQuizzes().stream()
            .flatMap(q -> q.getQuizQuestions().stream())
            .map(QuizQuestion::getQuestion)
            .filter(q -> q.getStatus() == Status.AVAILABLE)
            .distinct()
            .count();
        
        // number of answered questions at least once
        this.answeredQuestionsUnique = (int) execution.getQuizzes().stream()
                .flatMap(q -> q.getQuizAnswers() .stream()
                    .flatMap(qa -> qa.getQuestionAnswers().stream()
                        .map(QuestionAnswer::getQuestion)))
            .distinct()
            .count();

        // number of students 
        int students = execution.getStudents().size();

        long uniqueAllStudents = execution.getStudents().stream().mapToLong(student -> 
            student.getQuizAnswers().stream().flatMap(
                qa -> qa.getQuestionAnswers().stream().map(QuestionAnswer::getQuestion)
            ).distinct().count()).sum();

        // average
        this.averageQuestionsAnswered = students > 0 ? (float) uniqueAllStudents / students : 0.0f;
    }

    @Override
    public void accept(Visitor visitor) {
        
    }

    @Override
    public String toString() {
        return "QuestionStats{" +
            "id=" + id +
            ", teacherDashboard=" + dashboard.getId() +
            ", courseExecution=" + execution.getId() +
            ", numAvailable=" + numAvailable +
            ", answeredQuestionsUnique=" + answeredQuestionsUnique +
            ", averageQuestionsAnswered=" + averageQuestionsAnswered +
            '}';
      }
}