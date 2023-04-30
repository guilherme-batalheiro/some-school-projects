export default class QuizStats {

    id!: number;
    numQuizzes!: number;
    numUniqueAnsweredQuizzes!: number;
    averageQuizzesSolved!: number;
    courseExecutionYear!: number;
    constructor(jsonObj?: QuizStats) {
        if (jsonObj) {
            this.id = jsonObj.id;
            this.numQuizzes = jsonObj.numQuizzes;
            this.numUniqueAnsweredQuizzes = jsonObj.numUniqueAnsweredQuizzes;
            this.averageQuizzesSolved = jsonObj.averageQuizzesSolved;
            this.courseExecutionYear = jsonObj.courseExecutionYear;
        }
    }
}