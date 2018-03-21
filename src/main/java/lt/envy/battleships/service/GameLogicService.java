package lt.envy.battleships.service;

import lt.envy.battleships.UserInterface;
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
    public Ship generateShip(Game game, Coordinate startCoordinate, int shipSize, char orientation) {

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
    public void addShipToShipyard(Game game, Ship ship) {
        List<Ship> shipyard = game.getShipyard();
        shipyard.add(ship);
        game.setShipyard(shipyard);
    }

    //out
    public void generatePlayerBoards(Game game) {
        String[][] enemyBoard = generateEmptyBoard();
        String[][] myBoard = generateEmptyBoard();
        game.setPlayerBoard(myBoard);
        game.setEnemyBoard(enemyBoard);
    }

    //out
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

    //out?
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

    //Out
    public void drawGameBoard(Game game) {
        String[][] enemyBoard = game.getEnemyBoard();
        String[][] playerBoard = game.getPlayerBoard();
        System.out.println(".......PLAYER_BOARD.....................");
        printBoard(playerBoard, game);
        System.out.println(".......ENEMY_BOARD......................");
        printBoard(enemyBoard, game);
    }

    //OUT
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

    //OUT
    public void markTheShot(Game game, User user, String response) throws ParseException {
        List<String> columnList = game.getColumns();
        List<Long> rowList = game.getRows();
        List<Event> eventList = utilityService.setGameEventListFromStatus(response, game);
        String[][] playerBoard = game.getPlayerBoard();
        String[][] enemyBoard = game.getEnemyBoard();
        for (Event ev : eventList) {
            Coordinate eventCoordinate = ev.getCoordinate();
            String col = eventCoordinate.getColumn();
            long row = eventCoordinate.getRow();
            int colIndex = columnList.indexOf(col);
            int rowIndex = rowList.indexOf(row);
            if (!ev.getUserId().equals(user.getUserId())) {
                if (ev.isHit()) {
                    playerBoard[rowIndex][colIndex] = GameConstants.HIT_SYMBOL;
                    game.setPlayerBoard(playerBoard);
                } else {
                    playerBoard[rowIndex][colIndex] = GameConstants.MISS_SYMBOL;
                    game.setPlayerBoard(playerBoard);

                }


            }
            if (ev.getUserId().equals(user.getUserId())) {
                if (ev.isHit()) {
                    enemyBoard[rowIndex][colIndex] = GameConstants.HIT_SYMBOL;
                    game.setEnemyBoard(enemyBoard);
                } else {
                    enemyBoard[rowIndex][colIndex] = GameConstants.MISS_SYMBOL;
                    game.setEnemyBoard(enemyBoard);

                }
            }

        }
    }

    //OUT

    public void playVersionTwo(Game game, User user, Scanner scanner) throws IOException, ParseException, InterruptedException {

        String statusResponseString = utilityService.getStatusString(game.getGameId()); // gaunu status JSON paversta i stringa
        String statusString = utilityService.getStatusFromResponse(statusResponseString); // is JSON psiimu status : " ..something.."
        String nextPlayerId = utilityService.getNextPlayersTurnFromStatus(statusResponseString);
        String shot;
        String winnerId = utilityService.getWinnerId(statusResponseString);

        while (!GameConstants.FINISHED.equals(statusString)) {  //while ciklo salyga jeigu status nelygu finished

            while (winnerId.length() == 0 && !nextPlayerId.equals(user.getUserId())) {
                statusResponseString = utilityService.getStatusString(game.getGameId());
                statusString = utilityService.getStatusFromResponse(statusResponseString);
                if (GameConstants.FINISHED.equals(statusString)) {
                    break;
                }
                oponentsTurn(game, user);
                statusResponseString = utilityService.getStatusString(game.getGameId());
                winnerId = utilityService.getWinnerId(statusResponseString);
                if (GameConstants.FINISHED.equals(statusString) || winnerId.length() != 0) {
                    if (user.getUserId().equals(winnerId)) {
                        System.out.println("YOU WIN!!!!");
                        break;
                    } else {
                        System.out.println("YOU LOSE!!!");
                        break;
                    }
                }
                nextPlayerId = utilityService.getNextPlayersTurnFromStatus(utilityService.getStatusString(game.getGameId()));
                statusString = utilityService.getStatusFromResponse(statusResponseString);
                if (nextPlayerId.equals(user.getUserId())) {
                    break;
                }
            }

            if (winnerId.length() != 0) {
                break;
            }
            statusResponseString = utilityService.getStatusString(game.getGameId());
            markTheShot(game, user, statusResponseString);
            drawGameBoard(game);
            utilityService.shotHistory(game);

            System.out.println("Your turn. Make your shot. E.g. T5");
            shot = scanner.nextLine().toUpperCase();
            String shotResponse = gameService.shoot(game, user, shot);

            nextPlayerId = utilityService.getNextPlayersTurnFromStatus(shotResponse);
            statusString = utilityService.getStatusFromResponse(shotResponse);
            winnerId = utilityService.getWinnerId(shotResponse);
            if (GameConstants.FINISHED.equals(statusString) || winnerId.length() != 0) {
                if (user.getUserId().equals(winnerId)) {
                    System.out.println("YOU WIN!!!!");
                } else {
                    System.out.println("YOU LOSE!!!");
                    break;
                }

            }
        }

    }


    public void oponentsTurn(Game game, User user) throws IOException, ParseException, InterruptedException {
        String statusResponse = utilityService.getStatusString(game.getGameId());
        String winnerId = utilityService.getWinnerId(statusResponse);
        List<Event> eventListFromGame = game.getListOfEvents();
        List<Event> eventListFromStatus = utilityService.setGameEventListFromStatus(statusResponse, game);
        if (eventListFromGame.size() < eventListFromStatus.size() && winnerId.length() == 0) {
            markTheShot(game, user, statusResponse);
            drawGameBoard(game);
            utilityService.shotHistory(game);

        } else {
            Thread.sleep(1000);
            System.out.print("....");
        }
    }

    public void playersTurn(Game game, User user, Scanner scanner) throws IOException, ParseException {

        String statusResponseString = utilityService.getStatusString(game.getGameId());
        markTheShot(game, user, statusResponseString);
        drawGameBoard(game);
        utilityService.shotHistory(game);
        System.out.println("Your turn. Make your shot. E.g. T5");
        String shot = scanner.nextLine().toUpperCase();
        String shotResponse = gameService.shoot(game, user, shot);
        game.setNextTurnForUserId(utilityService.getNextPlayersTurnFromStatus(shotResponse));
        game.setStatus(utilityService.getStatusFromResponse(shotResponse));
        game.setWinnerId(utilityService.getWinnerId(shotResponse));


    }

}

