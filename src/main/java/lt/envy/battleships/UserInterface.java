package lt.envy.battleships;

import lt.envy.battleships.entity.*;
import lt.envy.battleships.service.GameService;
import lt.envy.battleships.service.UserService;
import lt.envy.battleships.utils.GameConstants;
import lt.envy.battleships.utils.GameUtilityService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    //TODO use StringBuilder for strings !!!!!
    private GameUtilityService utilityService = new GameUtilityService();

    public void printGreeting() {
        System.out.println("**************" +
                "\nWelcome to the best battleship app.\n" +
                "***************\n");
    }

    // Creates a user
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

    // Joins user to the game. Initialises new Game object.
    public Game initialiseGame(User user, GameService gameService) throws IOException, ParseException, InterruptedException {

        System.out.println("User : " + user.getName() + "\n" +
                "Id : " + user.getUserId() + "\nConnected");
        Game game = gameService.joinUser(user.getUserId());

        System.out.println("Game id: " + game.getGameId());
        System.out.println(game.getStatus());

        return game;
    }

    // For manual ship deployment
    public void setupShipyard(Scanner scanner, Game game, GameService gameService) {
        System.out.println("It's time to set up your battlefield!");
        System.out.println("-----------------QUICK REMINDER-----------------------");
        System.out.println("To deploy your ship enter the starting coordinate. ex. L3h");
        System.out.println("'h' - for horizontal ship orientation, 'v' - for vertical");
        System.out.println(".................................................................................................");
        System.out.println("LETS BEGIN!");
        for (int i = 0; i < Game.SHIPYARD_CONFIGURATION.length; i++) {
            System.out.println("Deploy ship. Size: " + Game.SHIPYARD_CONFIGURATION[i]);
            System.out.println("Enter the starting coordinate. For example: L4v");
            String shipCoordinateString = utilityService.validateCoordinateInput(scanner);
            Coordinate shipCoordinate = utilityService.convertInputStringToCoordinate(shipCoordinateString);
            char orientationCharacter = utilityService.getOrientationCharFromInputString(shipCoordinateString);
            Ship ship = gameService.generateShip(game, shipCoordinate, Game.SHIPYARD_CONFIGURATION[i], orientationCharacter);
            gameService.addShipToShipyard(game, ship);
        }
    }

    public void generatePlayerBoards(Game game) {
        String[][] enemyBoard = generateEmptyBoard();
        String[][] myBoard = generateEmptyBoard();
        game.setPlayerBoard(myBoard);
        game.setEnemyBoard(enemyBoard);
    }

    private String[][] generateEmptyBoard() {
        Board board = new Board(new String[10][10]);
        String[][] arena = board.getBoard();
        for (int i = 0; i < arena.length; i++) {
            for (int j = 0; j < arena[i].length; j++) {
                arena[i][j] = GameConstants.WATER_SYMBOL;
            }
        }
        return arena;
    }


    private void printBoard(String[][] board, Game game) {
        List<String> columns = game.getColumns();
        List<Long> rows = game.getRows();

        System.out.printf("%-3s", " ");
        for (String s : columns) {
            System.out.printf("%-3s", s);
        }
        System.out.println("");

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (j == 0) {
                    System.out.printf("%-3s", rows.get(i));
                }
                System.out.printf("%-3s", board[i][j]);
            }

            System.out.println("");
        }
    }

    public void drawGameBoard(Game game) {
        String[][] enemyBoard = game.getEnemyBoard();
        String[][] playerBoard = game.getPlayerBoard();
        System.out.println(".......PLAYER_BOARD.....................");
        printBoard(playerBoard, game);
        System.out.println(".......ENEMY_BOARD......................");
        printBoard(enemyBoard, game);
    }

    public void drawEvents(){

    }
    // for automatic ship deployment
    public void setShipsToPlayerBoard(Game game) {
        List<String> columns = game.getColumns();
        String[][] myBoard = game.getPlayerBoard();
        List<Ship> shipyard = game.getShipyard();

        for (Ship ship : shipyard) {
            int startColumnIndex = columns.indexOf(ship.getStartCoordinate().getColumn());
            int endColumnIndex = columns.indexOf(ship.getEndCoordinate().getColumn());
            int startRowIndex = ship.getStartCoordinate().getRow();
            int endRowIndex = ship.getEndCoordinate().getRow();

            //Check ships orientation
            // if startCol == endCol ship is vertical
            if (startColumnIndex == endColumnIndex) {
                int shipSize = endRowIndex - startRowIndex;
                for (int i = 0; i < shipSize + 1; i++) {
                    myBoard[startRowIndex + i][startColumnIndex] = GameConstants.BOAT_HULL_SYMBOL;

                }
                game.setPlayerBoard(myBoard);

            }
            if (startRowIndex == endRowIndex) {
                int shipSize = endColumnIndex - startColumnIndex;
                for (int i = 0; i < shipSize + 1; i++) {
                    myBoard[startRowIndex][startColumnIndex + i] = GameConstants.BOAT_HULL_SYMBOL;
                }
                game.setPlayerBoard(myBoard);
            }

        }

    }


}
