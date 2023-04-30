package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;

public class QuizStatsDto {
    private int numQuizzes;
    private int numUniqueAnsweredQuizzes;
    private float averageQuizzesSolved;
    private int courseExecutionYear;

    public QuizStatsDto(QuizStats quizStats) {
        this.numQuizzes = quizStats.getNumQuizzes();
        this.numUniqueAnsweredQuizzes = quizStats.getNumUniqueAnsweredQuizzes();
        this.averageQuizzesSolved = quizStats.getAverageQuizzesSolved();

        /*
         * The course execution is guaranteed to have year here, so we
         * do not catch the exception in this place
         */
        this.courseExecutionYear = quizStats.getCourseExecution().getYear();
    }

    public int getNumQuizzes() {
        return numQuizzes;
    }

    public void setNumQuizzes(int numQuizzes) {
        this.numQuizzes = numQuizzes;
    }

    public int getNumUniqueAnsweredQuizzes() {
        return numUniqueAnsweredQuizzes;
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

    public int getCourseExecutionYear() {
        return courseExecutionYear;
    }

    public void setCourseExecutionYear(int courseExecutionYear) {
        this.courseExecutionYear = courseExecutionYear;
    }

    @Override
    public String toString() {
        return "QuizStatsDto{" +
                "numQuizzes=" + numQuizzes +
                ", numUniqueAnsweredQuizzes=" + numUniqueAnsweredQuizzes +
                ", averageQuizzesSolved=" + averageQuizzesSolved +
                ", courseExecutionYear=" + courseExecutionYear +
                "}";
    }
}
