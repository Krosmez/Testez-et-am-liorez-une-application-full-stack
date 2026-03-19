describe('Session Detail', () => {
  const mockSession = {
    id: 1,
    name: 'Morning Yoga',
    description: 'A relaxing morning yoga session',
    date: '2025-06-15',
    teacher_id: 1,
    users: [3, 4],
    createdAt: '2025-01-10T00:00:00.000Z',
    updatedAt: '2025-03-01T00:00:00.000Z',
  };

  const mockTeacher = {
    id: 1,
    firstName: 'Sarah',
    lastName: 'Johnson',
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-06-01T00:00:00.000Z',
  };

  function loginAs(isAdmin: boolean) {
    cy.mockApiResponse('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'user@test.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: isAdmin,
        token: 'fake-token',
        type: 'Bearer',
      },
    });
    cy.mockApiResponse('GET', '/api/session', [mockSession]);

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('user@test.com');
    cy.get('input[formControlName=password]').type('password123{enter}{enter}');
    cy.url().should('include', '/sessions');
  }

  function navigateToDetail() {
    cy.mockApiResponse('GET', '/api/session/1', mockSession);
    cy.mockApiResponse('GET', '/api/teacher/1', mockTeacher);
    cy.contains('Detail').click();
    cy.url().should('include', '/sessions/detail/1');
  }

  describe('as a regular user', () => {
    beforeEach(() => {
      loginAs(false);
    });

    it('should display session details', () => {
      navigateToDetail();

      cy.contains('Morning Yoga').should('be.visible');
      cy.contains('A relaxing morning yoga session').should('be.visible');
      cy.contains('Sarah JOHNSON').should('be.visible');
      cy.contains('2 attendees').should('be.visible');
      cy.contains('June 15, 2025').should('be.visible');
    });

    it('should display Participate button when user is not participating', () => {
      navigateToDetail();

      cy.get('button').contains('Participate').should('be.visible');
      cy.get('button').contains('Delete').should('not.exist');
    });

    it('should display Do not participate button when user is already participating', () => {
      const sessionWithUser = { ...mockSession, users: [1, 3, 4] };

      cy.mockApiResponse('GET', '/api/session/1', sessionWithUser);
      cy.mockApiResponse('GET', '/api/teacher/1', mockTeacher);
      cy.contains('Detail').click();

      cy.get('button').contains('Do not participate').should('be.visible');
      cy.contains('3 attendees').should('be.visible');
    });

    it('should participate in a session', () => {
      navigateToDetail();

      const updatedSession = { ...mockSession, users: [1, 3, 4] };
      cy.mockApiResponse('POST', '/api/session/1/participate/1', {
        statusCode: 200,
      });
      cy.mockApiResponse('GET', '/api/session/1', updatedSession);
      cy.mockApiResponse('GET', '/api/teacher/1', mockTeacher);

      cy.get('button').contains('Participate').click();

      cy.get('button').contains('Do not participate').should('be.visible');
      cy.contains('3 attendees').should('be.visible');
    });

    it('should unparticipate from a session', () => {
      const sessionWithUser = { ...mockSession, users: [1, 3, 4] };
      cy.mockApiResponse('GET', '/api/session/1', sessionWithUser);
      cy.mockApiResponse('GET', '/api/teacher/1', mockTeacher);
      cy.contains('Detail').click();

      const updatedSession = { ...mockSession, users: [3, 4] };
      cy.mockApiResponse('DELETE', '/api/session/1/participate/1', {
        statusCode: 200,
      });
      cy.mockApiResponse('GET', '/api/session/1', updatedSession);
      cy.mockApiResponse('GET', '/api/teacher/1', mockTeacher);

      cy.get('button').contains('Do not participate').click();

      cy.get('button').contains('Participate').should('be.visible');
      cy.contains('2 attendees').should('be.visible');
    });

    it('should navigate back to sessions list', () => {
      navigateToDetail();

      cy.mockApiResponse('GET', '/api/session', [mockSession]);
      cy.get('button[mat-icon-button]').first().click();
      cy.url().should('include', '/sessions');
    });
  });

  describe('as an admin', () => {
    beforeEach(() => {
      loginAs(true);
    });

    it('should display Delete button instead of Participate', () => {
      navigateToDetail();

      cy.get('button').contains('Delete').should('be.visible');
      cy.get('button').contains('Participate').should('not.exist');
    });

    it('should delete a session', () => {
      navigateToDetail();

      cy.mockApiResponse('DELETE', '/api/session/1', { statusCode: 200 });
      cy.mockApiResponse('GET', '/api/session', []);
      cy.get('button').contains('Delete').click();

      cy.url().should('include', '/sessions');
    });
  });
});
