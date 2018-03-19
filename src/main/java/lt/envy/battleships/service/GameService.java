package lt.envy.battleships.service;


import lt.envy.battleships.UserInterface;
import lt.envy.battleships.entity.*;
import lt.envy.battleships.utils.GameConstants;
import lt.envy.battleships.utils.GameUtilityService;
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
import java.util.Scanner;

public class GameService {

    private GameUtilityService utilityService = new GameUtilityService();
    UserInterface ui = new UserInterface();

    public Game joinUser(String userId) throws IOException, ParseException {

        StringBuilder joinUserURL = new StringBuilder(URLConstants.SERVER_URL);
        joinUserURL.append(URLConstants.JOIN_USER_METHOD).append("user_id=").append(userId);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(joinUserURL.toString());

        HttpResponse joinUserResponse = httpClient.execute(getRequest);

        String joinUserResponseString = utilityService.convertInputStreamToString(joinUserResponse.getEntity().getContent());

        return convertJsonToGame(joinUserResponseString);

    }

    public Game convertJsonToGame(String response) throws ParseException {
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

    public void addShipToShipyard(Game game, Ship ship) {
        List<Ship> shipyard = game.getShipyard();
        shipyard.add(ship);
        game.setShipyard(shipyard);
    }

    // take a result
    public void sendShips(Game game, String shipyardCoordinates, String playerId) throws IOException {

        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append("setup?").append("game_id=").append(game.getGameId())
                .append("&user_id=").append(playerId).append("&data=").append(shipyardCoordinates);

        HttpClient client = HttpClientBuilder.create().build();

        HttpGet deployedShips = new HttpGet(url.toString());
        HttpResponse response = client.execute(deployedShips);

        // currently not needed
        //utilityService.convertInputStreamToString(response.getEntity().getContent());
    }

    //TODO create method to update/redraw the board. (Maybe in UI class)

    //TODO method to make a turn
// shoot should return a response to work with
    public String shoot(Game game, User user, String target) throws IOException, ParseException {

        String statusResponse = utilityService.getStatusString(game.getGameId());
        utilityService.setGameEventListFromStatus(statusResponse, game);
        StringBuilder sb = new StringBuilder(URLConstants.SERVER_URL);
        sb.append("turn?").append("game_id=").append(game.getGameId())
                .append("&user_id=").append(user.getUserId()).append("&data=").append(target);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet shotRequest = new HttpGet(sb.toString());
        HttpResponse response = client.execute(shotRequest);
        utilityService.setGameEventListFromStatus(statusResponse, game);

        return utilityService.convertInputStreamToString(response.getEntity().getContent());

    }

    public void play(Game game, User user, Scanner scanner) throws IOException, ParseException, InterruptedException {

        String statusResponse = utilityService.getStatusString(game.getGameId());
        ui.drawGameBoard(game);
        String winnerId = utilityService.getWinnerId(statusResponse);
        while (winnerId.length() == 0) {
            utilityService.shotHistory(game);
            System.out.println("Take your aim. Enter a coordinate to shoot. For example T6");
            String shot = scanner.nextLine();
            String response = shoot(game, user, shot);
            markTheShot(game, user, response);
            ui.drawGameBoard(game);
            statusResponse = utilityService.getStatusString(game.getGameId());
            winnerId = utilityService.getWinnerId(statusResponse);

            if (GameConstants.FINISHED.equals(utilityService.getStatusFromResponse(statusResponse))) {
                System.out.println("WE HAVE A WINNER");
                System.out.println(utilityService.getWinnerId(statusResponse));
                break;
            }

        }
    }

    public void markTheShot(Game game, User user, String response) throws ParseException {
        List<String> columnList = game.getColumns();
        List<Long> rowList = game.getRows();
        // events 0
//        List<Event> eventList = game.getListOfEvents();
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

}
