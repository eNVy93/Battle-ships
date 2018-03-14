package lt.envy.battleships;

import lt.envy.battleships.entity.Game;
import lt.envy.battleships.entity.User;
import lt.envy.battleships.service.GameService;
import lt.envy.battleships.service.UserService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface {
    //TODO use StringBuilder for strings !!!!!
    public void printGreeting() {
        System.out.println("**************" +
                "\nWelcome to the best battleship app.\n" +
                "***************\n");
    }

    public User setUpPlayers(Scanner sc, UserService service) {
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

    public void initialiseGame(User user, GameService gameService) throws IOException, ParseException, InterruptedException {
        System.out.println("User : " + user.getName() + "\n" +
                "Id : " + user.getUserId() + "\nConnected");
        Game game = gameService.joinUser(user.getUserId());
        System.out.println("Game id: " + game.getGameId() + " initialised");
        System.out.println(game.getStatus());

        gameService.waitForGameStatusChange(game);

        System.out.println(gameService.getStatus(game.getGameId()));
        String[][] enemyBoard = gameService.generateBoard();
        String[][] myBoard = gameService.generateBoard();
        System.out.println("---___ENEMY_BOARD___---");
        gameService.printBoard(enemyBoard,game);
        System.out.println("---___PLAYER_BOARD___---");
        gameService.printBoard(myBoard,game);


    }
}
