import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    screenshotOnRunFailure: false,
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
});
