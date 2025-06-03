/// <reference types="cypress-xpath" />
/// <reference types="cypress" />

describe("", () => {
  it("Login & Profile", () => {
    cy.visit("http://localhost:5173/login");

    cy.get('[data-cy="input-username"]').type("2");
    cy.get('[data-cy="input-password"]').type("2");
    cy.get('[data-cy="button-login"]').click();

    cy.wait(2000);
  });
});
