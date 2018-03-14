package lt.envy.battleships;

import lt.envy.battleships.entity.User;
import lt.envy.battleships.service.GameService;
import lt.envy.battleships.service.UserService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    private static UserInterface ui = new UserInterface();
    private static UserService service = new UserService();
    private static GameService gameService = new GameService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        ui.printGreeting();
        User user = ui.setUpPlayers(scanner,service);
        ui.initialiseGame(user,gameService);

        //TODO method that waits for status change
    }
}
