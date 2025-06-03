/// <reference types="cypress-xpath" />
/// <reference types="cypress" />

describe("Creating lobby With Auth Login", () => {
  // keeps the refresh token for the "Lobby Creation" test
  beforeEach(() => {
    cy.session("authSession", () => {
      cy.visit("http://localhost:5173/login");
      cy.get('[data-cy="input-username"]').type("2");
      cy.get('[data-cy="input-password"]').type("2");
      cy.get('[data-cy="button-login"]').click();

      cy.wait(5000);
    });
  });

  it("Lobby Creation", () => {
    cy.visit("http://localhost:5173/lobby");

    cy.get('[data-cy="input-maxPlayers"]')
      .clear()
      .type("1")
      .type("{uparrow}{uparrow}{uparrow}");

    cy.get('[data-cy="gamemode-NORMAL"]').click();
    cy.get('[data-cy="button-create-lobby"]').click();
    cy.get('[data-cy="button-go-to-room"]').click();
  });
});
