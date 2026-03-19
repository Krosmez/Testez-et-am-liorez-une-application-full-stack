describe('User Authentication', () => {
  const user = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    password: 'password123',
  };

  it('should test login errors', () => {
    cy.visit('/login');

    // Test login with wrong credentials
    cy.mockApiResponse('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Bad credentials' },
    }).as('badLogin');
    cy.get('input[formControlName=email]').type('wrong@email.com');
    cy.get('input[formControlName=password]').type('wrongpass{enter}{enter}');
    cy.wait('@badLogin');
    cy.get('.error').should('be.visible');

    // Clear and test missing field validation
    cy.visit('/login');
    cy.get('input[formControlName=email]').type(user.email);
    cy.get('button[type=submit]').should('be.disabled');
  });

  it('should login successfully and logout', () => {
    cy.visit('/login');

    // Login with correct credentials
    cy.mockApiResponse('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        admin: false,
      },
    });
    cy.mockApiResponse('GET', '/api/session', []);
    cy.get('input[formControlName=email]').type(user.email);
    cy.get('input[formControlName=password]').type(
      user.password + '{enter}{enter}',
    );

    // Verify logged in
    cy.url().should('include', '/sessions');
    cy.contains('Logout').should('be.visible');

    // Logout
    cy.contains('Logout').click();
    cy.url().should('not.include', '/sessions');
    cy.contains('Login').should('be.visible');
  });
});
