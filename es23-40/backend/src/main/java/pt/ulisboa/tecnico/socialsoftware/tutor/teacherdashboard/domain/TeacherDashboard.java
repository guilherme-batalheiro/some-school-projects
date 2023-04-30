package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class TeacherDashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Teacher teacher;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacherDashboard", orphanRemoval = true)
    private List<QuizStats> quizStats = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacherDashboard", orphanRemoval = true)
    private List<StudentStats> studentStats = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", orphanRemoval = true)
    private List<QuestionStats> questionStats = new ArrayList<>();

    public TeacherDashboard() {
    }

    public TeacherDashboard(CourseExecution courseExecution, Teacher teacher) {
        setCourseExecution(courseExecution);
        setTeacher(teacher);
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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacher.addDashboard(this);
    }

    public List<QuizStats> getQuizStats() {
        return quizStats;
    }

    public void setQuizStats(List<QuizStats> stats) {
        this.quizStats = stats;
    }

    public void addQuizStats(QuizStats quizStats) {
        this.quizStats.add(quizStats);
    }

    public List<StudentStats> getStudentStats() {
        return studentStats;
    }

    public void setStudentStats(List<StudentStats> stats) {
        this.studentStats = stats;
    }

    public void addStudentStats(StudentStats studentStat) {
        this.studentStats.add(studentStat);
    }

    public List<QuestionStats> getQuestionStats() {
        return questionStats;
    }

    public void setQuestionStats(List<QuestionStats> stats) {
        this.questionStats = stats;
    }

    public void addQuestionStats(QuestionStats stats) {
        questionStats.add(stats);
    }

    public void update() {
        this.quizStats.forEach(QuizStats::update);
        this.studentStats.forEach(StudentStats::update);
        this.questionStats.forEach(QuestionStats::update);
    }

    public void remove() {
        teacher.getDashboards().remove(this);
        teacher = null;
        new ArrayList<>(studentStats).forEach(StudentStats::remove);
        studentStats = null;
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacher=" + teacher +
                ", quizStats=" + quizStats +
                ", studentStats=" + studentStats +
                ", questionStats=" + questionStats +
                '}';
    }
}