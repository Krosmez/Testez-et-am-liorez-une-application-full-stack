// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************

import type { Method } from 'cypress/types/net-stubbing';

declare global {
  namespace Cypress {
    interface Chainable<Subject = any> {
      /**
       * Mocks an API response for a given method and URL
       * @param method HTTP method (GET, POST, PUT, DELETE, etc.)
       * @param url URL to intercept
       * @param response Response to return
       */
      mockApiResponse(
        method: string,
        url: string,
        response: any,
      ): Chainable<Subject>;
    }
  }
}

// Mock API responses
Cypress.Commands.add(
  'mockApiResponse',
  (method: string, url: string, response: any) => {
    cy.intercept(method as Method, url, response);
  },
);

export {};
