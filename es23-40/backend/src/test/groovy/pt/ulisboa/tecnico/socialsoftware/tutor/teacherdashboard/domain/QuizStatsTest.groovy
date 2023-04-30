package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import java.lang.reflect.Field

import spock.lang.Unroll

@DataJpaTest
class QuizStatsTest extends SpockTest {

    def teacherDashboard
    def quiz
    def quizQuestion


    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(DEMO_TEACHER_NAME, DEMO_TEACHER_NAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def createQuizStat() {
        def quizStat = new QuizStats(teacherDashboard, externalCourseExecution)
        quizStatsRepository.save(quizStat)
        return quizStat
    }

    def createQuiz(type = Quiz.QuizType.PROPOSED.toString()) {
        // Quiz
        quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        // Add Question
        def question = createQuestion()
        quizQuestion = createQuizQuestion(quiz, question)

        return quiz
    }

    def createQuestion() {
        def newQuestion = new Question()
        newQuestion.setTitle(QUESTION_1_TITLE)
        newQuestion.setCourse(externalCourse)
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

    def createStudent(username) {
        def student = new Student(USER_1_USERNAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        return student;
    }


    def "create empty quiz stat"() {
        when: "a quiz stat is created"
        def quizStat = createQuizStat()

        then: "a quiz stat is persisted"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.getNumQuizzes() == 0
        result.getNumUniqueAnsweredQuizzes() == 0
        result.getAverageQuizzesSolved() == 0

        and: "a quiz stat is returned"
        quizStat.getId() != 0
        quizStat.getCourseExecution().getId() == externalCourseExecution.getId()
        quizStat.getTeacherDashboard().getId() == teacherDashboard.getId()
        quizStat.getNumQuizzes() == 0
        quizStat.getNumUniqueAnsweredQuizzes() == 0
        quizStat.getAverageQuizzesSolved() == 0

        and: "the dashboard has a reference for the quiz stat"
        teacherDashboard.getQuizStats().size() == 1
        teacherDashboard.getQuizStats().contains(result)
    }


    @Unroll
    def "create quiz stat for a course execution with numQuizzes=#numQuizzes | numUniqueQuizzes=#numUniqueQuizzes | averageQuizzesSolved=#averageQuizzesResolved"() {
        when: "a quiz stat is created and updated with #numQuizzes, #numUniqueQuizzes and #averageQuizzesResolved"
        def quizStat = createQuizStat()
        quizStat.setNumQuizzes(numQuizzes)
        quizStat.setNumUniqueAnsweredQuizzes(numUniqueQuizzes)
        quizStat.setAverageQuizzesSolved(averageQuizzesResolved)

        then: "a quiz stat is persisted"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.getNumQuizzes() == numQuizzes
        result.getNumUniqueAnsweredQuizzes() == numUniqueQuizzes
        result.getAverageQuizzesSolved() == averageQuizzesResolved

        where:
        numQuizzes << [0, 100]
        numUniqueQuizzes << [0, 100]
        averageQuizzesResolved << [0, 100]
    }

    def "create quiz stat for a course execution with a quiz and update quiz stat"() {
        when: "a quiz stat is created"
        def quizStat = createQuizStat()

        and: "create quiz, adding to course execution"
        def quiz = createQuiz()

        and: "the quiz stat is updated"
        quizStat.update()

        then: "the quiz stat has the correct number of quizzes"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.getNumQuizzes() == 1
        result.getNumUniqueAnsweredQuizzes() == 0
        result.getAverageQuizzesSolved() == 0
    }

    def "create and update a quiz stat for a course execution with two quizzes, one answered"() {
        given: "a quiz with one answer"
        def quiz1 = createQuiz()
        def student = createStudent(USER_1_USERNAME)
        def questionAnswer = answerQuiz(quizQuestion, quiz1, student)

        and: "add a second unanswered quiz"
        def quiz2 = createQuiz()

        when: "a quiz stat is created"
        def quizStat = createQuizStat()

        and: "the quiz stat is updated"
        quizStat.update()

        then: "the quiz stat has the correct number of quizzes"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.getNumQuizzes() == 2
        result.getNumUniqueAnsweredQuizzes() == 1
        result.getAverageQuizzesSolved() == 1
    }

    @Unroll
    def "create and update a quiz stat for a course execution with three answered quizzes, updated via teacherDashboard: #useTeacherDashboardUpdate"() {
        given: "a quiz with two answers"
        def quiz1 = createQuiz()
        def student = createStudent(USER_1_USERNAME)
        answerQuiz(quizQuestion, quiz1, student)
        def student2 = createStudent(USER_2_USERNAME)
        answerQuiz(quizQuestion, quiz1, student2)

        and: "add a second quiz with one answer"
        def quiz2 = createQuiz()
        answerQuiz(quizQuestion, quiz2, student)

        when: "a quiz stat is created"
        def quizStat = createQuizStat()

        and: "the quiz stat is updated using teacherDashboard: #useTeacherDashboardUpdate "
        useTeacherDashboardUpdate ? teacherDashboard.update() : quizStat.update()

        then: "the quiz stat has the correct average of unique solved quizzes"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.getNumQuizzes() == 2
        result.getNumUniqueAnsweredQuizzes() == 2
        result.getAverageQuizzesSolved() == 1.5f

        where:
        useTeacherDashboardUpdate << [false, true]
    }

    def "create and update a quiz stat for a course execution with three answered quizzes, one not completed"() {
        given: "a quiz with two answers (one not completed)"
        def quiz1 = createQuiz()
        def student = createStudent(USER_1_USERNAME)
        answerQuiz(quizQuestion, quiz1, student)
        def student2 = createStudent(USER_2_USERNAME)
        answerQuiz(quizQuestion, quiz1, student2)

        and: "add a second quiz with one answer"
        def quiz2 = createQuiz()
        answerQuiz(quizQuestion, quiz2, student, false)

        when: "a quiz stat is created"
        def quizStat = createQuizStat()

        and: "the quiz stat is updated"
        quizStat.update()

        then: "the quiz stat has the correct average of unique solved quizzes"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.getNumQuizzes() == 2
        result.getNumUniqueAnsweredQuizzes() == 1
        result.getAverageQuizzesSolved() == 1f
    }

    def "remove a quiz stat"() {
        given: "a quiz stat"
        def quizStat = createQuizStat()

        when: "the quiz stat is removed"
        quizStat.remove()
        quizStatsRepository.delete(quizStat)

        then: "the quiz stat is removed"
        quizStatsRepository.count() == 0L
        teacherDashboard.getQuizStats().size() == 0
    }

    def "quiz stat string representation"() {
        given: "a quiz stat"
        def quizStat = createQuizStat()

        and: "all fields defined in quiz stat"
        def fields = quizStat.getClass().getDeclaredFields().stream()
            .map(Field::getName)
            .toList()

        when: "quiz stat toString method is called"
        def quizStatString = quizStat.toString()

        then: "the returned string contains data from all fields"
        fields.every {
            quizStatString.contains(it)
        }
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

