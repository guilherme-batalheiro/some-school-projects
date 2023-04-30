package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import javax.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.*;
import java.util.stream.Collectors;

@Entity
public class QuizStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    private int numQuizzes;

    private int numUniqueAnsweredQuizzes;

    private float averageQuizzesSolved;

    public QuizStats() {
    }

    public QuizStats(TeacherDashboard teacherDashboard, CourseExecution courseExecution) {
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
    }

    public void remove() {
        teacherDashboard.getQuizStats().remove(this);
        courseExecution = null;
        teacherDashboard = null;
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
        this.teacherDashboard.addQuizStats(this);
    }

    public int getNumQuizzes() {
        return numQuizzes;
    }

    public int getNumUniqueAnsweredQuizzes() {
        return numUniqueAnsweredQuizzes;
    }

    public void setNumQuizzes(int numQuizzes) {
        this.numQuizzes = numQuizzes;
    }

    public void setNumUniqueAnsweredQuizzes(int numUniqueAnsweredQuizzes) {
        this.numUniqueAnsweredQuizzes = numUniqueAnsweredQuizzes;
    }

    public float getAverageQuizzesSolved() {
        return averageQuizzesSolved;
    }

    public void setAverageQuizzesSolved(float averageQuizzesSolved) {
        this.averageQuizzesSolved = averageQuizzesSolved;
    }

    public void update() {
        int quizzesCount = courseExecution.getNumberOfQuizzes();
        this.setNumQuizzes(quizzesCount);

        int uniqueAnsweredQuizzes = (int) courseExecution.getQuizzes().stream()
                .distinct()
                .map(q -> q.getQuizAnswers().stream()
                        .filter(QuizAnswer::isCompleted).collect(Collectors.toSet()))
                .filter(qa -> !qa.isEmpty())
                .count();
        this.setNumUniqueAnsweredQuizzes(uniqueAnsweredQuizzes);

        int totalUniqueSolvedQuizzes = courseExecution.getQuizzes().stream()
                .distinct()
                .mapToInt(q -> q.getQuizAnswers().stream()
                        .filter(QuizAnswer::isCompleted).collect(Collectors.toSet()).size())
                .sum();

        int students = courseExecution.getStudents().size();
        float averageQuizzesSolved = students > 0 ? (float) totalUniqueSolvedQuizzes / students : 0.0f;
        this.setAverageQuizzesSolved(averageQuizzesSolved);

    }

    public void accept(Visitor visitor) {
        // only used for XML generation
    }

    @Override
    public String toString() {
        return "QuizStats{" +
                "id=" + id +
                ", courseExecutionId=" + courseExecution.getId() +
                ", teacherDashboardId=" + teacherDashboard.getId() +
                ", numQuizzes=" + numQuizzes +
                ", numUniqueAnsweredQuizzes=" + numUniqueAnsweredQuizzes +
                ", averageQuizzesSolved=" + averageQuizzesSolved +
                "}";
    }

}
