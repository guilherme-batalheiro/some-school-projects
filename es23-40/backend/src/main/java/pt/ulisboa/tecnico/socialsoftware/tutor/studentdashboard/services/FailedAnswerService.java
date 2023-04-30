package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.FailedAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.dto.FailedAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.StudentDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.FailedAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ANSWER_NOT_FOUND;

@Service
public class FailedAnswerService {

    @Autowired
    private StudentDashboardRepository studentDashboardRepository;

    @Autowired
    private FailedAnswerRepository failedAnswerRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FailedAnswerDto createFailedAnswer(int dashboardId, int questionAnswerId) {
        StudentDashboard studentDashboard = studentDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));

        FailedAnswer failedAnswer = new FailedAnswer(studentDashboard, questionAnswer, DateHandler.now());
        failedAnswerRepository.save(failedAnswer);

        return new FailedAnswerDto(failedAnswer);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeFailedAnswer(int failedAnswerId) {
        FailedAnswer toRemove = failedAnswerRepository.findById(failedAnswerId).orElseThrow(() -> new TutorException(ErrorMessage.FAILED_ANSWER_NOT_FOUND, failedAnswerId));
        toRemove.remove();
        failedAnswerRepository.delete(toRemove);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FailedAnswerDto> updateFailedAnswers(int dashboardId) {
        StudentDashboard studentDashboard = studentDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));

        LocalDateTime now = DateHandler.now();

        LocalDateTime start = getLastCheckDate(studentDashboard, now);
        LocalDateTime end = now;

        Set<QuizAnswer> answers = quizAnswerRepository.findByStudentAndCourseExecutionInPeriod(studentDashboard.getStudent().getId(),
                studentDashboard.getCourseExecution().getId(), start, end);

        answers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(Predicate.not(QuestionAnswer::isCorrect))
                .filter(qa -> studentDashboard.getFailedAnswers().stream()
                        .noneMatch(fa -> fa.getQuestionAnswer().getQuestion() == qa.getQuestion()))
                .filter(qa -> studentDashboard.getFailedAnswers().stream()
                        .noneMatch(fa -> fa.getQuestionAnswer() == qa))
                .forEach(questionAnswer -> createFailedAnswer(dashboardId, questionAnswer.getId()));

        studentDashboard.setLastCheckFailedAnswers(answers.stream()
                .filter(quizAnswer -> !quizAnswer.canResultsBePublic())
                .filter(quizAnswer -> studentDashboard.getLastCheckFailedAnswers() == null
                        || quizAnswer.getCreationDate().isAfter(studentDashboard.getLastCheckFailedAnswers()))
                .map(QuizAnswer::getCreationDate)
                .sorted()
                .findFirst()
                .map(localDateTime -> localDateTime.minusSeconds(1))
                .orElse(now));

        return studentDashboard.getFailedAnswers().stream()
                .map(FailedAnswerDto::new)
                .sorted(Comparator.comparing(FailedAnswerDto::getCollected, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private LocalDateTime getLastCheckDate(StudentDashboard studentDashboard, LocalDateTime now) {
        LocalDateTime startCheckDate;
        if (studentDashboard.getLastCheckFailedAnswers() == null) {
            Set<QuizAnswer> answers = quizAnswerRepository.findByStudentAndCourseExecution(studentDashboard.getStudent().getId(), studentDashboard.getCourseExecution().getId());

            startCheckDate = answers.stream()
                    .filter(quizAnswer -> quizAnswer.getCreationDate() != null)
                    .map(QuizAnswer::getCreationDate)
                    .sorted()
                    .findFirst()
                    .map(localDateTime -> localDateTime.minusSeconds(1))
                    .orElse(now);
        } else {
            startCheckDate = studentDashboard.getLastCheckFailedAnswers();
        }

        return startCheckDate;
    }
}