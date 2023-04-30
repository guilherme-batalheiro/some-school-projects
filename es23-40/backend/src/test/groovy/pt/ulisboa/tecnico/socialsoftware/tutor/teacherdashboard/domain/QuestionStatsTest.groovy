package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import spock.lang.Unroll;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthDemoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class QuestionStatsTest extends SpockTest {

    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def createQuiz () {
        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
    }

    def createQuizQuestion(quiz, question, seq) {
        def quizQuestion = new QuizQuestion(quiz, question, seq)
        quizQuestionRepository.save(quizQuestion)
    } 

    def createQuestionStats() {
        def questionStats = new QuestionStats(teacherDashboard, externalCourseExecution)
        questionStatsRepository.save(questionStats)
        return questionStats
    }

    def createStudent(name, user, mail) {
        def student = new Student(name, user, mail, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
    }

    def createQuestion(key, available=true) {
        def question = new Question()
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        if(available == true){
            question.setStatus(Question.Status.AVAILABLE)
        }
        else{
            question.setStatus(Question.Status.SUBMITTED)
        }
        question.setKey(key)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionRepository.save(question)

        def option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        def optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        return question;
    }

    def createQuizAnswer (user, quiz, date = DateHandler.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)
    }

    def createQuestionAnswer (quizQuestion, quizAnswer, sequence, correct, answered = true) {
        def questionAnswer = new QuestionAnswer ()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setSequence(sequence)
        questionAnswerRepository.save(questionAnswer)

        def answerDetails
        def correctOption = quizQuestion.getQuestion().getQuestionDetails().getCorrectOption()
        def incorrectOption = quizQuestion.getQuestion().getQuestionDetails().getOptions().stream().filter(option -> option != correctOption).findAny().orElse(null)
        if (answered && correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, correctOption)
        else if (answered && !correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, incorrectOption)
        else {
            questionAnswerRepository.save(questionAnswer)
            return questionAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }

    def "create an empty question stats"() {
        when: "a question stat is created"
        def questionStat = createQuestionStats()

        then: "a question stat is persisted"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        
        and: "a question stat is returned"
        result.getNumAvailable() == 0
        result.getAnsweredQuestionsUnique() == 0
        result.getAverageQuestionsAnswered() == 0.0f

        and: "the dashboard has a reference for the question stat"
        teacherDashboard.getQuestionStats().size() == 1
        teacherDashboard.getQuestionStats().contains(result)
    }

    def "update a question stats without students"() {
        given: "a question stat"
        def questionStat = createQuestionStats()

        when: "a question stat is updated"
        questionStat.update()

        then: "a question stat is persisted"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        
        and: "a question stat is returned"
        result.getNumAvailable() == 0
        result.getAnsweredQuestionsUnique() == 0
        result.getAverageQuestionsAnswered() == 0.0f

        and: "the dashboard has a reference for the question stat"
        teacherDashboard.getQuestionStats().size() == 1
        teacherDashboard.getQuestionStats().contains(result)
    }

    def "update a question stats with 3 available questions and 2 students"() {
        given: "two students"
        def s1 = createStudent(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL) 
        def s2 = createStudent(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)

        and: "three questions"
        def q1 = createQuestion(1)
        def q2 = createQuestion(2)
        def q3 = createQuestion(3)

        and: "a submitted question"
        def q4 = createQuestion(4, false)
        
        and: "a quiz"
        def quiz = createQuiz()
        def qq1 = createQuizQuestion(quiz, q1, 0)
        def qq2 = createQuizQuestion(quiz, q2, 1)
        def qq3 = createQuizQuestion(quiz, q3, 2)

        and: "question stats"
        def stats = createQuestionStats()

        and: "students answer questions"
        def quizAs1 = createQuizAnswer(s1, quiz)        
        def quizAs2 = createQuizAnswer(s2, quiz)
        createQuestionAnswer(qq1, quizAs1, 0, true)
        createQuestionAnswer(qq2, quizAs1, 1, false)
        createQuestionAnswer(qq2, quizAs2, 0, true)

        when: "the stats are updates"
        stats.update()

        then: "the stats are correct"
        stats.getAnsweredQuestionsUnique() == 2
        stats.getAverageQuestionsAnswered() == (float)3/2
        stats.getNumAvailable() == 3
    }

    def "generate a string of question stats after update with 3 available questions and 2 students"() {
        given: "two students"
        def s1 = createStudent(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL) 
        def s2 = createStudent(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)

        and: "three questions"
        def q1 = createQuestion(1)
        def q2 = createQuestion(2)
        def q3 = createQuestion(3)

        and: "a submitted question"
        def q4 = createQuestion(4, false)
        
        and: "a quiz"
        def quiz = createQuiz()
        def qq1 = createQuizQuestion(quiz, q1, 0)
        def qq2 = createQuizQuestion(quiz, q2, 1)
        def qq3 = createQuizQuestion(quiz, q3, 2)

        and: "question stats"
        def stats = createQuestionStats()

        and: "students answer questions"
        def quizAs1 = createQuizAnswer(s1, quiz)        
        def quizAs2 = createQuizAnswer(s2, quiz)
        createQuestionAnswer(qq1, quizAs1, 0, true)
        createQuestionAnswer(qq2, quizAs1, 1, false)
        createQuestionAnswer(qq2, quizAs2, 0, true)

        when: "the stats are updates"
        stats.update()
        def res = stats.toString()

        then: "the stats are correct"

        res.equals ("QuestionStats{" +
            "id=" + stats.getId() +
            ", teacherDashboard=" + teacherDashboard.getId() +
            ", courseExecution=" + externalCourseExecution.getId() +
            ", numAvailable=" + 3 +
            ", answeredQuestionsUnique=" + 2 +
            ", averageQuestionsAnswered=" + (float) 3/2 +
            '}');
    }

    def "remove a question stat"() {
        given: "a question stat"
        def stats = createQuestionStats()

        when: "the question stat is removed"
        stats.remove()
        questionStatsRepository.delete(stats)

        then: "the question stat is removed"
        questionStatsRepository.count() == 0L
        teacherDashboard.getQuestionStats().size() == 0
    }

    def "update with 3 available questions and 2 students"() {
        given: "two students"
        def s1 = createStudent(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL) 
        def s2 = createStudent(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)

        and: "three questions"
        def q1 = createQuestion(1)
        def q2 = createQuestion(2)
        def q3 = createQuestion(3)

        and: "a submitted question"
        def q4 = createQuestion(4, false)
        
        and: "a quiz"
        def quiz = createQuiz()
        def qq1 = createQuizQuestion(quiz, q1, 0)
        def qq2 = createQuizQuestion(quiz, q2, 1)
        def qq3 = createQuizQuestion(quiz, q3, 2)

        and: "question stats"
        def stats = createQuestionStats()

        and: "students answer questions"
        def quizAs1 = createQuizAnswer(s1, quiz)        
        def quizAs2 = createQuizAnswer(s2, quiz)
        createQuestionAnswer(qq1, quizAs1, 0, true)
        createQuestionAnswer(qq2, quizAs1, 1, false)
        createQuestionAnswer(qq2, quizAs2, 0, true)

        when: "the dashboard is updated"
        teacherDashboard.update()

        then: "the stats are correct"
        def statsLst = teacherDashboard.getQuestionStats()
        statsLst.size() == 1
        def stats2 = statsLst.get(0)
        stats2.getAnsweredQuestionsUnique() == 2
        stats2.getAverageQuestionsAnswered() == (float)3/2
        stats2.getNumAvailable() == 3
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
