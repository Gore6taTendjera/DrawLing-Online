import { GameMode } from '../enum/GameModeEnum';
import { GameRoundEnum } from '../enum/GameRoundEnum';
import { GameWordCategoryEnum } from '../enum/GameWordCategoryEnum';


export default interface CreateGameLobbyRequestData {
    maxPlayers: number;
    gameModeName: GameMode;
    gameRounds: {
        gameRoundEnum: GameRoundEnum;
        duration: number;
        wordCategory: GameWordCategoryEnum | null;
    }[];
}
