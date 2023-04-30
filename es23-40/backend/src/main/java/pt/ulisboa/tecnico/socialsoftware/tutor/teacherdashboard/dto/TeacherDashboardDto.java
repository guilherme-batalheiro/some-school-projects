package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import java.util.List;
import java.util.stream.Collectors;

public class TeacherDashboardDto {
    private Integer id;
    private List<StudentStatsDto> studentStats;
    private List<QuizStatsDto> quizStats;
    private List<QuestionStatsDto> questionStats;

    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();

        this.studentStats = teacherDashboard.getStudentStats().stream()
                .map(StudentStatsDto::new)
                .collect(Collectors.toList());

        this.quizStats = teacherDashboard.getQuizStats().stream()
                .map(QuizStatsDto::new)
                .collect(Collectors.toList());

        this.questionStats = teacherDashboard.getQuestionStats().stream()
                .map(QuestionStatsDto::new)
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<StudentStatsDto> getStudentStats() {
        return studentStats;
    }

    public void setStudentStats(List<StudentStatsDto> studentStats) {
        this.studentStats = studentStats;
    }

    public List<QuizStatsDto> getQuizStats() {
        return quizStats;
    }

    public void setQuizStats(List<QuizStatsDto> quizStats) {
        this.quizStats = quizStats;
    }

    public List<QuestionStatsDto> getQuestionStats() {
        return questionStats;
    }

    public void setQuestionStats(List<QuestionStatsDto> questionStats) {
        this.questionStats = questionStats;
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", studentStats=" + studentStats +
                ", quizStats=" + quizStats +
                ", questionStats=" + questionStats +
                "}";
    }
}
