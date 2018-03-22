package lt.envy.battleships.service;

import lt.envy.battleships.entity.User;
import lt.envy.battleships.utils.GameUtilityService;
import lt.envy.battleships.utils.URLConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class UserService extends WebService {

    private GameUtilityService utilityService = new GameUtilityService();

    public User createUser(String name, String email) throws IOException, ParseException {

        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append(URLConstants.CREATE_USER_METHOD);
        url.append("name=").append(name).append("&");
        url.append("email=").append(email);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url.toString());

        HttpResponse response = client.execute(getRequest);

        String responseAsString = utilityService.convertInputStreamToString(response.getEntity().getContent());

        return convertJsonToUser(responseAsString);
    }

    private User convertJsonToUser(String responce) throws ParseException {
        JSONParser parser = new JSONParser();

        JSONObject jsonUser = (JSONObject) parser.parse(responce);
        String userName = (String) jsonUser.get("name");
        String userEmail = (String) jsonUser.get("email");
        String userId = (String) jsonUser.get("id");
        return new User(userId, userName, userEmail);
    }

}
