package lt.envy.battleships.service;

import lt.envy.battleships.entity.Game;
import lt.envy.battleships.entity.User;
import lt.envy.battleships.utils.MyUtilityService;
import lt.envy.battleships.utils.URLConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class GameService {

    MyUtilityService utilityService = new MyUtilityService();

    public String joinUser(User user) throws IOException, ParseException {

        // Generate a URL to join user
        StringBuilder joinUserURL = new StringBuilder(URLConstants.SERVER_URL);
        joinUserURL.append(URLConstants.JOIN_USER_METHOD);
        joinUserURL.append("user_id=").append(user.getUserId());

        // Initiate HTTP client
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(joinUserURL.toString());

        // Tell client to send request and save response to a variable
        HttpResponse response = client.execute(getRequest);

        // Convert responses' input stream to string
        String responseAsString = utilityService.convertInputStreamToString(response.getEntity().getContent());

        String gameId = getGameId(responseAsString);
        // TODO if flow controll
        //TODO if gameId != null execute(request) ------- might not work, because there is already an ID
        //TODO else createGame and execute(request)

        return gameId;
    }

    public Game createGame(String id1, String id2) {
        if (id1 != null && id2 != null && id1.equals(id2)) {
            return new Game(id1);
        }

        return null;

    }

    // TODO implement my methods to store data to Game object; and rename this method below
    public String getGameStatus(String gameId) throws IOException, ParseException {
        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append(URLConstants.GAME_STATUS_METHOD).append("game_id=").append(gameId);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url.toString());

        HttpResponse response = client.execute(getRequest);

        String responseToString = utilityService.convertInputStreamToString(response.getEntity().getContent());
        return getStatus(responseToString);


    }

    private String getGameId(String response) throws ParseException {
        JSONParser parser = new JSONParser();

        JSONObject jsonGame = (JSONObject) parser.parse(response);

        return (String) jsonGame.get("id");

    }

    private String getStatus(String response) throws ParseException {

        JSONParser parser = new JSONParser();

        JSONObject jsonStatus = (JSONObject) parser.parse(response);

        return (String) jsonStatus.get("status");

    }

}
