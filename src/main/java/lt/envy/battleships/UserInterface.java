package lt.envy.battleships;

import lt.envy.battleships.entity.Coordinate;
import lt.envy.battleships.entity.Game;
import lt.envy.battleships.entity.Ship;
import lt.envy.battleships.entity.User;
import lt.envy.battleships.service.GameService;
import lt.envy.battleships.service.UserService;
import lt.envy.battleships.utils.GameStatus;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        game.setPlayerBoard(myBoard);
        game.setEnemyBoard(enemyBoard);

        System.out.println("---___ENEMY_BOARD___---");
        gameService.printBoard(enemyBoard, game);
        System.out.println("---___PLAYER_BOARD___---");
        gameService.printBoard(myBoard, game);


    }

    public void offlineTest(User user, Game game, GameService gameService) {
        List<String> cols = new ArrayList<>();
        {
            cols.add("K");
            cols.add("I");
            cols.add("L");
            cols.add("O");
            cols.add("M");
            cols.add("E");
            cols.add("T");
            cols.add("R");
            cols.add("A");
            cols.add("S");
        }
        List<Long> rows = new ArrayList<>();
        {
            rows.add(0L);
            rows.add(1L);
            rows.add(2L);
            rows.add(3L);
            rows.add(4L);
            rows.add(5L);
            rows.add(6L);
            rows.add(7L);
            rows.add(8L);
            rows.add(9L);
        }

        user = new User("123test", "Testeris", "Tester@Testovic.ru");
        game = new Game("testGame123", GameStatus.READY_FOR_SHIPS, null, "", "", cols, rows);

        String[][] myBoard = gameService.generateBoard();
        game.setPlayerBoard(myBoard);


        //TODO priimkime kad useris yra ne durnas ir laivus sudes atsizvelgdamas i taisykles. Todel reikia mesti exceptionus
        Ship carrier = gameService.deployShip(game, new Coordinate("L", 8), 4, 'h');
        gameService.addShipToShipyard(game, carrier);

        Ship battleCruiser = gameService.deployShip(game,new Coordinate("I",4),3,'v');
        gameService.addShipToShipyard(game,battleCruiser);

        Ship battleCruiser2 = gameService.deployShip(game,new Coordinate("R",1),3,'v');
        gameService.addShipToShipyard(game,battleCruiser2);

        Ship cruiser = gameService.deployShip(game,new Coordinate("I",1),2,'h');
        gameService.addShipToShipyard(game,cruiser);

        Ship cruiser2 = gameService.deployShip(game,new Coordinate("E",3),2,'v');
        gameService.addShipToShipyard(game,cruiser2);

        Ship cruiser3 = gameService.deployShip(game,new Coordinate("R",6),2,'h');
        gameService.addShipToShipyard(game,cruiser3);

        Ship boat = gameService.deployShip(game,new Coordinate("O",3),1,'h');
        gameService.addShipToShipyard(game,boat);

        Ship boat1 = gameService.deployShip(game,new Coordinate("O",5),1,'h');
        gameService.addShipToShipyard(game,boat1);

        Ship boat2 = gameService.deployShip(game,new Coordinate("K",9),1,'h');
        gameService.addShipToShipyard(game,boat2);

        Ship boat3 = gameService.deployShip(game,new Coordinate("A",8),1,'h');
        gameService.addShipToShipyard(game,boat3);


        String[][] shipBoard = gameService.drawShips(game, gameService);

        gameService.printBoard(shipBoard,game);

    //TODO parse shipyard to coordinates to URL
        gameService.parseShipyardToUrl(game,gameService);
        List<Ship> listOfShips = game.getShipyard();
//        System.out.println(listOfShips);
//        gameService.printBoard(myBoard, game);

    }
}
