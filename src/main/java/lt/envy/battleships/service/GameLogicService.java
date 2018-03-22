package lt.envy.battleships.service;

import lt.envy.battleships.entity.*;
import lt.envy.battleships.utils.GameConstants;
import lt.envy.battleships.utils.GameUtilityService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class GameLogicService {
    GameService gameService = new GameService();
    GameUtilityService utilityService = new GameUtilityService();

    //OUT
    public Ship generateShip(GameData game, Coordinate startCoordinate, int shipSize, char orientation) {

        List<String> columnHeaders = game.getColumns();
        List<Long> rowHeaders = game.getRows();
        String startCol = startCoordinate.getColumn();

        int startRow = startCoordinate.getRow();

        int columnIndex = columnHeaders.indexOf(startCol);
        //Horizontal
        // When ship is horizontal, the row is constant and we iterate trough columns
        if ('h' == orientation) {
            for (int i = 0; i < shipSize; i++) {
                if (i == (shipSize - 1)) {
                    return new Ship(startCoordinate, new Coordinate(columnHeaders.get(columnIndex + i), startRow));
                }
            }
        }
        //Vertical
        if ('v' == orientation) {
            for (int i = 0; i < shipSize; i++) {
                if (i == (shipSize - 1)) {
                    return new Ship(startCoordinate, new Coordinate(columnHeaders.get(columnIndex), (startRow + i)));
                }
            }
        }
        return null;
    }

    //OUT
    public List<Ship> addShipToShipyard(List<Ship> shipyard, Ship ship) {
        shipyard.add(ship);
        return shipyard;
    }

    //out
    public void generatePlayerBoards(GameData game) {
        String[][] enemyBoard = generateEmptyBoard();
        String[][] myBoard = generateEmptyBoard();

    }

    //out
    public String[][] generateEmptyBoard() {
        Board board = new Board(new String[10][10]);
        String[][] arena = board.getBoard();
        for (int i = 0; i < arena.length; i++) {
            for (int j = 0; j < arena[i].length; j++) {
                arena[i][j] = GameConstants.WATER_SYMBOL;
            }
        }
        return arena;
    }

    //out?
    private void printBoard(String[][] board, GameData game) {
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

    //Out
    public void drawGameBoard(GameData game) {
        System.out.println(".......PLAYER_BOARD.....................");
        printBoard(game.getPlayerBoard(), game);
        System.out.println(".......ENEMY_BOARD......................");
        printBoard(game.getEnemyBoard(), game);
    }

    //OUT
    // for automatic ship deployment
    public String[][] setShipsToPlayerBoard(String[][] myBoard, List<Ship> shipyard, GameData game) {
        List<String> columns = game.getColumns();

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


            }
            if (startRowIndex == endRowIndex) {
                int shipSize = endColumnIndex - startColumnIndex;
                for (int i = 0; i < shipSize + 1; i++) {
                    myBoard[startRowIndex][startColumnIndex + i] = GameConstants.BOAT_HULL_SYMBOL;
                }

            }

        }
        return myBoard;
    }

    public void markTheTarget(GameData game, User user) throws IOException, ParseException {

        GameData status = gameService.status(game.getGameId());
        List<String> columns = status.getColumns();
        List<Long> rows = status.getRows();
        String[][] palyerBoard = game.getPlayerBoard();
        String[][] enemyBoard = game.getEnemyBoard();
        for (Event ev : status.getListOfEvents()) {
            Coordinate eventCoordinate = ev.getCoordinate();
            String col = eventCoordinate.getColumn();
            long row = eventCoordinate.getRow();
            if (!ev.getUserId().equals(user.getUserId())) {
                if (ev.isHit()) {
                    palyerBoard[rows.indexOf(row)][columns.indexOf(col)] = GameConstants.HIT_SYMBOL;
                }
                if (!ev.isHit()) {
                    palyerBoard[rows.indexOf(row)][columns.indexOf(col)] = GameConstants.MISS_SYMBOL;
                }

            } else {
                if (ev.isHit()) {
                    enemyBoard[rows.indexOf(row)][columns.indexOf(col)] = GameConstants.HIT_SYMBOL;
                }
                if (!ev.isHit()) {
                    enemyBoard[rows.indexOf(row)][columns.indexOf(col)] = GameConstants.MISS_SYMBOL;
                }

            }
        }

    }

    public GameData oponentsTurn(GameData game, User user) throws InterruptedException, IOException, ParseException {
        GameData statusData = gameService.status(game.getGameId());
        String winnerId = statusData.getWinnerId();
        String nextPlayerId = statusData.getNextTurnForUserId();
        List<Event> eventListFromGame = game.getListOfEvents();
        List<Event> eventListFromStatus = statusData.getListOfEvents();
        while(eventListFromGame.size() == eventListFromStatus.size()){
            Thread.sleep(1000);
            System.out.print("....");
            statusData = gameService.status(game.getGameId());
            eventListFromStatus = statusData.getListOfEvents();
        }
//        markTheTarget(game, user);
        statusData.setPlayerBoard(game.getPlayerBoard());
        statusData.setEnemyBoard(game.getEnemyBoard());


        return statusData;


//        while(user.getUserId().equals(nextPlayerId)){
//
//            Thread.sleep(1000);
//            System.out.print("....");
//            if(!user.getUserId().equals(nextPlayerId)){
//                break;
//            }
//        }
//        return statusData;
    }

    public GameData takeTurn(GameData game, User user, Scanner scanner) throws IOException, ParseException, InterruptedException {
        GameData statusData = gameService.status(game.getGameId());
        String target;
//        drawGameBoard(game);
//        utilityService.shotHistory(game);

        if (user.getUserId().equals(statusData.getNextTurnForUserId())) {
            System.out.println("YOUR TURN. MAKE A MOVE. (e.g. T4): ");
//            utilityService.shotHistory(game);
            target = scanner.nextLine();
            GameData turnData = gameService.turn(game.getGameId(), user.getUserId(), target);
//            markTheTarget(game, user);
            turnData.setEnemyBoard(game.getEnemyBoard());
            turnData.setPlayerBoard(game.getPlayerBoard());
//            utilityService.shotHistory(turnData);
            return turnData;
        }

        statusData = oponentsTurn(game, user);
        statusData.setPlayerBoard(game.getPlayerBoard());
        statusData.setEnemyBoard(game.getEnemyBoard());
        return statusData;


    }

}



