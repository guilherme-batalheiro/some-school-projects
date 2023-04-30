package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion

@DataJpaTest
class TeacherDashboardQuestionStatsTest extends SpockTest {

    def createCourse() {
        def course = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        return course
    }

    def createCourseExecution(course, academicTerm, endDate) {
        def courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, academicTerm, course.getType(), endDate)
        courseExecutionRepository.save(courseExecution)
        return courseExecution
    }

    def createTeacher() {
        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)
        return teacher
    }

    def createQuestion(courseExecution, key) {
        def newQuestion = new Question()
        newQuestion.setTitle(QUESTION_1_TITLE)
        newQuestion.setCourse(courseExecution.getCourse())
        newQuestion.setStatus(Question.Status.AVAILABLE)
        newQuestion.setKey(key)

        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)

        def option = new Option()
        option.setContent(OPTION_1_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        def optionKO = new Option()
        optionKO.setContent(OPTION_2_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        return newQuestion;
    }

    def createQuiz(courseExecution, key, type = Quiz.QuizType.PROPOSED.toString()) {
        // Quiz
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(courseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        // Add Question
        def question = createQuestion(courseExecution, key)
        quizQuestion = createQuizQuestion(quiz, question)

        return quiz
    }

    def createQuizQuestion(quiz, question) {
        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def answerQuiz(quizQuestion, quiz, student, completed = true, date = DateHandler.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        def answerDetails
        def correctOption = quizQuestion.getQuestion().getQuestionDetails().getCorrectOption()
        answerDetails = new MultipleChoiceAnswer(questionAnswer, correctOption)
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }

    def createStudent(username, courseExecution) {
        def student = new Student(USER_1_USERNAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        return student;
    }

    def course
    def courseExecution1819
    def courseExecution1920
    def courseExecution2021
    def courseExecution2122
    def courseExecution2223
    def teacher
    def quizQuestion

    def setup() {
        course = createCourse()
        teacher = createTeacher()
        courseExecution1819 = createCourseExecution(course, "1 semestre 2018/19", DateHandler.toLocalDateTime("2018-12-31"))
        courseExecution1920 = createCourseExecution(course, "1 semestre 2019/20", DateHandler.toLocalDateTime("2019-12-31"))
        courseExecution2021 = createCourseExecution(course, "1 semestre", DateHandler.toLocalDateTime("2020-12-31"))
        courseExecution2122 = createCourseExecution(course, "1 semestre 2021/22", DateHandler.toLocalDateTime("2021-12-31"))
        courseExecution2223 = createCourseExecution(course, "1 semestre 2022/23", DateHandler.toLocalDateTime("2022-12-31"))
        teacher.addCourse(courseExecution1819)
        teacher.addCourse(courseExecution1920)
        teacher.addCourse(courseExecution2021)
        teacher.addCourse(courseExecution2122)
        teacher.addCourse(courseExecution2223)
    }

    def "get a dashboard for a course execution with no previous executions"() {
        when: "getting the dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution1819.getId(), teacher.getId())

        then: "a dashboard with one question stats is returned"
        teacherDashboardDto.getQuestionStats().size() == 1L

        and: "the stats of 18/19 are well initialized"
        def stats = teacherDashboardDto.getQuestionStats().get(0)
        stats.getNumAvailable() == 0
        stats.getAnsweredQuestionsUnique() == 0
        stats.getAverageQuestionsAnswered() == 0
        stats.getCourseExecutionYear() == 2018

        and: "one dashboard and one question stats exists in the database"
        teacherDashboardRepository.count() == 1L
        questionStatsRepository.count() == 1L
    }

    def "get a dashboard for a course execution with one previous execution"() {
        when: "getting the dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution1920.getId(), teacher.getId())

        then: "a dashboard with two question stats is returned"
        teacherDashboardDto.getQuestionStats().size() == 2L

        and: "the stats of 19/20 are well initialized"
        def stats1 = teacherDashboardDto.getQuestionStats().get(0)
        stats1.getNumAvailable() == 0
        stats1.getAnsweredQuestionsUnique() == 0
        stats1.getAverageQuestionsAnswered() == 0
        stats1.getCourseExecutionYear() == 2019

        and: "the stats of 18/19 are well initialized"
        def stats2 = teacherDashboardDto.getQuestionStats().get(1)
        stats2.getNumAvailable() == 0
        stats2.getAnsweredQuestionsUnique() == 0
        stats2.getAverageQuestionsAnswered() == 0
        stats2.getCourseExecutionYear() == 2018

        and: "one dashboard and two question stats exists in the database"
        teacherDashboardRepository.count() == 1L
        questionStatsRepository.count() == 2L
    }

    def "get a dashboard for a course execution with three previous executions, and a gap"() {
        when: "getting the dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution2223.getId(), teacher.getId())

        then: "a dashboard with two question stats is returned"
        teacherDashboardDto.getQuestionStats().size() == 3L

        and: "the stats of 22/23 are well initialized"
        def stats1 = teacherDashboardDto.getQuestionStats().get(0)
        stats1.getNumAvailable() == 0
        stats1.getAnsweredQuestionsUnique() == 0
        stats1.getAverageQuestionsAnswered() == 0
        stats1.getCourseExecutionYear() == 2022

        and: "the stats of 21/22 are well initialized"
        def stats2 = teacherDashboardDto.getQuestionStats().get(1)
        stats2.getNumAvailable() == 0
        stats2.getAnsweredQuestionsUnique() == 0
        stats2.getAverageQuestionsAnswered() == 0
        stats2.getCourseExecutionYear() == 2021

        and: "the stats of 19/20 are well initialized"
        def stats3 = teacherDashboardDto.getQuestionStats().get(2)
        stats3.getNumAvailable() == 0
        stats3.getAnsweredQuestionsUnique() == 0
        stats3.getAverageQuestionsAnswered() == 0
        stats3.getCourseExecutionYear() == 2019

        and: "one dashboard and three question stats exists in the database"
        teacherDashboardRepository.count() == 1L
        questionStatsRepository.count() == 3L
    }

    def "update the dashboard for a course execution after a question is created for the current course execution"() {
        given: "getting the dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution2223.getId(), teacher.getId())

        and: "a new question for that course execution"
        createQuiz(courseExecution2223, 1)

        when: "updating the dashboard for 22/23"
        teacherDashboardService.updateTeacherDashboard(teacherDashboardDto.getId())

        then: "the dashboard exists with 3 question stats"
        teacherDashboardRepository.count() == 1L
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getQuestionStats().size() == 3

        and: "the stats are updated with 1 question"
        def stats = teacherDashboard.getQuestionStats().get(0)
        stats.getNumAvailable() == 1
        stats.getAnsweredQuestionsUnique() == 0
        stats.getAverageQuestionsAnswered() == 0

        and: "one dashboard and three question stats exists in the database"
        teacherDashboardRepository.count() == 1L
        questionStatsRepository.count() == 3L
    }

    def "update the number of answered questions in a older course execution"() {
        given: "a dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution1920.getId(), teacher.getId())

        and: "a quiz with one answer"
        def quiz1 = createQuiz(courseExecution1920, 1)
        def student = createStudent(USER_2_USERNAME, courseExecution1920)
        def questionAnswer = answerQuiz(quizQuestion, quiz1, student)

        when: "updating the dashboard"
        teacherDashboardService.updateTeacherDashboard(teacherDashboardDto.getId())

        then: "the dashboard is updated with 1 student having solved at quizzes"
        teacherDashboardRepository.count() == 1L
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getQuestionStats().size() == 2L

        and: "stats for course execution in 19/20"
        def stats = teacherDashboard.getQuestionStats().get(0)
        stats.getNumAvailable() == 1
        stats.getAnsweredQuestionsUnique() == 1
        stats.getAverageQuestionsAnswered() == 1

        and: "one dashboard and two question stats exists in the database"
        teacherDashboardRepository.count() == 1L
        questionStatsRepository.count() == 2L
    }

    def "update the number of questions in a dashboard for a course execution with no previous executions"() {
        given: "a dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution1819.getId(), teacher.getId())

        and: "a new question for that course execution"
        createQuiz(courseExecution1819, 1)

        when: "updating the dashboard for 18/19"
        teacherDashboardService.updateTeacherDashboard(teacherDashboardDto.getId())

        then: "the dashboard exists with 1 question stats (current)"
        teacherDashboardRepository.count() == 1L
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getQuestionStats().size() == 1

        and: "stats for course execution in 18/19"
        def stats = teacherDashboard.getQuestionStats().get(0)
        stats.getNumAvailable() == 1
        stats.getAnsweredQuestionsUnique() == 0
        stats.getAverageQuestionsAnswered() == 0

        and: "one dashboard and one quiz stats exists in the database"
        teacherDashboardRepository.count() == 1L
        questionStatsRepository.count() == 1L
    }

    def "update all question statistics of multiple dashboards"() {
        given: "two dashboards"
        teacherDashboardService.getTeacherDashboard(courseExecution1819.getId(), teacher.getId())
        teacherDashboardService.getTeacherDashboard(courseExecution1920.getId(), teacher.getId())

        and: "a new question for the course execution of 18/19"
        createQuiz(courseExecution1819, 1)

        when: "updating all dashboards"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "the dashboard of 18/19 is updated"
        def teacherDashboard1819 = teacherDashboardService.getTeacherDashboard(courseExecution1819.getId(), teacher.getId())
        def stats1819 = teacherDashboard1819.getQuestionStats()
        stats1819.size() == 1
        stats1819.get(0).getNumAvailable() == 1
        stats1819.get(0).getAnsweredQuestionsUnique() == 0
        stats1819.get(0).getAverageQuestionsAnswered() == 0

        and: "the dashboard of 19/20 is updated"
        def teacherDashboard1920 = teacherDashboardService.getTeacherDashboard(courseExecution1920.getId(), teacher.getId())
        def stats1920 = teacherDashboard1920.getQuestionStats()
        stats1920.size() == 2

        //stats for 19/20
        stats1920.get(0).getNumAvailable() == 0
        stats1920.get(0).getAnsweredQuestionsUnique() == 0
        stats1920.get(0).getAverageQuestionsAnswered() == 0

        //stats for 18/19
        stats1920.get(1).getNumAvailable() == 1
        stats1920.get(1).getAnsweredQuestionsUnique() == 0
        stats1920.get(1).getAverageQuestionsAnswered() == 0

        and: "the dashboard not explicitly created were also created by the service (one for each of the 4 course executions)"
        teacherDashboardRepository.count() == 4L

        and: "the 9 question stats were created: one for 1819, two for 1920, and three for 2122 and 2223"
        questionStatsRepository.count() == 9L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}