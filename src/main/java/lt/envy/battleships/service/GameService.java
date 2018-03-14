package lt.envy.battleships.service;


import lt.envy.battleships.entity.Board;
import lt.envy.battleships.entity.Event;
import lt.envy.battleships.entity.Game;
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
    private void deployShips(Game game) {
        game.getColumns();
        game.getRows();
    }

}
