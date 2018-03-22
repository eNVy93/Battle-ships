package lt.envy.battleships.service;

import lt.envy.battleships.entity.Coordinate;
import lt.envy.battleships.entity.Event;
import lt.envy.battleships.entity.GameData;
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

public class GameService extends WebService {

    public static int[] SHIPYARD_CONFIGURATION = {4,3,3,2,2,2,1,1,1,1};

    public GameData join(String userId) throws IOException, ParseException {

        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append(URLConstants.JOIN_USER_METHOD).append("user_id=").append(userId);

        return performRequest(url.toString());
    }

    public GameData setup(String gameId, String playerId, String shipyardCoordinates) throws IOException, ParseException {

        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append(URLConstants.SETUP_GAME_METHOD).append("game_id=").append(gameId)
                .append("&user_id=").append(playerId).append("&data=").append(shipyardCoordinates);

        return performRequest(url.toString());

    }

    public GameData status(String gameId) throws IOException, ParseException {

        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append(URLConstants.GAME_STATUS_METHOD).append("game_id=").append(gameId);

        return performRequest(url.toString());
    }

    public GameData turn(String gameId, String userId, String target) throws IOException, ParseException {

        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append(URLConstants.GAME_TURN_METHOD).append("game_id=").append(gameId)
                .append("&user_id=").append(userId).append("&data=").append(target);

        return performRequest(url.toString());

    }


    public GameData performRequest(String url) throws IOException, ParseException {

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);

        HttpResponse response = httpClient.execute(getRequest);
        String responseString = convertInputStreamToString(response.getEntity().getContent());
        if (response.getStatusLine().getStatusCode() == 200) {
            return convertJsonToGameData(responseString);
        }
        return null;
    }

    public GameData convertJsonToGameData(String responseBody) throws ParseException {

        JSONParser parser = new JSONParser();
        JSONObject jsonGame = (JSONObject) parser.parse(responseBody);

        JSONArray JSONevents = (JSONArray) jsonGame.get("events");
        List<Event> eventList = new ArrayList<>();
        for (Object o : JSONevents) {
            JSONObject eventObj = (JSONObject) o;
            JSONObject coordinateObj = (JSONObject) eventObj.get("coordinate");

            String column = (String) coordinateObj.get("column");
            long row = (long) coordinateObj.get("row");

            long date = (long) eventObj.get("date");
            String userId = (String) eventObj.get("userId");
            boolean hit = (boolean) eventObj.get("hit");
            eventList.add(new Event(new Coordinate(column, (int) row), date, userId, hit));
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

        return new GameData(gameId, status, eventList, winnerUserId, nextTurnForUserId, columns, rows);

    }
}
