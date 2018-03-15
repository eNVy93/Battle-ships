package lt.envy.battleships.service;


import lt.envy.battleships.entity.*;
import lt.envy.battleships.utils.GameStatus;
import lt.envy.battleships.utils.MyUtilityService;
import lt.envy.battleships.utils.URLConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameService {

    private MyUtilityService utilityService = new MyUtilityService();

    public Game joinUser(String userId) throws IOException, ParseException {

        StringBuilder joinUserURL = new StringBuilder(URLConstants.SERVER_URL);
        joinUserURL.append(URLConstants.JOIN_USER_METHOD).append("user_id=").append(userId);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(joinUserURL.toString());

        HttpResponse joinUserResponse = httpClient.execute(getRequest);

        String joinUserResponseString = utilityService.convertInputStreamToString(joinUserResponse.getEntity().getContent());

        return convertJsonToGame(joinUserResponseString);

    }

    private Game convertJsonToGame(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonGame = (JSONObject) parser.parse(response);

        JSONArray JSONevents = (JSONArray) jsonGame.get("events");
        List<Event> eventList = new ArrayList<>();
        for (Object o : JSONevents) {
            eventList.add((Event) o);
        }
        JSONArray JSONcolumns = (JSONArray) jsonGame.get("columns");
        List<String> columns = new ArrayList<>();
        for (Object o : JSONcolumns) {
            columns.add((String) o);
        }
        JSONArray JSONrows = (JSONArray) jsonGame.get("rows");
        List<Long> rows = new ArrayList<>();
        for (Object o : JSONrows) {
            rows.add((Long) o);
        }
        String gameId = (String) jsonGame.get("id");
        String nextTurnForUserId = (String) jsonGame.get("nextTurnForUserId");
        String status = (String) jsonGame.get("status");
        String winnerUserId = (String) jsonGame.get("winnerUserId");

        return new Game(gameId, status, eventList, winnerUserId, nextTurnForUserId, columns, rows);

    }

    public String getStatus(String gameId) throws IOException, ParseException {
        StringBuilder statusURL = new StringBuilder(URLConstants.SERVER_URL);
        statusURL.append(URLConstants.GAME_STATUS_METHOD).append("game_id=").append(gameId);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getStatus = new HttpGet(statusURL.toString());

        HttpResponse response = client.execute(getStatus);

        String responseString = utilityService.convertInputStreamToString(response.getEntity().getContent());

        return getStatusFromResponse(responseString);

    }

    public void waitForGameStatusChange(Game game) throws InterruptedException, IOException, ParseException {
        while (!GameStatus.READY_FOR_SHIPS.equals(getStatus(game.getGameId()))) {
            System.out.println("...waiting for second player");
            Thread.sleep(3333);
            System.out.println("...");
        }
//        // EXECUTE METHOD TO DEPLOY SHIPS
//            deployShips(game); // TODO implemet
    }

    public String[][] generateBoard() {
        Board board = new Board(new String[10][10]);
        String[][] arena = board.getBoard();
        for (int i = 0; i < arena.length; i++) {
            for (int j = 0; j < arena[i].length; j++) {
                arena[i][j] = ".";
            }
        }
        return arena;
    }

    public void printBoard(String[][] board, Game game) {
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

    private String getStatusFromResponse(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject gameStatus = (JSONObject) parser.parse(response);
        return (String) gameStatus.get("status");
    }

    //TODO Now its time for ship deployment
    // create a method which takes a scanner for user input. Then convert user input to coordinates and deploy a ship.
    // Maybe gather all the inputs and string build them to a URL to deploy all ships at once
    // @return new Ship ??
    public Ship deployShip(Game game, Coordinate startCoordinate, int shipSize, char orientation) {

        List<String> columnHeaders = game.getColumns();
        List<Long> rowHeaders = game.getRows();
        String[][] board = game.getPlayerBoard();
        String startCol = startCoordinate.getColumn();

        int startRow = startCoordinate.getRow();

        int columnIndex = columnHeaders.indexOf(startCol);
        //Horizontal
        // When ship is horizontal, the row is constant and we iterate trough columns
        if ('h' == orientation) {
            for (int i = 0; i < shipSize; i++) {
                // simplify this equation - columnHeaders.indexOf(startCol)+i
                //TODO make a seperate method to update board
                board[startRow][columnIndex + i] = "#";
                // method should save end coordinate for forging a URL
                if (i == (shipSize - 1)) {
//                    System.out.println("End coordinate r = " + startRow + "\nEnd col = " + columnHeaders.get(columnIndex + i));
                    return new Ship(startCoordinate, new Coordinate(columnHeaders.get(columnIndex + i), startRow));
                }
            }
        }
        //Vertical
        // W
        if ('v' == orientation) {
            for (int i = 0; i < shipSize; i++) {

                board[startRow + i][columnIndex] = "#";
                // method should save end coordinate for forging a URL
                if (i == (shipSize - 1)) {
//                    System.out.println("End coordinate r = " + (startRow + i) + "\nEnd col = " + columnHeaders.get(columnIndex));
                    return new Ship(startCoordinate, new Coordinate(columnHeaders.get(columnIndex), (startRow + i)));
                }
            }
        }
        return null;
    }

    public void addShipToShipyard(Game game, Ship ship) {
        List<Ship> shipyard = game.getShipyard();
        shipyard.add(ship);
        game.setShipyard(shipyard);
    }

    //TODO create method to update the board

    public String[][] drawShips(Game game, GameService gameService) {
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
                for(int i =0;i<shipSize;i++){
                    myBoard[startRowIndex+i][startColumnIndex] = "#";

                }
                return myBoard;

            }
            if (startRowIndex == endRowIndex){
                int shipSize = endColumnIndex - startColumnIndex;
                for(int i=0;i<shipSize;i++){
                    myBoard[startRowIndex][startColumnIndex+i] = "#";
                }
                return myBoard;
            }

        }

        return myBoard;
    }

    public String parseShipyardToUrl(Game game, GameService gameService){
        List<Ship> shipyard = game.getShipyard();
        StringBuilder builder = new StringBuilder();
        for(Ship ship: shipyard){
            // Coordinates for URL looks like &data=K0-K3!L1-L3!L5-L7!M2-M3!M5-M6!M8-M9!E7-E7!S9-S9!R8-R8!R5-R5
            String startLetter = ship.getStartCoordinate().getColumn();
            String endLetter = ship.getEndCoordinate().getColumn();
            int startDigit = ship.getStartCoordinate().getRow();
            int endDigit = ship.getEndCoordinate().getRow();
            builder.append(startLetter).append(startDigit).append("-").append(endLetter).append(endDigit).append("!");

        }
        System.out.println(builder);
        return builder.toString();
    }

    public String sendShips(Game game, String shipyardCoordinates, String playerId) throws IOException {
        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append("setup?").append("game_id=").append(game.getGameId())
                .append("&user_id=").append(playerId).append("&data=").append(shipyardCoordinates);

        HttpClient client = HttpClientBuilder.create().build();

        HttpGet deployedShips = new HttpGet(url.toString());
        HttpResponse response = client.execute(deployedShips);

        return utilityService.convertInputStreamToString(response.getEntity().getContent());
    }

}
