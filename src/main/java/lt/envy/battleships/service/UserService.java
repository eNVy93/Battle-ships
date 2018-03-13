package lt.envy.battleships.service;

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

public class UserService {

    private MyUtilityService utilityService = new MyUtilityService();

    public User createUser(String name, String email) throws IOException, ParseException {

        //Build a URL
        StringBuilder url = new StringBuilder(URLConstants.SERVER_URL);
        url.append(URLConstants.CREATE_USER_METHOD);
        url.append("name=").append(name).append("&");
        url.append("email=").append(email);

        //Initiate http client
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url.toString());

        //Tell the client to send the request and save response to a variable
        HttpResponse response = client.execute(getRequest);

        //Convert responses' InputStream to String
        String responseAsString = utilityService.convertInputStreamToString(response.getEntity().getContent());

        //Then return new user
        return convertJsonToUser(responseAsString);

    }
    private User convertJsonToUser(String responce) throws ParseException {
        JSONParser parser = new JSONParser();

        JSONObject jsonUser = (JSONObject)parser.parse(responce);
        String userName = (String) jsonUser.get("name");
        String userEmail = (String) jsonUser.get("email");
        String userId = (String) jsonUser.get("id");
        return new User(userId, userName, userEmail);
    }

}
