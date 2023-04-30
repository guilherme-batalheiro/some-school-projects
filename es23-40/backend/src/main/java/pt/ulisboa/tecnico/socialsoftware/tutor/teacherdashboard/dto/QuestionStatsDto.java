package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;

public class QuestionStatsDto {
    private int numAvailable;
    private int answeredQuestionsUnique;
    private float averageQuestionsAnswered;
    private int courseExecutionYear;

    public QuestionStatsDto() {
    }

    public QuestionStatsDto(QuestionStats questionStats) {
        this.numAvailable = questionStats.getNumAvailable();
        this.answeredQuestionsUnique = questionStats.getAnsweredQuestionsUnique();
        this.averageQuestionsAnswered = questionStats.getAverageQuestionsAnswered();

        /*
         * The course execution is guaranteed to have year here, so we
         * do not catch the exception in this place
         */
        this.courseExecutionYear = questionStats.getCourseExecution().getYear();
    }

    public int getNumAvailable() {
        return numAvailable;
    }

    public void setNumAvailable(int numAvailable) {
        this.numAvailable = numAvailable;
    }

    public int getAnsweredQuestionsUnique() {
        return answeredQuestionsUnique;
    }

    public void setAnsweredQuestionsUnique(int answeredQuestionsUnique) {
        this.answeredQuestionsUnique = answeredQuestionsUnique;
    }

    public float getAverageQuestionsAnswered() {
        return averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(float averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    public int getCourseExecutionYear() {
        return courseExecutionYear;
    }

    public void setCourseExecutionYear(int courseExecutionYear) {
        this.courseExecutionYear = courseExecutionYear;
    }

    @Override
    public String toString() {
        return "QuestionStatsDto{" +
                "numAvailable=" + numAvailable +
                ", answeredQuestionsUnique=" + answeredQuestionsUnique +
                ", averageQuestionsAnswered=" + averageQuestionsAnswered +
                ", courseExecutionYear=" + courseExecutionYear +
                '}';
    }
}
