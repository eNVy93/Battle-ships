package lt.envy.battleships.utils;

import lt.envy.battleships.entity.Coordinate;
import lt.envy.battleships.entity.Event;
import lt.envy.battleships.entity.Game;
import lt.envy.battleships.entity.Ship;
import lt.envy.battleships.service.GameService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameUtilityService {
    static GameService gameService = new GameService();

    public String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        return writer.toString();
    }

    public String validateCoordinateInput(Scanner scanner) {
        String coordinate;
        boolean flow = true;
        while (flow) {
            coordinate = scanner.nextLine();
            char firstSymbol = Character.toUpperCase(coordinate.charAt(0));
            char secondSymbol = coordinate.charAt(1);
            char thirdSymbol = Character.toLowerCase(coordinate.charAt(2));

            if (coordinate.length() != 3) {
                System.out.println("Please enter coordinate correctly. Format Column-Row-Orientation.\n" +
                        " Ex .: A5h translates to A - column; 5 - row; h -horizontal");
            } else if (!Character.isLetter(firstSymbol)) {
                System.out.println("First symbol is not a letter");
            } else if (!Character.isDigit(secondSymbol)) {
                System.out.println("Second symbol must be a digit!!");
            } else {
                switch (thirdSymbol) {
                    case 'h': {
                        flow = false;
                        String result = Character.toString(firstSymbol).toUpperCase() + secondSymbol + thirdSymbol;
                        System.out.println(result);
                        return result;
                    }
                    case 'v': {
                        flow = false;
                        String result = Character.toString(firstSymbol).toUpperCase() + secondSymbol + thirdSymbol;
                        System.out.println(result);
                        return result;
                    }
                    default: {
                        System.out.println("Wrong input");
                        break;
                    }
                }
            }

        }

        return null;
    }

    public Coordinate convertInputStringToCoordinate(String coordinate) {

        String col = Character.toString(coordinate.charAt(0));
        int row = Character.getNumericValue(coordinate.charAt(1));
        return new Coordinate(col, row);
    }

    public char getOrientationCharFromInputString(String input) {
        return input.charAt(2);
    }



    public String parseShipyardToUrl(Game game) {
        List<Ship> shipyard = game.getShipyard();
        StringBuilder builder = new StringBuilder();
        for (Ship ship : shipyard) {
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

    public String getStatusFromResponse(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject gameStatus = (JSONObject) parser.parse(response);
        return (String) gameStatus.get("status");
    }

    public void waitForGameStatusChange(Game game, String status) throws InterruptedException, IOException, ParseException {
        System.out.println("Waiting for other player");
        while (!status.equals(getStatusFromResponse(getStatusString(game.getGameId())))) {
            Thread.sleep(3333);
            System.out.print(".......");
        }
        System.out.println("");
        System.out.println(getStatusFromResponse(getStatusString(game.getGameId())));
    }

    public String getStatusString(String gameId) throws IOException {
        StringBuilder statusURL = new StringBuilder(URLConstants.SERVER_URL);
        statusURL.append(URLConstants.GAME_STATUS_METHOD).append("game_id=").append(gameId);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getStatus = new HttpGet(statusURL.toString());

        HttpResponse response = client.execute(getStatus);

        return convertInputStreamToString(response.getEntity().getContent());

    }

    public List<Event> setGameEventListFromStatus(String response, Game game) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject gameStatus = (JSONObject) parser.parse(response);
        JSONArray eventArray = (JSONArray) gameStatus.get("events");
        List<Event> eventList = new ArrayList<>();
        for (Object o : eventArray) {
//            Event singleEvent = (Event) o;
            JSONObject eventObject = (JSONObject) o;
            long date = (long) eventObject.get("date");
            String userId = (String) eventObject.get("userId");
            boolean isHit = (boolean) eventObject.get("hit");

            JSONObject coordinateObject = (JSONObject) eventObject.get("coordinate");
            String column = (String) coordinateObject.get("column");
            long row = (Long) coordinateObject.get("row");

            Coordinate coordinate = new Coordinate(column, (int) row);
            eventList.add(new Event(coordinate, date, userId, isHit));

        }
        game.setListOfEvents(eventList);
        return eventList;

    }

    public String getNextPlayersTurnFromStatus(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject gameStatus = (JSONObject) parser.parse(response);
        return (String) gameStatus.get("nextTurnForUserId");
    }

    public String getWinnerId(String response) throws ParseException {

        JSONParser parser = new JSONParser();
        JSONObject gameStatus = (JSONObject) parser.parse(response);

        return (String) gameStatus.get("winnerUserId");

    }

    public void shotHistory(Game game) throws ParseException, IOException {
        String statusResponse = getStatusString(game.getGameId());

        List<Event> eventList = setGameEventListFromStatus(statusResponse, game);
        List<Event> lastThreeEvents = eventList.subList(Math.max(eventList.size() - 3, 0), eventList.size());
        if(lastThreeEvents.size()==0){

        }
        System.out.println("...SHOT HISTORY. LAST 3 SHOTS...");
        for (Event e :
                lastThreeEvents) {
            System.out.println("\n" + e);
        }
        System.out.println("..................................");

    }
// shouldnt take Game parameter. Return a list
    public void shipLoader(Game game) {
        List<String> cols = new ArrayList<>();
        {
            cols.add("K");
            cols.add("I");
            cols.add("L");
            cols.add("O");
            cols.add("M");
            cols.add("E");
            cols.add("T");
            cols.add("R");
            cols.add("A");
            cols.add("S");
        }
        List<Long> rows = new ArrayList<>();
        {
            rows.add(0L);
            rows.add(1L);
            rows.add(2L);
            rows.add(3L);
            rows.add(4L);
            rows.add(5L);
            rows.add(6L);
            rows.add(7L);
            rows.add(8L);
            rows.add(9L);
        }

        Ship carrier = gameService.generateShip(game, new Coordinate("L", 8), 4, 'h');
        gameService.addShipToShipyard(game, carrier);

        Ship battleCruiser = gameService.generateShip(game, new Coordinate("I", 4), 3, 'v');
        gameService.addShipToShipyard(game, battleCruiser);

        Ship battleCruiser2 = gameService.generateShip(game, new Coordinate("R", 1), 3, 'v');
        gameService.addShipToShipyard(game, battleCruiser2);

        Ship cruiser = gameService.generateShip(game, new Coordinate("I", 1), 2, 'h');
        gameService.addShipToShipyard(game, cruiser);

        Ship cruiser2 = gameService.generateShip(game, new Coordinate("E", 3), 2, 'v');
        gameService.addShipToShipyard(game, cruiser2);

        Ship cruiser3 = gameService.generateShip(game, new Coordinate("R", 6), 2, 'h');
        gameService.addShipToShipyard(game, cruiser3);

        Ship boat = gameService.generateShip(game, new Coordinate("O", 3), 1, 'h');
        gameService.addShipToShipyard(game, boat);

        Ship boat1 = gameService.generateShip(game, new Coordinate("O", 5), 1, 'h');
        gameService.addShipToShipyard(game, boat1);

        Ship boat2 = gameService.generateShip(game, new Coordinate("K", 9), 1, 'h');
        gameService.addShipToShipyard(game, boat2);

        Ship boat3 = gameService.generateShip(game, new Coordinate("A", 8), 1, 'h');
        gameService.addShipToShipyard(game, boat3);


    }


}
