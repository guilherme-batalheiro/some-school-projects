export default class StudentStats {
	id!: number;
	numStudents!: number;
	numMore75CorrectQuestions!: number;
	numAtLeast3Quizzes!: number;
	courseExecutionYear!: number;

	constructor(jsonObj?: StudentStats) {
		if (jsonObj) {
			this.id = jsonObj.id;
			this.numStudents = jsonObj.numStudents;
			this.numMore75CorrectQuestions = jsonObj.numMore75CorrectQuestions;
			this.numAtLeast3Quizzes = jsonObj.numAtLeast3Quizzes;
			this.courseExecutionYear = jsonObj.courseExecutionYear;
		}
	}
}