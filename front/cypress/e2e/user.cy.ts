describe('User Session Participation', () => {
  it('should login as user, view sessions, participate, check account, and logout with navigation guards', () => {
    const mockSession = {
      id: 1,
      name: 'Morning Yoga',
      description: 'Start your day fresh',
      date: '2024-12-25',
      teacher_id: 1,
      users: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    const mockTeacher = { id: 1, firstName: 'Sarah', lastName: 'Johnson' };

    const mockUserDetails = {
      id: 2,
      email: 'user@studio.com',
      firstName: 'Jane',
      lastName: 'Smith',
      admin: false,
      createdAt: new Date('2023-01-15'),
      updatedAt: new Date('2023-06-20'),
    };

    // Login as regular user
    cy.mockApiResponse('POST', '/api/auth/login', {
      body: {
        id: 2,
        username: 'user@studio.com',
        firstName: 'Jane',
        lastName: 'Smith',
        admin: false,
      },
    });
    cy.mockApiResponse('GET', '/api/session', [mockSession]);

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('user@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');

    // Verify sessions list - no admin buttons
    cy.url().should('include', '/sessions');
    cy.contains('Morning Yoga').should('be.visible');
    cy.contains('Create').should('not.exist');
    cy.get('button').contains('Edit').should('not.exist');

    // View session details
    cy.mockApiResponse('GET', '/api/session/1', mockSession);
    cy.mockApiResponse('GET', '/api/teacher/1', mockTeacher);
    cy.contains('Detail').click();

    // Verify no delete button but participate button exists
    cy.url().should('include', '/sessions/detail/1');
    cy.contains('Morning Yoga').should('be.visible');
    cy.contains('Start your day fresh').should('be.visible');
    cy.contains('Sarah JOHNSON').should('be.visible');
    cy.get('button').contains('Delete').should('not.exist');
    cy.get('button').contains('Participate').should('be.visible');

    // Participate in session
    cy.mockApiResponse('POST', '/api/session/1/participate/2', {
      statusCode: 200,
    });
    cy.mockApiResponse('GET', '/api/session/1', {
      ...mockSession,
      users: [2],
    });
    cy.get('button').contains('Participate').click();

    // Verify participation
    cy.get('button').contains('Do not participate').should('be.visible');
    cy.contains('1 attendees').should('be.visible');

    // Navigate back to sessions
    cy.get('button[mat-icon-button]').first().click();
    cy.url().should('include', '/sessions');

    // Go to account page
    cy.mockApiResponse('GET', '/api/user/2', mockUserDetails);
    cy.contains('Account').click();

    // Verify account information
    cy.url().should('include', '/me');
    cy.contains('User information').should('be.visible');
    cy.contains('Jane SMITH').should('be.visible');
    cy.contains('user@studio.com').should('be.visible');
    cy.contains('June 20, 2023').should('be.visible');

    // Navigate back to sessions
    cy.get('button[mat-icon-button]').click();
    cy.url().should('include', '/sessions');

    // Logout
    cy.contains('Logout').should('be.visible');
    cy.contains('Logout').click();

    // Verify logout and session cleared
    cy.url().should('not.include', '/sessions');
    cy.contains('Login').should('be.visible');
    cy.contains('Logout').should('not.exist');
    cy.contains('Account').should('not.exist');

    // Test navigation guards - try accessing protected routes
    cy.visit('/sessions');
    cy.url().should('include', '/login');

    cy.visit('/me');
    cy.url().should('include', '/login');
  });
});
