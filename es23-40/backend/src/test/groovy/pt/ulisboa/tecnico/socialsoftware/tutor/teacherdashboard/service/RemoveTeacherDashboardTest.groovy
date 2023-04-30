package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import spock.lang.Unroll

@DataJpaTest
class RemoveTeacherDashboardTest extends SpockTest {

    def teacher

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
    }

    def createTeacherDashboard() {
        def dashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(dashboard)
        return dashboard
    }

    def "remove a dashboard"() {
        given: "a dashboard"
        def dashboard = createTeacherDashboard()

        when: "the user removes the dashboard"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 0
        teacher.getDashboards().size() == 0
    }

    def "remove a dashboard and check that the associated quiz stats are also removed"() {
        given: "a dashboard"
        def dashboard = createTeacherDashboard()

        and: "a quiz stats associated with the dashboard"
        def quizStats = new QuizStats(dashboard, externalCourseExecution)
        quizStatsRepository.save(quizStats)

        when: "the user removes the dashboard"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 0
        teacher.getDashboards().size() == 0

        and: "quiz stats have been deleted"
        quizStatsRepository.findAll().size() == 0
    }

    def "remove a dashboard and check that the associated student statistics are also removed"() {
        given: "a dashboard with student statistics"
        def dashboard = createTeacherDashboard()
        def studentStats = new StudentStats(dashboard, externalCourseExecution)
        studentStatsRepository.save(studentStats)

        when: "the user removes the dashboard"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 0
        teacher.getDashboards().size() == 0
        
        and: "the student statistics are removed"
        studentStatsRepository.findAll().size() == 0
    }

    def "remove a dashboard and check that the associated question statistics are also removed"() {
        given: "a dashboard with question statistics"
        def dashboard = createTeacherDashboard()
        def questionStats = new QuestionStats(dashboard, externalCourseExecution)
        questionStatsRepository.save(questionStats)

        when: "the user removes the dashboard"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 0
        teacher.getDashboards().size() == 0

        and: "the question statistics are removed"
        questionStatsRepository.findAll().size() == 0
    }

    def "cannot remove a dashboard twice"() {
        given: "a removed dashboard"
        def dashboard = createTeacherDashboard()
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        when: "the dashboard is removed for the second time"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
    }

    @Unroll
    def "cannot remove a dashboard that doesn't exist with the dashboardId=#dashboardId"() {
        when: "an incorrect dashboard id is removed"
        teacherDashboardService.removeTeacherDashboard(dashboardId)

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [null, 10, -1]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
