describe('Dashboard', () => {
	let date;

	// 2023 Values
	const STUDENT_STATS_2023 = ["7", "8", "9"]
	const QUIZ_STATS_2023 = ["9", "7", "4"]
	const QUESTION_STATS_2023 = ["23", "13", "8"]

	// 2022 Values
	const STUDENT_STATS_2022 = ["6", "5", "4"]
	const QUIZ_STATS_2022 = ["2", "1", "8"]
	const QUESTION_STATS_2022 = ["15", "9", "5"]

	// 2019 Values
	const STUDENT_STATS_2019 = ["3", "2", "1"]
	const QUIZ_STATS_2019 = ["9", "3", "2"]
	const QUESTION_STATS_2019 = ["8", "5", "2"]
  
	beforeEach(() => {
	  cy.deleteStats();

	  cy.request('http://localhost:8080/auth/demo/teacher')
		.as('loginResponse')
		.then((response) => {
		  Cypress.env('token', response.body.token);
		  return response;
		});
  
	  date = new Date();
	  //create quiz
	  cy.demoTeacherLogin();
  
	  cy.createCourseExecutionOnDemoCourse("1st semester 2022/2023");
	  cy.createCourseExecutionOnDemoCourse("1st semester 2019/2020");
	 
	  cy.createTeacherDashboard("1", "1st semester 2023/2024");
	  cy.createTeacherDashboard("2", "1st semester 2022/2023");
	  cy.createTeacherDashboard("3", "1st semester 2019/2020");

	  // 2023 Dashboard
	  cy.insertStudentStats("1", "1st semester 2023/2024", STUDENT_STATS_2023[0], STUDENT_STATS_2023[1], STUDENT_STATS_2023[2], "1");
	  cy.insertStudentStats("2", "1st semester 2022/2023", STUDENT_STATS_2022[0], STUDENT_STATS_2022[1], STUDENT_STATS_2022[2], "1");
	  cy.insertStudentStats("3", "1st semester 2019/2020", STUDENT_STATS_2019[0], STUDENT_STATS_2019[1], STUDENT_STATS_2019[2], "1");

	  cy.insertQuizStats("1", "1st semester 2023/2024", QUIZ_STATS_2023[0], QUIZ_STATS_2023[1], QUIZ_STATS_2023[2], "1");
	  cy.insertQuizStats("2", "1st semester 2022/2023", QUIZ_STATS_2022[0], QUIZ_STATS_2022[1], QUIZ_STATS_2022[2], "1");
	  cy.insertQuizStats("3", "1st semester 2019/2020", QUIZ_STATS_2019[0], QUIZ_STATS_2019[1], QUIZ_STATS_2019[2], "1");

	  cy.insertQuestionStats("1", "1st semester 2023/2024", QUESTION_STATS_2023[0], QUESTION_STATS_2023[1], QUESTION_STATS_2023[2], "1");
	  cy.insertQuestionStats("2", "1st semester 2022/2023", QUESTION_STATS_2022[0], QUESTION_STATS_2022[1], QUESTION_STATS_2022[2], "1");
	  cy.insertQuestionStats("3", "1st semester 2019/2020", QUESTION_STATS_2019[0], QUESTION_STATS_2019[1], QUESTION_STATS_2019[2], "1");

	  // 2022 Dashboard
	  cy.insertStudentStats("4", "1st semester 2022/2023", STUDENT_STATS_2022[0], STUDENT_STATS_2022[1], STUDENT_STATS_2022[2], "2");
	  cy.insertStudentStats("5", "1st semester 2019/2020", STUDENT_STATS_2019[0], STUDENT_STATS_2019[1], STUDENT_STATS_2019[2], "2");

	  cy.insertQuizStats("4", "1st semester 2022/2023", QUIZ_STATS_2022[0], QUIZ_STATS_2022[1], QUIZ_STATS_2022[2], "2");
	  cy.insertQuizStats("5", "1st semester 2019/2020", QUIZ_STATS_2019[0], QUIZ_STATS_2019[1], QUIZ_STATS_2019[2], "2");

	  cy.insertQuestionStats("4", "1st semester 2022/2023", QUESTION_STATS_2022[0], QUESTION_STATS_2022[1], QUESTION_STATS_2022[2], "2");
	  cy.insertQuestionStats("5", "1st semester 2019/2020", QUESTION_STATS_2019[0], QUESTION_STATS_2019[1], QUESTION_STATS_2019[2], "2");


	  // 2019 Dashboard
	  cy.insertStudentStats("6", "1st semester 2019/2020", STUDENT_STATS_2019[0], STUDENT_STATS_2019[1], STUDENT_STATS_2019[2], "3");
	  cy.insertQuizStats("6", "1st semester 2019/2020", QUIZ_STATS_2019[0], QUIZ_STATS_2019[1], QUIZ_STATS_2019[2], "3");
	  cy.insertQuestionStats("6", "1st semester 2019/2020", QUESTION_STATS_2019[0], QUESTION_STATS_2019[1], QUESTION_STATS_2019[2], "3");


	  cy.contains('Logout').click();
	});
  
	it('teacher accesses 2023 dashboard', () => {
	  cy.intercept('GET', '**/teachers/dashboards/executions/*').as(
		'getDashboard'
	  );

	  cy.changeDemoTeacherCourseExecutionMatchingAcademicTerm("1st semester 2023/2024");
  
	  cy.demoTeacherLogin();
	  cy.get('[data-cy="dashboardMenuButton"]').click();
	  cy.wait('@getDashboard');

	  cy.get('.bar-chart').eq(0).scrollIntoView().wait(5000).screenshot("students2023", {overwrite: true});
	  cy.get('.bar-chart').eq(1).scrollIntoView().wait(5000).screenshot("quizzes2023", {overwrite: true});
	  cy.get('.bar-chart').eq(2).scrollIntoView().wait(5000).screenshot("questions2023", {overwrite: true});

	  /// Comparing screenshots ///
	
	  cy.compareScreenshots("studentChart2023", "students2023");
	  cy.compareScreenshots("quizChart2023", "quizzes2023");
	  cy.compareScreenshots("questionChart2023", "questions2023");

	  /////////////////////////////


	  cy.checkStudentStats(STUDENT_STATS_2023[0], STUDENT_STATS_2023[1], STUDENT_STATS_2023[2]);
	  cy.checkQuizStats(QUIZ_STATS_2023[0], QUIZ_STATS_2023[1], QUIZ_STATS_2023[2]);
	  cy.checkQuestionStats(QUESTION_STATS_2023[0], QUESTION_STATS_2023[1], QUESTION_STATS_2023[2]);
  
	  cy.contains('Logout').click();
  
	  Cypress.on('uncaught:exception', (err, runnable) => {
		// returning false here prevents Cypress from
		// failing the test
		return false;
	  });
	});

	it('teacher accesses 2022 dashboard', () => {
		cy.intercept('GET', '**/teachers/dashboards/executions/*').as(
			'getDashboard'
		);

		cy.changeDemoTeacherCourseExecutionMatchingAcademicTerm("1st semester 2022/2023");

		cy.demoTeacherLogin();
		cy.get('[data-cy="dashboardMenuButton"]').click();
		cy.wait('@getDashboard');

		cy.get('.bar-chart').eq(0).scrollIntoView().wait(5000).screenshot("students2022", {overwrite: true});
		cy.get('.bar-chart').eq(1).scrollIntoView().wait(5000).screenshot("quizzes2022", {overwrite: true});
		cy.get('.bar-chart').eq(2).scrollIntoView().wait(5000).screenshot("questions2022", {overwrite: true});

		/// Comparing screenshots ///

		cy.compareScreenshots("studentChart2022", "students2022");
		cy.compareScreenshots("quizChart2022", "quizzes2022");
		cy.compareScreenshots("questionChart2022", "questions2022");

		/////////////////////////////


		cy.checkStudentStats(STUDENT_STATS_2022[0], STUDENT_STATS_2022[1], STUDENT_STATS_2022[2]);
		cy.checkQuizStats(QUIZ_STATS_2022[0], QUIZ_STATS_2022[1], QUIZ_STATS_2022[2]);
		cy.checkQuestionStats(QUESTION_STATS_2022[0], QUESTION_STATS_2022[1], QUESTION_STATS_2022[2]);

		cy.contains('Logout').click();

		Cypress.on('uncaught:exception', (err, runnable) => {
			// returning false here prevents Cypress from
			// failing the test
			return false;
		});
	});

	it('teacher accesses 2019 dashboard', () => {
	  cy.intercept('GET', '**/teachers/dashboards/executions/*').as(
		'getDashboard'
	  );

	  cy.changeDemoTeacherCourseExecutionMatchingAcademicTerm("1st semester 2019/2020");
  
	  cy.demoTeacherLogin();
	  cy.get('[data-cy="dashboardMenuButton"]').click();
	  cy.wait('@getDashboard');

	  cy.get('.bar-chart').eq(0).scrollIntoView().wait(5000).screenshot("students2019", {overwrite: true});
	  cy.get('.bar-chart').eq(1).scrollIntoView().wait(5000).screenshot("quizzes2019", {overwrite: true});
	  cy.get('.bar-chart').eq(2).scrollIntoView().wait(5000).screenshot("questions2019", {overwrite: true});

	  /// Comparing screenshots ///
	
	  cy.compareScreenshots("studentChart2019", "students2019");
	  cy.compareScreenshots("quizChart2019", "quizzes2019");
	  cy.compareScreenshots("questionChart2019", "questions2019");

	  /////////////////////////////


	  cy.checkStudentStats(STUDENT_STATS_2019[0], STUDENT_STATS_2019[1], STUDENT_STATS_2019[2]);
	  cy.checkQuizStats(QUIZ_STATS_2019[0], QUIZ_STATS_2019[1], QUIZ_STATS_2019[2]);
	  cy.checkQuestionStats(QUESTION_STATS_2019[0], QUESTION_STATS_2019[1], QUESTION_STATS_2019[2]);
  
	  cy.contains('Logout').click();
  
	  Cypress.on('uncaught:exception', (err, runnable) => {
		// returning false here prevents Cypress from
		// failing the test
		return false;
	  });
	});

	afterEach(() => {
		cy.changeDemoTeacherCourseExecutionMatchingAcademicTerm("1st semester 2023/2024");
		cy.deleteStats();
	});
  });
  