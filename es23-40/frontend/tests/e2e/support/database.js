function dbPasswordCommand(password) {
  if (Cypress.platform === 'win32') {
    return `set PGPASSWORD=${password}&& `;
  } else {
    return `PGPASSWORD=${password} `;
  }
}

function dbCommand(command) {
  return cy.exec(
    dbPasswordCommand(Cypress.env('psql_db_password')) +
      `psql -d ${Cypress.env('psql_db_name')} ` +
      `-U ${Cypress.env('psql_db_username')} ` +
      `-h ${Cypress.env('psql_db_host')} ` +
      `-p ${Cypress.env('psql_db_port')} ` +
      `-c "${command.replace(/\r?\n/g, ' ')}"`
  );
}

Cypress.Commands.add('beforeEachTournament', () => {
  dbCommand(`
      WITH tmpCourse as (SELECT ce.course_id, ce.id as course_execution_id FROM courses c JOIN course_executions ce on ce.course_id = c.id WHERE name = 'Demo Course')      
        ,insert1 as (INSERT INTO assessments (id, sequence, status, title, course_execution_id) VALUES (1, 0, 'AVAILABLE', 'test1', (select course_execution_id from tmpCourse)))
        ,insert2 as (INSERT INTO assessments (id, sequence, status, title, course_execution_id) VALUES (2, 0, 'AVAILABLE', 'test2', (select course_execution_id from tmpCourse)))
        ,insert3 as (INSERT INTO topic_conjunctions (id, assessment_id) VALUES (100, 1))
        ,insert4 as (INSERT INTO topic_conjunctions (id, assessment_id) VALUES (101, 2))
        ,insert5 as (INSERT INTO topics (id, name, course_id) VALUES (82, 'Software Architecture', (select course_id from tmpCourse)))
        ,insert6 as (INSERT INTO topics (id, name, course_id) VALUES (83, 'Web Application', (select course_id from tmpCourse)))
        ,insert7 as (INSERT INTO topics_topic_conjunctions (topics_id, topic_conjunctions_id) VALUES (82, 100))
        ,insert8 as (INSERT INTO topics_topic_conjunctions (topics_id, topic_conjunctions_id) VALUES (83, 101))
        ,insert9 as (INSERT INTO questions (id, title, content, status, course_id, creation_date) VALUES (1389, 'test', 'Question?', 'AVAILABLE', (select course_id from tmpCourse), current_timestamp))
        ,insert10 as (INSERT INTO question_details (id, question_type, question_id) VALUES (1000, 'multiple_choice', 1389))
        INSERT INTO topics_questions (topics_id, questions_id) VALUES (82, 1389);
    `);

  for (let content in [0, 1, 2, 3]) {
    let correct = content === '0' ? 't' : 'f';
    dbCommand(`
      INSERT INTO options(content, correct, question_details_id, sequence) VALUES ('${content}', '${correct}', 1000, ${content});
      `);
  }
});

Cypress.Commands.add('cleanTestTopics', () => {
  dbCommand(`
        DELETE FROM topics
        WHERE name like 'CY%'
    `);
});

Cypress.Commands.add('cleanTestCourses', () => {
  dbCommand(`
    delete from users_course_executions where course_executions_id in (select id from course_executions where acronym like 'TEST-%');
    delete from course_executions where acronym like 'TEST-%';
    `);
});

Cypress.Commands.add('updateTournamentStartTime', () => {
  dbCommand(`
        UPDATE tournaments SET start_time = '2020-07-16 07:57:00';
    `);
});

