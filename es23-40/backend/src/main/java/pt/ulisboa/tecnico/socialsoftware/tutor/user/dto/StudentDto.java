package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import java.io.Serializable;

public class StudentDto extends UserDto implements Serializable {
    private int numberOfTeacherQuizzes;
    private int numberOfInClassQuizzes;
    private int numberOfStudentQuizzes;
    private int numberOfTeacherAnswers;
    private int numberOfInClassAnswers;
    private int numberOfStudentAnswers;
    private int numberOfAnswers;
    private int percentageOfCorrectTeacherAnswers;
    private int percentageOfCorrectInClassAnswers;
    private int percentageOfCorrectStudentAnswers;
    private int percentageOfCorrectAnswers;

    public StudentDto() {}

    public StudentDto(Student student) {
        super(student);
    }

    public StudentDto(Student student, CourseExecution courseExecution) {
        this(student);

        StudentDashboard studentDashboard = student.getCourseExecutionDashboard(courseExecution);

        if (studentDashboard != null) {
            setNumberOfInClassQuizzes(studentDashboard.getNumberOfInClassQuizzes());
            setNumberOfStudentQuizzes(studentDashboard.getNumberOfStudentQuizzes());
            setNumberOfTeacherQuizzes(studentDashboard.getNumberOfTeacherQuizzes());
            setNumberOfInClassAnswers(studentDashboard.getNumberOfInClassAnswers());
            setNumberOfStudentAnswers(studentDashboard.getNumberOfStudentAnswers());
            setNumberOfTeacherAnswers(studentDashboard.getNumberOfTeacherAnswers());
            setNumberOfAnswers(getNumberOfTeacherAnswers() + getNumberOfInClassAnswers() + getNumberOfStudentAnswers());
            if (getNumberOfInClassAnswers() != 0)
                setPercentageOfCorrectInClassAnswers(studentDashboard.getNumberOfCorrectInClassAnswers() * 100 / getNumberOfInClassAnswers());
            if (getNumberOfStudentAnswers() != 0)
                setPercentageOfCorrectStudentAnswers(studentDashboard.getNumberOfCorrectStudentAnswers() * 100 / getNumberOfStudentAnswers());
            if (getNumberOfTeacherAnswers() != 0)
                setPercentageOfCorrectTeacherAnswers(studentDashboard.getNumberOfCorrectTeacherAnswers() * 100 / getNumberOfTeacherAnswers());
            if (getNumberOfAnswers() != 0)
                setPercentageOfCorrectAnswers((studentDashboard.getNumberOfCorrectInClassAnswers() + studentDashboard.getNumberOfCorrectStudentAnswers() + studentDashboard.getNumberOfCorrectTeacherAnswers()) * 100 / getNumberOfAnswers());
        }
    }

    public int getNumberOfTeacherQuizzes() {
        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(int numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public int getNumberOfInClassQuizzes() {
        return numberOfInClassQuizzes;
    }

    public void setNumberOfInClassQuizzes(int numberOfInClassQuizzes) {
        this.numberOfInClassQuizzes = numberOfInClassQuizzes;
    }

    public int getNumberOfStudentQuizzes() {
        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(int numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public int getNumberOfTeacherAnswers() {
        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(int numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public int getNumberOfInClassAnswers() {
        return numberOfInClassAnswers;
    }

    public void setNumberOfInClassAnswers(int numberOfInClassAnswers) {
        this.numberOfInClassAnswers = numberOfInClassAnswers;
    }

    public int getNumberOfStudentAnswers() {
        return numberOfStudentAnswers;
    }

    public void setNumberOfStudentAnswers(int numberOfStudentAnswers) {
        this.numberOfStudentAnswers = numberOfStudentAnswers;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public int getPercentageOfCorrectTeacherAnswers() {
        return percentageOfCorrectTeacherAnswers;
    }

    public void setPercentageOfCorrectTeacherAnswers(int percentageOfCorrectTeacherAnswers) {
        this.percentageOfCorrectTeacherAnswers = percentageOfCorrectTeacherAnswers;
    }

    public int getPercentageOfCorrectInClassAnswers() {
        return percentageOfCorrectInClassAnswers;
    }

    public void setPercentageOfCorrectInClassAnswers(int percentageOfCorrectInClassAnswers) {
        this.percentageOfCorrectInClassAnswers = percentageOfCorrectInClassAnswers;
    }

    public int getPercentageOfCorrectStudentAnswers() {
        return percentageOfCorrectStudentAnswers;
    }

    public void setPercentageOfCorrectStudentAnswers(int percentageOfCorrectStudentAnswers) {
        this.percentageOfCorrectStudentAnswers = percentageOfCorrectStudentAnswers;
    }

    public int getPercentageOfCorrectAnswers() {
        return percentageOfCorrectAnswers;
    }

    public void setPercentageOfCorrectAnswers(int percentageOfCorrectAnswers) {
        this.percentageOfCorrectAnswers = percentageOfCorrectAnswers;
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfInClassQuizzes=" + numberOfInClassQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", numberOfInClassAnswers=" + numberOfInClassAnswers +
                ", numberOfStudentAnswers=" + numberOfStudentAnswers +
                ", numberOfAnswers=" + numberOfAnswers +
                ", percentageOfCorrectTeacherAnswers=" + percentageOfCorrectTeacherAnswers +
                ", percentageOfCorrectInClassAnswers=" + percentageOfCorrectInClassAnswers +
                ", percentageOfCorrectStudentAnswers=" + percentageOfCorrectStudentAnswers +
                ", percentageOfCorrectAnswers=" + percentageOfCorrectAnswers +
                '}';
    }

}
