package lt.envy.battleships;

import lt.envy.battleships.service.UserService;

import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    private static UserInterface ui = new UserInterface();
    private static UserService service = new UserService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ui.printGreeting();
        ui.setUpPlayers(scanner,service);
    }
}