Cypress.Commands.add('afterEachTournament', () => {
  dbCommand(`
         DELETE FROM tournaments_topics WHERE topics_id = 82;
         DELETE FROM tournaments_topics WHERE topics_id = 83;
         DELETE FROM topics_topic_conjunctions WHERE topics_id = 82;
         DELETE FROM topics_topic_conjunctions WHERE topics_id = 83;
         DELETE FROM topic_conjunctions WHERE id = 100;
         DELETE FROM topic_conjunctions WHERE id = 101;
         DELETE FROM assessments WHERE id = 1;
         DELETE FROM assessments WHERE id = 2;
         DELETE FROM topics_questions WHERE questions_id = 1389;
         DELETE FROM topics WHERE id = 82;
         DELETE FROM topics WHERE id = 83;
         DELETE FROM question_answers USING quiz_questions WHERE quiz_questions.id = question_answers.quiz_question_id AND quiz_questions.question_id = 1389;
         DELETE FROM quiz_questions WHERE question_id = 1389;
         DELETE FROM options WHERE question_details_id = 1000;
         DELETE FROM question_details WHERE id = 1000;
         DELETE FROM questions WHERE id = 1389;
         DELETE FROM tournaments_participants;
         DELETE FROM tournaments; 
         ALTER SEQUENCE tournaments_id_seq RESTART WITH 1;
         UPDATE tournaments SET id=nextval('tournaments_id_seq');
    `);
});

Cypress.Commands.add('addQuestionSubmission', (title, submissionStatus) => {
  dbCommand(`
    WITH course as (SELECT ce.course_id as course_id, ce.id as course_execution_id FROM courses c JOIN course_executions ce on ce.course_id = c.id WHERE name = 'Demo Course')     
    , quest AS (
      INSERT INTO questions (title, content, status, course_id, creation_date) 
      VALUES ('${title}', 'Question?', 'SUBMITTED', (select course_id from course), current_timestamp) RETURNING id
      )
    INSERT INTO question_submissions (status, question_id, submitter_id, course_execution_id) 
    VALUES ('${submissionStatus}', (SELECT id from quest), (select id from users where name = 'Demo Student'), (select course_execution_id from course));`);

  //add options
  for (let content in [0, 1, 2, 3]) {
    let correct = content === '0' ? 't' : 'f';
    dbCommand(
      `WITH quest AS (SELECT id FROM questions WHERE title='${title}'),
      quest_details as (INSERT INTO question_details (question_type, question_id) VALUES ('multiple_choice', (SELECT id FROM quest)) RETURNING id)
      INSERT INTO options(content, correct, question_details_id, sequence) 
      VALUES ('${content}', '${correct}', (SELECT id FROM quest_details), ${content});`
    );
  }
});

Cypress.Commands.add('removeQuestionSubmission', (hasReviews = false) => {
  if (hasReviews) {
    dbCommand(`WITH rev AS (DELETE FROM reviews WHERE id IN (SELECT max(id) FROM reviews) RETURNING question_submission_id)
                      , sub AS (DELETE FROM question_submissions WHERE id IN (SELECT * FROM rev) RETURNING question_id) 
                      , opt AS (DELETE FROM options WHERE question_details_id IN (SELECT qd.id FROM sub JOIN question_details qd on qd.question_id = sub.question_id)) 
                      , det AS (DELETE FROM question_details WHERE question_id in (SELECT * FROM sub))
                        DELETE FROM questions WHERE id IN (SELECT * FROM sub);`);
  } else {
    dbCommand(`WITH sub AS (DELETE FROM question_submissions WHERE id IN (SELECT max(id) FROM question_submissions) RETURNING question_id)
                      , opt AS (DELETE FROM options WHERE question_details_id IN (SELECT qd.id FROM sub JOIN question_details qd on qd.question_id = sub.question_id)) 
                      , det AS (DELETE FROM question_details WHERE question_id in (SELECT * FROM sub))
                    DELETE FROM questions WHERE id IN (SELECT * FROM sub);`);
  }
});

Cypress.Commands.add('cleanMultipleChoiceQuestionsByName', (questionName) => {
  dbCommand(`WITH toDelete AS (SELECT qt.id as question_id FROM questions qt JOIN question_details qd ON qd.question_id = qt.id and qd.question_type='multiple_choice' where title like '%${questionName}%')
                  , opt AS (DELETE FROM options WHERE question_details_id IN (SELECT qd.id FROM toDelete JOIN question_details qd on qd.question_id = toDelete.question_id)) 
                  , det AS (DELETE FROM question_details WHERE question_id in (SELECT question_id FROM toDelete))
                DELETE FROM questions WHERE id IN (SELECT question_id FROM toDelete);`);
});

