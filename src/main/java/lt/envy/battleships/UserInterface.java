package lt.envy.battleships;

import lt.envy.battleships.entity.*;
import lt.envy.battleships.service.GameLogicService;
import lt.envy.battleships.service.GameService;
import lt.envy.battleships.service.UserService;
import lt.envy.battleships.utils.GameUtilityService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

    private GameUtilityService utilityService = new GameUtilityService();
    private GameLogicService logicService = new GameLogicService();

    public void printGreeting() {
        System.out.println("**************" +
                "\nWelcome to the best battleship app.\n" +
                "***************\n");
    }

    public User setUpPlayer(Scanner sc, UserService service) {
        System.out.println("Enter name for PlayerOne: ");
        String playerOneName = sc.nextLine();
        System.out.println("Enter email for PlayerOne: ");
        String playerOneEmail = sc.nextLine();

        try {
            User playerOne = service.createUser(playerOneName, playerOneEmail);
            if (playerOne.getUserId() != null) {
                System.out.println("PlayerOne: " + playerOne.getName() + " created");
                return playerOne;
            } else {
                System.out.println("Player two not created");
                return null;
            }

        } catch (IOException | ParseException e) {
            System.out.println("Klaida " + e.getMessage());
        }
        return null;
    }

    public GameData initialiseGame(User user, GameService gameService) throws IOException, ParseException, InterruptedException {

        System.out.println("User : " + user.getName() + "\n" +
                "Id : " + user.getUserId() + "\nConnected");
        GameData gamedata = gameService.join(user.getUserId());
        System.out.println("Game id: " + gamedata.getGameId());
        System.out.println(gamedata.getStatus());

        return gamedata;
    }

    public List<Ship> setupShipyard(Scanner scanner, GameData game, GameService gameService) {
        System.out.println("It's time to set up your battlefield!");
        System.out.println("-----------------QUICK REMINDER-----------------------");
        System.out.println("To deploy your ship enter the starting coordinate. ex. L3h");
        System.out.println("'h' - for horizontal ship orientation, 'v' - for vertical");
        System.out.println(".................................................................................................");
        System.out.println("LETS BEGIN!");
        List<Ship> resultList = new ArrayList<>();
        for (int i = 0; i < GameService.SHIPYARD_CONFIGURATION.length; i++) {
            System.out.println("Deploy ship. Size: " + GameService.SHIPYARD_CONFIGURATION[i]);
            System.out.println("Enter the starting coordinate. For example: L4v");
            String shipCoordinateString = utilityService.validateCoordinateInput(scanner);
            Coordinate shipCoordinate = utilityService.convertInputStringToCoordinate(shipCoordinateString);
            char orientationCharacter = utilityService.getOrientationCharFromInputString(shipCoordinateString);
            Ship ship = logicService.generateShip(game, shipCoordinate, GameService.SHIPYARD_CONFIGURATION[i], orientationCharacter);
            logicService.addShipToShipyard(resultList, ship);
        }
        return resultList;
    }


}
