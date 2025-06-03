/// <reference types="cypress-xpath" />
/// <reference types="cypress" />

describe('creating lobby', () => {
  let link = "";


  it('Lobby Creation', () => {
    cy.visit("http://localhost:5173/lobby");

    cy.get('[data-cy="input-maxPlayers"]')
      .clear()
      .type("1")
      .type("{uparrow}{uparrow}{uparrow}");

    cy.get('[data-cy="gamemode-NORMAL"]').click();
    cy.get('[data-cy="button-create-lobby"]').click();
    cy.on('window:alert', (str) => {
      expect(str).to.equal('You are not authorized to create a game lobby. Please log in.')
    });
  });


});