Cypress.Commands.add('cleanCodeFillInQuestionsByName', (questionName) => {
  dbCommand(`WITH toDelete AS (SELECT qt.id as question_id FROM questions qt JOIN question_details qd ON qd.question_id = qt.id and qd.question_type='code_fill_in' where title like '%${questionName}%')
                , fillToDelete AS (SELECT id FROM  code_fill_in_spot WHERE question_details_id IN (SELECT qd.id FROM toDelete JOIN question_details qd on qd.question_id = toDelete.question_id))
                , opt AS (DELETE FROM  code_fill_in_options WHERE code_fill_in_id IN (SELECT id FROM fillToDelete))
                , fill AS (DELETE FROM  code_fill_in_spot WHERE id IN (SELECT id FROM fillToDelete)) 
                , det AS (DELETE FROM question_details WHERE question_id in (SELECT question_id FROM toDelete))
              DELETE FROM questions WHERE id IN (SELECT question_id FROM toDelete);`);
});

Cypress.Commands.add('createWeeklyScore', () => {
  dbCommand(`WITH courseExecutionId as (SELECT ce.id as course_execution_id FROM course_executions ce WHERE acronym = 'DemoCourse')
        , demoStudentId as (SELECT u.id as users_id FROM users u WHERE name = 'Demo Student')
        , dashboardId as (SELECT d.id as student_dashboard_id FROM student_dashboard d WHERE student_id = (select users_id from demoStudentId) AND course_execution_id = (select course_execution_id from courseExecutionId))
       INSERT INTO weekly_score(closed, quizzes_answered, questions_answered, questions_uniquely_answered, percentage_correct, improved_correct_answers, week, student_dashboard_id) VALUES (true, 3, 10, 50, 9, 8, '2022-02-02', (select student_dashboard_id from dashboardId))
      `);
});

Cypress.Commands.add('deleteWeeklyScores', () => {
  dbCommand(`
         UPDATE student_dashboard SET last_check_weekly_scores = NULL;
         DELETE FROM weekly_score;
    `);
});

Cypress.Commands.add('deleteFailedAnswers', () => {
  dbCommand(`
         UPDATE student_dashboard SET last_check_failed_answers = NULL;
         DELETE FROM failed_answer;
    `);
});

Cypress.Commands.add('addTopicAndAssessment', () => {
  dbCommand(`
      WITH tmpCourse as (SELECT ce.course_id, ce.id as course_execution_id FROM courses c JOIN course_executions ce on ce.course_id = c.id WHERE name = 'Demo Course')      
        ,insert1 as (INSERT INTO assessments (id, sequence, status, title, course_execution_id) VALUES (1, 0, 'AVAILABLE', 'assessment one', (select course_execution_id from tmpCourse)))
        ,insert2 as (INSERT INTO topic_conjunctions (id, assessment_id) VALUES (100, 1))
        ,insert3 as (INSERT INTO topics (id, name, course_id) VALUES (82, 'Software Architecture', (select course_id from tmpCourse)))
        INSERT INTO topics_topic_conjunctions (topics_id, topic_conjunctions_id) VALUES (82, 100);
    `);
});

Cypress.Commands.add('deleteDifficultQuestions', () => {
  dbCommand(`
         DELETE FROM difficult_question;
    `);
});

Cypress.Commands.add('deleteQuestionsAndAnswers', () => {
  dbCommand(`
         DELETE FROM replies;
         DELETE FROM discussions;
         DELETE FROM answer_details;
         DELETE FROM question_answers;
         DELETE FROM quiz_answers;
         DELETE FROM quiz_questions;
         DELETE FROM quizzes;
         DELETE FROM topics_topic_conjunctions;
         DELETE FROM topic_conjunctions;
         DELETE FROM topics_questions;
         DELETE FROM assessments;
         DELETE FROM options;
         DELETE FROM question_details;
         DELETE FROM questions;
         DELETE FROM topics;
    `);
});

