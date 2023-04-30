package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

public class StudentDashboardDto {
    private Integer id;

    private String lastCheckFailedAnswers;

    private String lastCheckWeeklyScores;

    public StudentDashboardDto() {
    }

    public StudentDashboardDto(StudentDashboard studentDashboard) {
        id = studentDashboard.getId();
        lastCheckFailedAnswers = DateHandler.toISOString(studentDashboard.getLastCheckFailedAnswers());
        lastCheckWeeklyScores = DateHandler.toISOString(studentDashboard.getLastCheckWeeklyScores());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastCheckFailedAnswers() {
        return lastCheckFailedAnswers;
    }

    public void setLastCheckFailedAnswers(String lastCheckFailedAnswers) {
        this.lastCheckFailedAnswers = lastCheckFailedAnswers;
    }

    public String getLastCheckWeeklyScores() {
        return lastCheckWeeklyScores;
    }

    public void setLastCheckWeeklyScores(String lastCheckWeeklyScores) {
        this.lastCheckWeeklyScores = lastCheckWeeklyScores;
    }

    @Override
    public String toString() {
        return "StudentDashboardDto{" +
                "id=" + id +
                ", lastCheckFailedAnswers=" + lastCheckFailedAnswers +
                ", lastWeeklyStats=" + lastCheckWeeklyScores +
                "}";
    }
}
