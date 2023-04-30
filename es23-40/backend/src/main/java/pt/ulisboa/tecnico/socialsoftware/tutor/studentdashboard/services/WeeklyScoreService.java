package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.WeeklyScore;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.dto.WeeklyScoreDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.StudentDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.WeeklyScoreRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND;

@Service
public class WeeklyScoreService {

    @Autowired
    private WeeklyScoreRepository weeklyScoreRepository;

    @Autowired
    private StudentDashboardRepository studentDashboardRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<WeeklyScoreDto> updateWeeklyScore(Integer dashboardId) {
        if (dashboardId == null) {
            throw new TutorException(DASHBOARD_NOT_FOUND);
        }

        StudentDashboard studentDashboard = studentDashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));

        LocalDateTime now = DateHandler.now();

        createMissingWeeklyScores(studentDashboard, now);

        computeStatistics(studentDashboard);

        removeEmptyClosedWeeklyScores(studentDashboard);

        studentDashboard.setLastCheckWeeklyScores(now);

        return studentDashboard.getWeeklyScores().stream()
                .sorted(Comparator.comparing(WeeklyScore::getWeek, Comparator.reverseOrder()))
                .map(WeeklyScoreDto::new)
                .collect(Collectors.toList());
    }

    private void createMissingWeeklyScores(StudentDashboard studentDashboard, LocalDateTime now) {
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate currentWeek = now.with(weekSunday).toLocalDate();

        if (studentDashboard.getLastCheckWeeklyScores() == null) {
            WeeklyScore weeklyScore = new WeeklyScore(studentDashboard, currentWeek);
            weeklyScoreRepository.save(weeklyScore);
        }

        LocalDateTime lastCheckDate = getLastCheckDate(studentDashboard, now);

        while (lastCheckDate.isBefore(currentWeek.atStartOfDay())) {
            LocalDate week = lastCheckDate.with(weekSunday).toLocalDate();

            WeeklyScore weeklyScore = new WeeklyScore(studentDashboard, week);
            weeklyScoreRepository.save(weeklyScore);

            lastCheckDate = lastCheckDate.plusDays(7);
        }
    }

    private LocalDateTime getLastCheckDate(StudentDashboard studentDashboard, LocalDateTime now) {
        LocalDateTime startCheckDate;
        if (studentDashboard.getLastCheckWeeklyScores() == null) {
            startCheckDate = studentDashboard.getStudent().getQuizAnswers().stream()
                    .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution() == studentDashboard.getCourseExecution())
                    .filter(quizAnswer -> quizAnswer.getCreationDate() != null)
                    .map(QuizAnswer::getCreationDate)
                    .sorted()
                    .findFirst()
                    .orElse(now);
        } else {
            startCheckDate = studentDashboard.getLastCheckWeeklyScores();
        }

        return startCheckDate;
    }

    private void computeStatistics(StudentDashboard studentDashboard) {
        studentDashboard.getWeeklyScores().stream()
                .filter(Predicate.not(WeeklyScore::isClosed))
                .forEach(weeklyScore -> {
                    LocalDateTime start = weeklyScore.getWeek().atStartOfDay();
                    LocalDateTime end = weeklyScore.getWeek().plusDays(7).atStartOfDay();

                    Set<QuizAnswer> answers = quizAnswerRepository.findByStudentAndCourseExecutionInPeriod(studentDashboard.getStudent().getId(),
                            studentDashboard.getCourseExecution().getId(), start, end);

                    weeklyScore.computeStatistics(answers);
                });
    }

    private void removeEmptyClosedWeeklyScores(StudentDashboard studentDashboard) {
        Set<WeeklyScore> weeklyScoresToDelete = studentDashboard.getWeeklyScores().stream()
                .filter(weeklyScore -> weeklyScore.isClosed() && weeklyScore.getQuestionsAnswered() == 0)
                .collect(Collectors.toSet());

        weeklyScoresToDelete.forEach(weeklyScore -> {
            weeklyScore.remove();
            weeklyScoreRepository.delete(weeklyScore);
        });
    }
}