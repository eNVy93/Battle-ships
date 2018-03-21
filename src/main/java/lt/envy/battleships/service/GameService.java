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

    public String sendShips(Game game, String shipyardCoordinates, String playerId) throws IOException {

        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append("setup?").append("game_id=").append(game.getGameId())
                .append("&user_id=").append(playerId).append("&data=").append(shipyardCoordinates);

        HttpClient client = HttpClientBuilder.create().build();

        HttpGet deployedShips = new HttpGet(url.toString());
        HttpResponse response = client.execute(deployedShips);

        return utilityService.convertInputStreamToString(response.getEntity().getContent());
    }

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


}
