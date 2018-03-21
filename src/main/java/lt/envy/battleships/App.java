package lt.envy.battleships;

import lt.envy.battleships.entity.Game;
import lt.envy.battleships.entity.User;
import lt.envy.battleships.service.GameLogicService;
import lt.envy.battleships.service.GameService;
import lt.envy.battleships.service.UserService;
import lt.envy.battleships.utils.GameConstants;
import lt.envy.battleships.utils.GameUtilityService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;


public class App {
    private static UserInterface ui = new UserInterface();
    private static UserService service = new UserService();
    private static GameService gameService = new GameService();
    static GameLogicService gameLogicService = new GameLogicService();
    private static GameUtilityService utilityService = new GameUtilityService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {

       Scanner scanner = new Scanner(System.in);
        // ONLINE BATTLESHIP
        //1.Greeting for the player
        ui.printGreeting();
        //2.Create a user
        User user = ui.setUpPlayer(scanner, service);
        //3. Connect user to a game
        Game game = ui.initialiseGame(user, gameService);
        //4. Wait for player to connect (check status for Ready for ships)
        utilityService.waitForGameStatusChange(game, GameConstants.READY_FOR_SHIPS);
        //5.Generate empty boards
        gameLogicService.generatePlayerBoards(game);
        //6. loads ships automatically
        // should return a list, without gameservice interaction
        utilityService.shipLoader(game);
//        //7. Draw enemy and player boards
//        ui.drawGameBoard(game);
        gameLogicService.setShipsToPlayerBoard(game);
        //8. Send shipyard data to server
        String shipCoordinates = utilityService.parseShipyardToUrl(game);
        gameService.sendShips(game,shipCoordinates,user.getUserId());
        //8.Check for status change.
        utilityService.waitForGameStatusChange(game, GameConstants.READY_TO_PLAY);

        utilityService.setGameEventListFromStatus(utilityService.getStatusString(game.getGameId()),game);

//        gameLogicService.play(game,user,scanner);

        gameLogicService.playVersionTwo(game,user,scanner);
        scanner.close();

        //6. For manual ship input
//        ui.setupShipyard(scanner, game, gameService);

//        // OFFLINE TEST
//        ui.offlineTest(null,null,gameService);
//        gameService.validateCoordinateInput(scanner);
//        ui.shipOrientationInputValidation(scanner);

        //TODO method that waits for status change
    }
}
