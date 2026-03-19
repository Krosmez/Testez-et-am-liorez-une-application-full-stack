describe('Session Edit', () => {
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

  const mockTeachers = [
    {
      id: 1,
      firstName: 'Sarah',
      lastName: 'Johnson',
      createdAt: '2024-01-01T00:00:00.000Z',
      updatedAt: '2024-06-01T00:00:00.000Z',
    },
    {
      id: 2,
      firstName: 'Mike',
      lastName: 'Wilson',
      createdAt: '2024-02-01T00:00:00.000Z',
      updatedAt: '2024-07-01T00:00:00.000Z',
    },
  ];

  function loginAsAdmin() {
    cy.mockApiResponse('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'admin@test.com',
        firstName: 'Admin',
        lastName: 'User',
        admin: true,
        token: 'fake-token',
        type: 'Bearer',
      },
    });
    cy.mockApiResponse('GET', '/api/session', [mockSession]);

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('admin@test.com');
    cy.get('input[formControlName=password]').type('password123{enter}{enter}');
    cy.url().should('include', '/sessions');
  }

  function navigateToEdit() {
    cy.mockApiResponse('GET', '/api/session/1', mockSession);
    cy.mockApiResponse('GET', '/api/teacher', mockTeachers);
    cy.contains('Edit').click();
    cy.url().should('include', '/sessions/update/1');
  }

  beforeEach(() => {
    loginAsAdmin();
  });

  it('should display the update form with pre-filled values', () => {
    navigateToEdit();

    cy.contains('Update session').should('be.visible');
    cy.get('input[formControlName=name]').should('have.value', 'Morning Yoga');
    cy.get('input[formControlName=date]').should('have.value', '2025-06-15');
    cy.get('textarea[formControlName=description]').should(
      'have.value',
      'A relaxing morning yoga session',
    );
  });

  it('should disable Save button when a required field is empty', () => {
    navigateToEdit();

    cy.get('input[formControlName=name]').clear();
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=name]').type('Updated Session');
    cy.get('button[type=submit]').should('not.be.disabled');
  });

  it('should update a session and redirect to sessions list', () => {
    navigateToEdit();

    cy.get('input[formControlName=name]').clear().type('Evening Yoga');
    cy.get('input[formControlName=date]').clear().type('2025-09-20');
    cy.get('textarea[formControlName=description]')
      .clear()
      .type('A calming evening session');

    // Change teacher
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('Mike Wilson').click();

    const updatedSession = {
      ...mockSession,
      name: 'Evening Yoga',
      date: '2025-09-20',
      description: 'A calming evening session',
      teacher_id: 2,
    };
    cy.mockApiResponse('PUT', '/api/session/1', {
      statusCode: 200,
      body: updatedSession,
    });
    cy.mockApiResponse('GET', '/api/session', [updatedSession]);

    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions');
    cy.contains('Evening Yoga').should('be.visible');
  });

  it('should navigate back to sessions list via back button', () => {
    navigateToEdit();

    cy.mockApiResponse('GET', '/api/session', [mockSession]);
    cy.get('button[mat-icon-button]').first().click();
    cy.url().should('include', '/sessions');
  });

  it('should redirect non-admin user away from edit page', () => {
    // Login as non-admin
    cy.contains('Logout').click();

    cy.mockApiResponse('POST', '/api/auth/login', {
      body: {
        id: 2,
        username: 'user@test.com',
        firstName: 'Jane',
        lastName: 'Doe',
        admin: false,
        token: 'fake-token',
        type: 'Bearer',
      },
    });
    cy.mockApiResponse('GET', '/api/session', [mockSession]);

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('user@test.com');
    cy.get('input[formControlName=password]').type('password123{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Edit button should not be visible for non-admin
    cy.get('button').contains('Edit').should('not.exist');
  });
});