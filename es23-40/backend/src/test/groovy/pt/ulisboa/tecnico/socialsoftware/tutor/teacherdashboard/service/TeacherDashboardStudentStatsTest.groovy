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
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto
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
class TeacherDashboardStudentStatsTest extends SpockTest {

    def createCourse(courseName,courseType) {
        def course = new Course(courseName, courseType)
        courseRepository.save(course)
        return course
    }

    def createCourseExecution(Course course, acronym, academicTerm, endDate) {
        def courseExecution = new CourseExecution(course, acronym, academicTerm, course.getType(), endDate)
        courseExecutionRepository.save(courseExecution)
        return courseExecution
    }

    def createAndAddStudent(courseExecution, name, username, email) {
        def student = new Student(name, username, email, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        return student
    }

    def createAndAddTeacher(courseExecution, name, username, email, isAdmin, type) {
        def teacher = new Teacher(name, username,email,isAdmin,type)
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)
        return teacher
    }

    def createQuiz(courseExecution = externalCourseExecution) {
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createQuestion(course) {
        def newQuestion = new Question()
        newQuestion.setTitle("title")
        newQuestion.setCourse(course)
        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)

        def option = new Option()
        option.setContent("1")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        def optionKO = new Option()
        optionKO.setContent("2")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)
        questionDetailsRepository.save(questionDetails)
        optionRepository.save(option)
        optionRepository.save(optionKO)
        return newQuestion
    }

    def createQuizQuestion(course, quiz, sequence) {
        def question = createQuestion(course)
        def quizQuestion = new QuizQuestion(quiz, question, sequence)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def createQuestionAnswer(quizAnswer, quizQuestion, correct) {
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)

        def option
        if(correct) {
            option = quizQuestion.getQuestion().getQuestionDetails().getOptions().get(0)
        }
        else {
            option = quizQuestion.getQuestion().getQuestionDetails().getOptions().get(1)
        }
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, option)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }

    def createQuizAnswer(quiz, student, date = DateHandler.now(), completed = true) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)
        return quizAnswer
    }

    def createAndSolveQuiz(course, courseExecution, student, correct) {
        def quiz = createQuiz(courseExecution)
        def quizQuestion = createQuizQuestion(course, quiz, 0)

        def quizAnswer = createQuizAnswer(quiz, student)
        createQuestionAnswer(quizAnswer, quizQuestion, correct)
    }

    def course1
    def courseExecution11819

    def courseExecution11920
    def courseExecution12021
    def courseExecution12122
    def courseExecution12223

    def teacher1
    def teacher2

    def student1


    def setup() {
        // Create 5 course executions, one of which with an invalid year; create 2 teachers; setup allows a total of 5 dashboards
        // (these 5 dashboards will be updated or created when using the updateAll service)
        course1 = createCourse("Course 1",Course.Type.TECNICO)
        courseExecution11819 = createCourseExecution(course1, "C1", "1 semestre 2018/19", DateHandler.toLocalDateTime("2019-12-31"))

        courseExecution11920 = createCourseExecution(course1, "C1", "1 semestre 2019/20", DateHandler.toLocalDateTime("2020-12-31"))
        student1 = createAndAddStudent(courseExecution11920, "Student 1 19/20", "s11920", "s11920@x.pt")

        // the academic term of the courseExecution12021 purposely misses the year
        courseExecution12021 = createCourseExecution(course1, "C1", "1 semestre", DateHandler.toLocalDateTime("2021-12-31"))
        createAndAddStudent(courseExecution12021, "Student 1 20/21", "s12021", "s12021@x.pt")
        createAndAddStudent(courseExecution12021, "Student 2 20/21", "s22021", "s22021@x.pt")

        courseExecution12122 = createCourseExecution(course1, "C1", "1 semestre 2021/22", DateHandler.toLocalDateTime("2022-12-31"))
        createAndAddStudent(courseExecution12122, "Student 1 21/22", "s12122", "s12122@x.pt")
        createAndAddStudent(courseExecution12122, "Student 2 21/22", "s22122", "s22122@x.pt")
        createAndAddStudent(courseExecution12122, "Student 3 21/22", "s32122", "s32122@x.pt")

        courseExecution12223 = createCourseExecution(course1, "C1", "1 semestre 2022/23", DateHandler.toLocalDateTime("2023-12-31"))
        createAndAddStudent(courseExecution12223, "Student 1 22/23", "s12223", "s12223@x.pt")
        createAndAddStudent(courseExecution12223, "Student 2 22/23", "s22223", "s22223@x.pt")
        createAndAddStudent(courseExecution12223, "Student 3 22/23", "s32223", "s32223@x.pt")
        createAndAddStudent(courseExecution12223, "Student 4 22/23", "s42223", "s42223@x.pt")

        // Teacher 1: 18/19, 19/20 (2 dashboards)
        teacher1 = createAndAddTeacher(courseExecution11819, "Teacher 1", "t1", "t1@x.com", false, AuthUser.Type.TECNICO)
        teacher1.addCourse(courseExecution11920)

        // Teacher 2: 19/20, 20/21, 21/22, and 22/23 (only 3 dashboards, since 20/21 doesn't have year)
        teacher2 = createAndAddTeacher(courseExecution11920, "Teacher 2", "t2", "t2@x.com", false, AuthUser.Type.TECNICO)
        teacher2.addCourse(courseExecution12021)
        teacher2.addCourse(courseExecution12122)
        teacher2.addCourse(courseExecution12223)
    }

    def "get a dashboard for a course execution with no previous executions"() {
        when: "getting the dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution11819.getId(), teacher1.getId())

        then: "a dashboard with only one student stats is returned"
        teacherDashboardDto.getStudentStats().size() == 1

        and: "the stats are correct"
        def stats = teacherDashboardDto.getStudentStats().get(0)
        stats.getNumStudents() == 0
        stats.getNumAtLeast3Quizzes() == 0
        stats.getNumMore75CorrectQuestions() == 0
        stats.getCourseExecutionYear() == 2018
    }

    def "get a dashboard for a course execution with one previous execution"() {
        when: "getting the dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution11920.getId(), teacher1.getId())

        then: "a dashboard with two student stats is returned"
        teacherDashboardDto.getStudentStats().size() == 2

        and: "the stats are correct for the newest (19/20)"
        def stats1 = teacherDashboardDto.getStudentStats().get(0)
        stats1.getNumStudents() == 1
        stats1.getNumAtLeast3Quizzes() == 0
        stats1.getNumMore75CorrectQuestions() == 0
        stats1.getCourseExecutionYear() == 2019

        and: "the stats are correct for the oldest (18/19)"
        def stats2 = teacherDashboardDto.getStudentStats().get(1)
        stats2.getNumStudents() == 0
        stats2.getNumAtLeast3Quizzes() == 0
        stats2.getNumMore75CorrectQuestions() == 0
        stats2.getCourseExecutionYear() == 2018
    }

    def "get a dashboard for a course execution with two previous executions (and a gap)"() {
        when: "getting the dashboard for 22/23"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution12223.getId(), teacher2.getId())

        then: "a dashboard with three student stats is returned"
        teacherDashboardDto.getStudentStats().size() == 3

        and: "the stats are correct for the newest (22/23)"
        def stats1 = teacherDashboardDto.getStudentStats().get(0)
        stats1.getNumStudents() == courseExecution12223.getNumberOfActiveStudents()
        stats1.getNumAtLeast3Quizzes() == 0
        stats1.getNumMore75CorrectQuestions() == 0
        stats1.getCourseExecutionYear() == 2022

        and: "the stats are correct for the previous execution (21/22)"
        def stats2 = teacherDashboardDto.getStudentStats().get(1)
        stats2.getNumStudents() == courseExecution12122.getNumberOfActiveStudents()
        stats2.getNumAtLeast3Quizzes() == 0
        stats2.getNumMore75CorrectQuestions() == 0
        stats2.getCourseExecutionYear() == 2021

        and: "the oldest is the execution 19/20 and the stats are correct"
        def stats3 = teacherDashboardDto.getStudentStats().get(2)
        stats3.getNumStudents() == courseExecution11920.getNumberOfActiveStudents()
        stats3.getNumAtLeast3Quizzes() == 0
        stats3.getNumMore75CorrectQuestions() == 0
        stats3.getCourseExecutionYear() == 2019
    }

    def "get a dashboard for an execution that does not have the year defined"() {
        when: "getting the dashboard for 20/21"
        teacherDashboardService.getTeacherDashboard(courseExecution12021.getId(), teacher2.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION
    }

    def "update the number of students of a dashboard for a course execution with multiple previous course executions"() {
        given: "a dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution12223.getId(), teacher2.getId())

        and: "a new student enters in the course execution"
        createAndAddStudent(courseExecution12223, "Student 5 22/23", "s52223", "s52223@x.pt")

        when: "updating the dashboard for 22/23"
        teacherDashboardService.updateTeacherDashboard(teacherDashboardDto.getId())

        then: "the dashboard is updated with 5 students"
        teacherDashboardRepository.count() == 1L
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getStudentStats().size() == 3

        //stats for course execution in 22/23
        def stats = teacherDashboard.getStudentStats().get(0)
        stats.getNumStudents() == courseExecution12223.getNumberOfActiveStudents()
        stats.getNumAtLeast3Quizzes() == 0
        stats.getNumMore75CorrectQuestions() == 0
    }

    def "update the number of students that solved at least 3 quizzes in a dashboard for a course execution with one previous course execution"() {
        given: "a dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution11920.getId(), teacher1.getId())

        and: "a student solves 3 quizzes"
        createAndSolveQuiz(course1, courseExecution11920, student1, false)
        createAndSolveQuiz(course1, courseExecution11920, student1, false)
        createAndSolveQuiz(course1, courseExecution11920, student1, false)

        when: "updating the dashboard for 19/20"
        teacherDashboardService.updateTeacherDashboard(teacherDashboardDto.getId())

        then: "the dashboard is updated with 1 student having solved at least 3 quizzes"
        teacherDashboardRepository.count() == 1L
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getStudentStats().size() == 2

        //stats for course execution in 19/20
        def stats = teacherDashboard.getStudentStats().get(0)
        stats.getNumStudents() == courseExecution11920.getNumberOfActiveStudents()
        stats.getNumAtLeast3Quizzes() == 1
        stats.getNumMore75CorrectQuestions() == 0
    }

    def "update the number of students that have more than 75% correct questions in a dashboard for a course execution with no previous executions"() {
        given: "a dashboard"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution11819.getId(), teacher1.getId())

        and: "a student is added to the course execution and solves a quiz with all correct answers"
        def student0 = createAndAddStudent(courseExecution11819, "Student 0 18/19", "s11819", "s11819@x.pt")
        createAndSolveQuiz(course1, courseExecution11819, student0, true)

        when: "updating the dashboard for 18/19"
        teacherDashboardService.updateTeacherDashboard(teacherDashboardDto.getId())

        then: "the dashboard is updated with 1 student having more than 75% correct questions"
        teacherDashboardRepository.count() == 1L
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getStudentStats().size() == 1

        //stats for course execution in 18/19
        def stats = teacherDashboard.getStudentStats().get(0)
        stats.getNumStudents() == courseExecution11819.getNumberOfActiveStudents()
        stats.getNumAtLeast3Quizzes() == 0
        stats.getNumMore75CorrectQuestions() == 1
    }

    def "update all student statistics of multiple dashboards"() {
        given: "two dashboards"
        TeacherDashboardDto dashboardDto1 = teacherDashboardService.getTeacherDashboard(courseExecution11819.getId(), teacher1.getId())
        TeacherDashboardDto dashboardDto2 = teacherDashboardService.getTeacherDashboard(courseExecution11920.getId(), teacher1.getId())

        and: "a student is added to the course execution 18/19 and solves 3 quizzes with all correct answers"
        def student0 = createAndAddStudent(courseExecution11819, "Student 1 18/19", "s11819", "s11819@x.pt")
        createAndSolveQuiz(course1, courseExecution11819, student0, true)
        createAndSolveQuiz(course1, courseExecution11819, student0, true)
        createAndSolveQuiz(course1, courseExecution11819, student0, true)

        and: "two students are added to the course execution 19/20 and solve 3 quizzes with all correct answers"
        def student1 = createAndAddStudent(courseExecution11920, "Student 2 19/20", "s21920", "s21920@x.pt")
        createAndSolveQuiz(course1, courseExecution11920, student1, true)
        createAndSolveQuiz(course1, courseExecution11920, student1, true)
        createAndSolveQuiz(course1, courseExecution11920, student1, true)

        def student2 = createAndAddStudent(courseExecution11920, "Student 3 19/20", "s31920", "s31920@x.pt")
        createAndSolveQuiz(course1, courseExecution11920, student2, true)
        createAndSolveQuiz(course1, courseExecution11920, student2, true)
        createAndSolveQuiz(course1, courseExecution11920, student2, true)


        when: "updating all dashboards"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "both dashboards are updated"
        def teacherDashboard1819 = teacherDashboardService.getTeacherDashboard(courseExecution11819.getId(), teacher1.getId())
        def teacherDashboard1920 = teacherDashboardService.getTeacherDashboard(courseExecution11920.getId(), teacher1.getId())
        teacherDashboard1819.getStudentStats().size() == 1
        teacherDashboard1920.getStudentStats().size() == 2

        and: "stats stored in teacherDashboard 18/19 for course execution in 18/19 were updated"
        dashboardDto1.getStudentStats().get(0).getNumStudents() == 0
        dashboardDto1.getStudentStats().get(0).getNumAtLeast3Quizzes() == 0
        dashboardDto1.getStudentStats().get(0).getNumMore75CorrectQuestions() == 0
        // new values
        def stats1819 = teacherDashboard1819.getStudentStats().get(0)
        stats1819.getNumStudents() == courseExecution11819.getNumberOfActiveStudents()
        stats1819.getNumAtLeast3Quizzes() == 1
        stats1819.getNumMore75CorrectQuestions() == 1

        and: "stats stored in teacherDashboard 19/20 for course execution in 19/20 were updated"
        dashboardDto2.getStudentStats().get(0).getNumStudents() == 1
        dashboardDto2.getStudentStats().get(0).getNumAtLeast3Quizzes() == 0
        dashboardDto2.getStudentStats().get(0).getNumMore75CorrectQuestions() == 0
        // new values
        def stats1920 = teacherDashboard1920.getStudentStats().get(0)
        stats1920.getNumStudents() == courseExecution11920.getNumberOfActiveStudents()
        stats1920.getNumAtLeast3Quizzes() == 2
        stats1920.getNumMore75CorrectQuestions() == 2

        and: "all possible 5 dashboards are created"
        teacherDashboardRepository.count() == 5L
    }

    def "update the number of students for a course execution with a new more recent course execution"() {
        given: "a new course execution in years 2024/25"
        def courseExecution12425 = createCourseExecution(course1, "C1", "1 semestre 2024/25", DateHandler.toLocalDateTime("2025-12-31"))
        teacher2.addCourse(courseExecution12425)
        
        and: "a dashboard for course execution 2024/25"
        def teacherDashboardDto = teacherDashboardService.getTeacherDashboard(courseExecution12425.getId(), teacher2.getId())

        and: "a new course execution in 2023/24 created after the dashboard for 2024/25 is created"
        def courseExecution12324 = createCourseExecution(course1, "C1", "1 semestre 2023/24", DateHandler.toLocalDateTime("2024-12-31"))
        teacher2.addCourse(courseExecution12324)
        createAndAddStudent(courseExecution12324, "Student 1 23/24", "s12324", "s12324@x.pt")

        when: "updating the dashboard for year 2024/25"
        teacherDashboardService.updateTeacherDashboard(teacherDashboardDto.getId())

        then: "the dashboard is updated with the 2023/24 course execution stats"
        teacherDashboardRepository.count() == 1L
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getStudentStats().size() == 3

        //stats for course execution in 24/25
        def stats2425 = teacherDashboard.getStudentStats().get(0)
        stats2425.getNumStudents() == courseExecution12425.getNumberOfActiveStudents()
        stats2425.getNumAtLeast3Quizzes() == 0
        stats2425.getNumMore75CorrectQuestions() == 0

        //stats for course execution in 23/24
        def stats2324 = teacherDashboard.getStudentStats().get(1)
        stats2324.getNumStudents() == courseExecution12324.getNumberOfActiveStudents()
        stats2324.getNumAtLeast3Quizzes() == 0
        stats2324.getNumMore75CorrectQuestions() == 0

        //stats for course execution in 22/23
        def stats2223 = teacherDashboard.getStudentStats().get(2)
        stats2223.getNumStudents() == courseExecution12223.getNumberOfActiveStudents()
        stats2223.getNumAtLeast3Quizzes() == 0
        stats2223.getNumMore75CorrectQuestions() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}