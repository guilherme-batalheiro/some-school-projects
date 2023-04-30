import QuizStats from '@/models/statistics/QuizStats';
import QuestionStats from '@/models/statistics/QuestionStats';
import StudentStats from '@/models/statistics/StudentStats';

export default class TeacherDashboard {
  id!: number;
  questionStats: QuestionStats[] = [];
  studentStats: StudentStats[] = [];
  quizStats: QuizStats[] = [];


  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.questionStats = jsonObj.questionStats.map((questionStats: QuestionStats) => new QuestionStats(questionStats));
      this.studentStats = jsonObj.studentStats.map((studentStats: StudentStats) => new StudentStats(studentStats));
      this.quizStats = jsonObj.quizStats.map((quizStats: QuizStats) => new QuizStats(quizStats));
    }
  }
}