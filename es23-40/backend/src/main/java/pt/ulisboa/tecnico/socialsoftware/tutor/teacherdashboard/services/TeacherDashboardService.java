package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository;

import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TeacherDashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Autowired
    private StudentStatsRepository studentStatsRepository;

    @Autowired
    private QuizStatsRepository quizStatsRepository;

    @Autowired
    private QuestionStatsRepository questionStatsRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto getTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        Optional<TeacherDashboard> dashboardOptional = teacher.getDashboards().stream()
                .filter(dashboard -> dashboard.getCourseExecution().getId().equals(courseExecutionId))
                .findAny();

        return dashboardOptional.map(TeacherDashboardDto::new)
                .orElseGet(() -> createAndReturnTeacherDashboardDto(courseExecution, teacher));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto createTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (teacher.getDashboards().stream()
                .anyMatch(dashboard -> dashboard.getCourseExecution().equals(courseExecution)))
            throw new TutorException(TEACHER_ALREADY_HAS_DASHBOARD);

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        return createAndReturnTeacherDashboardDto(courseExecution, teacher);
    }

    private TeacherDashboardDto createAndReturnTeacherDashboardDto(CourseExecution courseExecution,
            Teacher teacher) {
        TeacherDashboard teacherDashboard = new TeacherDashboard(courseExecution, teacher);
        addStatisticsToTeacherDashboard(teacherDashboard);
        teacherDashboard.update();
        teacherDashboardRepository.save(teacherDashboard);
        return new TeacherDashboardDto(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboardRepository.delete(teacherDashboard);
        teacherDashboard.remove();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository
                .findById(dashboardId)
                .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));

        addStatisticsToTeacherDashboard(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAllTeacherDashboards() {
        teacherRepository.findAll()
                .forEach(teacher -> teacher.getCourseExecutions().stream()
                        .filter(this::noExceptionGetYear)
                        .forEach(courseExecution -> Optional
                                .ofNullable(teacher.getCourseExecutionDashboard(
                                        courseExecution))
                                .ifPresentOrElse(
                                        teacherDashboard -> updateTeacherDashboard(
                                                teacherDashboard.getId()),
                                        () -> createTeacherDashboard(
                                                courseExecution.getId(),
                                                teacher.getId()))));
    }

    private List<CourseExecution> getNPreviousExecutions(int n, CourseExecution courseExecution) {
        // A course execution may not have the academic term string well-formed.
        // We return here a more specific exception.
        if (!noExceptionGetYear(courseExecution)) {
            throw new TutorException(INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION);
        }
        // get a list of the n previous executions of the same course (including the
        // given courseExecution)
        return courseExecution.getCourse().getCourseExecutions().stream()
                .filter(this::noExceptionGetYear)
                .filter(ce -> ce.getYear() <= courseExecution.getYear())
                .sorted(Comparator.comparing(CourseExecution::getYear).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    private boolean noExceptionGetYear(CourseExecution courseExecution) {
        try {
            courseExecution.getYear();
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    private StudentStats createAndSaveNewStudentStats(TeacherDashboard teacherDashboard,
            CourseExecution courseExecution) {
        StudentStats studentStats = new StudentStats(teacherDashboard, courseExecution);
        studentStats.update();
        studentStatsRepository.save(studentStats);
        return studentStats;
    }

    private QuizStats createAndSaveNewQuizStats(TeacherDashboard teacherDashboard,
            CourseExecution courseExecution) {
        QuizStats quizStats = new QuizStats(teacherDashboard, courseExecution);
        quizStats.update();
        quizStatsRepository.save(quizStats);
        return quizStats;
    }

    private QuestionStats createAndSaveNewQuestionStats(TeacherDashboard teacherDashboard,
            CourseExecution courseExecution) {
        QuestionStats questionStats = new QuestionStats(teacherDashboard, courseExecution);
        questionStats.update();
        questionStatsRepository.save(questionStats);
        return questionStats;
    }

    private void addStatisticsToTeacherDashboard(TeacherDashboard teacherDashboard) {
        List<CourseExecution> lastThreeCourseExecutions = this.getNPreviousExecutions(3,
                teacherDashboard.getCourseExecution());

        teacherDashboard.getStudentStats().clear();
        teacherDashboard.getQuizStats().clear();
        teacherDashboard.getQuestionStats().clear();
        lastThreeCourseExecutions.stream().forEach(execution -> {
            createAndSaveNewStudentStats(teacherDashboard, execution);
            createAndSaveNewQuizStats(teacherDashboard, execution);
            createAndSaveNewQuestionStats(teacherDashboard, execution);
        });
    }
}
