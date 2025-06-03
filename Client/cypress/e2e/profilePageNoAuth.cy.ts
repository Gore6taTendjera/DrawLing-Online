/// <reference types="cypress-xpath" />
/// <reference types="cypress" />

describe("", () => {
  it("clicks button", () => {
    cy.visit("http://localhost:5173/profile");

    cy.xpath('//*[@id="username"]').type("2");
    cy.xpath('//*[@id="password"]').type("2");

    cy.xpath("/html/body/div/div/div/form/button").click();
  });
});
