import { ISOtoString } from '@/services/ConvertDateService';

export default class StudentDashboard {
  id!: number;
  lastCheckFailedAnswers!: string;
  lastCheckWeeklyScores!: string;

  constructor(jsonObj?: StudentDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.lastCheckFailedAnswers)
        this.lastCheckFailedAnswers = ISOtoString(
          jsonObj.lastCheckFailedAnswers
        );
      if (jsonObj.lastCheckWeeklyScores)
        this.lastCheckWeeklyScores = ISOtoString(jsonObj.lastCheckWeeklyScores);
    }
  }
}
