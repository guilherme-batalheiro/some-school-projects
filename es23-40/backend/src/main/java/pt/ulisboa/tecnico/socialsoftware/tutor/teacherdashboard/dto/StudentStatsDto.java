package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;

public class StudentStatsDto {
    private int numStudents;
    private int numMore75CorrectQuestions;
    private int numAtLeast3Quizzes;
    private int courseExecutionYear;

    public StudentStatsDto(StudentStats studentStats) {
        this.numStudents = studentStats.getNumStudents();
        this.numMore75CorrectQuestions = studentStats.getNumMore75CorrectQuestions();
        this.numAtLeast3Quizzes = studentStats.getNumAtLeast3Quizzes();
        
        /*
         * The course execution is guaranteed to have year here, so we
         * do not catch the exception in this place
         */
        this.courseExecutionYear = studentStats.getCourseExecution().getYear();
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

    public int getCourseExecutionYear() {
        return courseExecutionYear;
    }

    public void setCourseExecutionYear(int courseExecutionYear) {
        this.courseExecutionYear = courseExecutionYear;
    }

    @Override
    public String toString() {
        return "StudentStatsDto{" +
                "numStudents=" + numStudents +
                ", numMore75CorrectQuestions=" + numMore75CorrectQuestions +
                ", numAtLeast3Quizzes=" + numAtLeast3Quizzes +
                ", courseExecutionYear=" + courseExecutionYear +
                '}';
    }
}
