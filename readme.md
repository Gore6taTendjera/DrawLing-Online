# DrawLing Online

DrawLing Online is a Multiplayer Browser Game where players can draw or guess a word on a white canvas. Players are rotated per round 'Drawer' or' Guesser:

- **Drawer**: Draws a word on the canvas.
- **Guesser**: Tries to guess the word being drawn.

The `Normal` game mode requires a minimum of 2 players to start, and the lobby settings can determine the duration and number of rounds. Only 1 player is selected to draw per round. After each round, players are rotated. Players earn money and experience points for correctly guessing the drawings. The game ends once all rounds finished.

## Tech Stack

- **Frontend:** React + TypeScript
- **Backend:** Spring Boot
- **Database:** MySQL
- **Testing:** JUnit, Cypress
- **Code Quality:** SonarQube [Quality Gate](https://raw.githubusercontent.com/Gore6taTendjera/DrawLing-Online/refs/heads/main/Documents/SonarQubeSprint6.png)
- **Containerization:** Docker
- **CI/CD:** GitLab CI/CD


## System Design
- **Strategy Pattern**
- **Factory Pattern**
- **Repository Pattern**

+ **SOLID Principles**


## Canvas Tools

- **Color Picker**: Change brush color.
- **Brush Sizes**: Change brush size.
- **Clear Canvas**: Clear everything on the canvas.


![](https://raw.githubusercontent.com/Gore6taTendjera/DrawLing-Online/refs/heads/main/GAME-SCREENSHOTS/Gameplay.png)


## Create Lobby Page

Create a game lobby with customizable options:
- **Max Players:** Set the maximum number of players allowed in the lobby.
- **Round Time (in seconds):** Set the duration for each round.
- **Number of Rounds:** Set how many rounds will be played in the game.
- **Game Modes:** Choose from the following game modes:
  - `Normal`
  - `Duo`
  - `Trio`
  - `Combined`

+ **Select Round Types and Categories:**
  - **_(Optional)_ Word Category Selection:** Select a word category for each round. Available categories include:
    - Geography
    - Animals
    - Furniture
    - Emotion
    - Food
    - Sport
    - Plant
    - ...
  - **_(Optional)_ Round Type:** Select the round type:
    - **Normal:** Regular round time.
    - **Fast:** Round time halved.


![](https://raw.githubusercontent.com/Gore6taTendjera/DrawLing-Online/refs/heads/main/GAME-SCREENSHOTS/CreateLobbyPage.png)


## Profile Page

- Saved drawings.
- Change your display name.
- Upload a profile picture.
- Balance and Experience level.

![](https://raw.githubusercontent.com/Gore6taTendjera/DrawLing-Online/refs/heads/main/GAME-SCREENSHOTS/ProfilePage.png)

## Home Page
![](https://raw.githubusercontent.com/Gore6taTendjera/DrawLing-Online/refs/heads/main/GAME-SCREENSHOTS/HomePage.png)

## Login / Register Page
![](https://raw.githubusercontent.com/Gore6taTendjera/DrawLing-Online/refs/heads/main/GAME-SCREENSHOTS/LoginPage.png)
