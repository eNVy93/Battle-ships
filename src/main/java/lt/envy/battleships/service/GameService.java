package lt.envy.battleships.service;


import lt.envy.battleships.entity.Event;
import lt.envy.battleships.entity.Game;
import lt.envy.battleships.entity.User;
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

    MyUtilityService utilityService = new MyUtilityService();

    public Game joinUser(User user) throws IOException, ParseException {

        StringBuilder joinUserURL = new StringBuilder(URLConstants.JOIN_USER_METHOD);
        joinUserURL.append("user_id=").append(user.getUserId());

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
        for (Object o: JSONevents){
            eventList.add((Event)o);
        }
        JSONArray JSONcolumns = (JSONArray) jsonGame.get("columns");
        List<String> columns = new ArrayList<>();
        for(Object o: JSONcolumns){
            columns.add((String)o);
        }
        JSONArray JSONrows = (JSONArray) jsonGame.get("rows");
        List<Integer> rows = new ArrayList<>();
        for(Object o: JSONrows){
            rows.add((Integer)o);
        }
        String gameId = (String) jsonGame.get("id");
        String nextTurnForUserId = (String) jsonGame.get("nextTurnForUserId");
        String status = (String) jsonGame.get("status");
        String winnderUserId = (String) jsonGame.get("winnerUserId");

        return new Game(gameId,status,eventList,winnderUserId,nextTurnForUserId,columns,rows);

    }

}
