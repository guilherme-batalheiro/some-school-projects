package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.*;

@Entity
public class StudentStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    private int numStudents = 0;
    private int numMore75CorrectQuestions = 0;
    private int numAtLeast3Quizzes = 0;

    @ManyToOne
    private TeacherDashboard teacherDashboard;
    
    @OneToOne
    private CourseExecution courseExecution;

    public StudentStats() {

    }

    public StudentStats(TeacherDashboard teacherDashboard, CourseExecution courseExecution) {
        setTeacherDashboard(teacherDashboard);
        setCourseExecution(courseExecution);
    }

    public void remove() {
        teacherDashboard.getStudentStats().remove(this);
        courseExecution = null;
        teacherDashboard = null;
    }

    public Integer getId() {
        return id;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public int getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumMore75CorrectQuestions(int numMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = numMore75CorrectQuestions;
    }

    public int getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(int numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
        this.teacherDashboard.addStudentStats(this);
    }

    public CourseExecution getCourseExecution() {
        return this.courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    private Stream<QuizAnswer> getCompletedQuizAnswersForCourse(Student student, CourseExecution courseExecution) {
        return student.getQuizAnswers().stream()
                .filter(QuizAnswer::isCompleted)
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution() == courseExecution);
    }
    

    public void update() {
        Set<Student> students = courseExecution.getStudents();

        numStudents = students.size();

        numMore75CorrectQuestions = (int) students.stream()
        .filter(student ->
            { long numCorrectAnswers = getCompletedQuizAnswersForCourse(student, courseExecution)
                    .mapToLong(QuizAnswer::getNumberOfCorrectAnswers)
                    .sum();
                long numQuestions = getCompletedQuizAnswersForCourse(student, courseExecution)
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestionsNumber())
                    .sum();
                return (numQuestions > 0 ? numCorrectAnswers / (double) numQuestions : 0) > 0.75;
            })
        .count();

        numAtLeast3Quizzes = (int) students.stream()
                .filter(student -> getCompletedQuizAnswersForCourse(student, courseExecution)
                        .map(QuizAnswer::getQuiz)
                        .distinct()
                        .count() >= 3)
                .count();
    }

    public void accept(Visitor visitor) {
        // only used for XML generation
    }

    @Override
    public String toString() {
        return "StudentStats{" +
                "id=" + id +
                ", teacherDashboard=" + teacherDashboard.getId() +
                ", courseExecution=" + courseExecution.getId() +
                ", numStudents=" + numStudents +
                ", numMore75CorrectQuestions=" + numMore75CorrectQuestions +
                ", numAtLeast3Quizzes=" + numAtLeast3Quizzes +
                '}';
    }
}