const credentials = {
  user: Cypress.env('psql_db_username'),
  host: Cypress.env('psql_db_host'),
  database: Cypress.env('psql_db_name'),
  password: Cypress.env('psql_db_password'),
  port: Cypress.env('psql_db_port'),
};

Cypress.Commands.add('getDemoCourseExecutionId', () => {
  cy.task('queryDatabase', {
    query: "SELECT id FROM course_executions WHERE acronym = 'DemoCourse'",
    credentials: credentials,
  });
});


// Course execution

Cypress.Commands.add('createCourseExecutionOnDemoCourse', (academicTerm) => {
  cy.task('queryDatabase', {
    query: `INSERT into course_executions (id, academic_term, acronym, end_date, status, type, course_id)
            SELECT id+1, '${academicTerm}', acronym, end_date, status, type, course_id FROM course_executions
              WHERE acronym = 'DemoCourse'
              AND id=(
                SELECT max(id) FROM course_executions
              )`,
    credentials: credentials,
  });
});

Cypress.Commands.add(
  'changeDemoTeacherCourseExecutionMatchingAcademicTerm',
  (academicTerm) => {
    cy.task('queryDatabase', {
      query: `UPDATE users_course_executions
            SET course_executions_id=(SELECT id from course_executions WHERE academic_term = '${academicTerm}')
            WHERE users_id=(SELECT id FROM users WHERE name='Demo Teacher')`,
      credentials: credentials,
    });
  }
);

// Teacher dashboard


Cypress.Commands.add('createTeacherDashboard', (id,academicTerm) => {
  cy.task('queryDatabase', {
    query: `INSERT into teacher_dashboard (id, course_execution_id, teacher_id)
            VALUES('${id}',
            (SELECT id FROM course_executions WHERE academic_term = '${academicTerm}'),
            (SELECT id FROM users WHERE name='Demo Teacher'))`,
    credentials: credentials,
  });
});

// Create and insert values on student stats 

Cypress.Commands.add('insertStudentStats', (id,academicTerm,numStudents,numMore75correct,numAtLeast3Quizzes,dashboardId) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO student_stats(id, num_at_least3quizzes, num_more75correct_questions, num_students,
              course_execution_id, teacher_dashboard_id)
            VALUES ('${id}', '${numAtLeast3Quizzes}', '${numMore75correct}', '${numStudents}',
              (SELECT id FROM course_executions WHERE academic_term = '${academicTerm}'),
              '${dashboardId}')`,

    credentials: credentials,
  });
});

Cypress.Commands.add('deleteStats', () => {
  dbCommand(`
        DELETE FROM quiz_stats;
        DELETE FROM question_stats;
        DELETE FROM student_stats;
        DELETE FROM teacher_dashboard;
        DELETE FROM course_executions WHERE id <> 1;
    `);
});

Cypress.Commands.add('insertQuestionStats', (id, academicTerm, numAvailable, answeredQuestionsUnique, averageQuestionsAnswered, dashboardId) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO question_stats(id, num_available, answered_questions_unique, average_questions_answered, execution_id, dashboard_id)
            VALUES ('${id}', '${numAvailable}', '${answeredQuestionsUnique}', '${averageQuestionsAnswered}',
              (SELECT id FROM course_executions WHERE academic_term = '${academicTerm}'),
              '${dashboardId}')`,

    credentials: credentials,
  });
});

Cypress.Commands.add('insertQuizStats', (id,academicTerm,numQuizzes,numUniqueAnsweredQuizzes
    ,averageQuizzesSolved,dashboardId) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO quiz_stats(id, average_quizzes_solved, num_quizzes, num_unique_answered_quizzes,
              course_execution_id, teacher_dashboard_id)
            VALUES ('${id}', '${averageQuizzesSolved}', '${numQuizzes}', '${numUniqueAnsweredQuizzes}',
              (SELECT id FROM course_executions WHERE academic_term = '${academicTerm}'),
              '${dashboardId}')`,

    credentials: credentials,
  });
});