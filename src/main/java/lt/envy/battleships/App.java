package lt.envy.battleships;

import lt.envy.battleships.entity.GameData;
import lt.envy.battleships.entity.Ship;
import lt.envy.battleships.entity.User;
import lt.envy.battleships.service.GameLogicService;
import lt.envy.battleships.service.GameService;
import lt.envy.battleships.service.UserService;
import lt.envy.battleships.utils.GameConstants;
import lt.envy.battleships.utils.GameUtilityService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class App {
    private static UserInterface ui = new UserInterface();
    private static UserService userService = new UserService();
    private static GameService gameService = new GameService();
    private static GameLogicService gameLogicService = new GameLogicService();
    private static GameUtilityService utilityService = new GameUtilityService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {

        List<Ship> shipyard = new ArrayList<>();

        String[][] playerBoard;
        String[][] enemyBoard;
        Scanner scanner = new Scanner(System.in);
        ui.printGreeting();
        User user = ui.setUpPlayer(scanner, userService);
        GameData joinGameData = ui.initialiseGame(user, gameService);
        utilityService.waitForGameStatusChange(joinGameData, GameConstants.READY_FOR_SHIPS);
        // Method for automatic deployment
        List<Ship> loadedShipyard = utilityService.shipLoader(shipyard, joinGameData);
        // Method for manual deployment
//        List<Ship> loadedShipyard = ui.setupShipyard(scanner,joinGameData,gameService);
        playerBoard = gameLogicService.generateEmptyBoard();
        enemyBoard = gameLogicService.generateEmptyBoard();
        String shipyardCoordinateString = utilityService.parseShipyardToUrl(loadedShipyard);
        GameData setupData = gameService.setup(joinGameData.getGameId(), user.getUserId(), shipyardCoordinateString);
        String[][] loadedPlayerBoard = gameLogicService.setShipsToPlayerBoard(playerBoard, loadedShipyard, setupData);
        setupData.setEnemyBoard(enemyBoard);
        setupData.setPlayerBoard(loadedPlayerBoard);
        utilityService.waitForGameStatusChange(setupData, GameConstants.READY_TO_PLAY);

        while (!gameService.status(setupData.getGameId()).getStatus().equals(GameConstants.FINISHED)) {
            GameData newData = gameService.status(setupData.getGameId());
            newData.setPlayerBoard(setupData.getPlayerBoard());
            newData.setEnemyBoard(setupData.getEnemyBoard());
            gameLogicService.markTheTarget(newData, user);
            gameLogicService.drawGameBoard(newData);
            utilityService.shotHistory(newData);
            GameData turnData = gameLogicService.takeTurn(newData, user, scanner);

        }
        GameData statusData = gameService.status(setupData.getGameId());
        if (gameService.status(statusData.getGameId()).getWinnerId().length() != 0) {
            if (gameService.status(statusData.getGameId()).getWinnerId().equals(user.getUserId()) && gameService.status(statusData.getGameId()).getWinnerId().length() != 0) {
                System.out.println("CONGRATULATIONS!!! YOU HAVE WON THE GAME, CAPTAIN!");

            }
            if (!gameService.status(statusData.getGameId()).getWinnerId().equals(user.getUserId()) && gameService.status(statusData.getGameId()).getWinnerId().length() != 0) {
                System.out.println("LOSER!!! GO ROW YOUR LITTLE BOAT!");

            }
        }
        scanner.close();

    }
}
