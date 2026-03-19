describe('User Registration', () => {
  const user = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    password: 'password123',
  };

  beforeEach(() => {
    cy.visit('/register');
  });

  it('should complete registration', () => {
    // Test form validation - missing password field
    cy.get('input[formControlName=firstName]').type(user.firstName);
    cy.get('input[formControlName=lastName]').type(user.lastName);
    cy.get('input[formControlName=email]').type(user.email);
    cy.get('button[type=submit]').should('be.disabled');

    // Complete registration
    cy.get('input[formControlName=password]').type(user.password);
    cy.get('button[type=submit]').should('not.be.disabled');

    // Test missing first name
    cy.get('input[formControlName=firstName]').clear();
    cy.get('button[type=submit]').should('be.disabled');
    cy.get('input[formControlName=firstName]').type(user.firstName);

    // Submit registration
    cy.mockApiResponse('POST', '/api/auth/register', { statusCode: 200 });
    cy.get('input[formControlName=password]').type('{enter}');

    // Redirected to login
    cy.url().should('include', '/login');
  });
});
