package lt.envy.battleships.service;


import lt.envy.battleships.entity.*;
import lt.envy.battleships.utils.GameUtilityService;
import lt.envy.battleships.utils.URLConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class GameService {

    private GameUtilityService utilityService = new GameUtilityService();

    public Game joinUser(String userId) throws IOException, ParseException {

        StringBuilder joinUserURL = new StringBuilder(URLConstants.SERVER_URL);
        joinUserURL.append(URLConstants.JOIN_USER_METHOD).append("user_id=").append(userId);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(joinUserURL.toString());

        HttpResponse joinUserResponse = httpClient.execute(getRequest);

        String joinUserResponseString = utilityService.convertInputStreamToString(joinUserResponse.getEntity().getContent());

        return utilityService.convertJsonToGame(joinUserResponseString);

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


}
