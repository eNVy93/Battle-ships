package lt.envy.battleships.utils;

import lt.envy.battleships.entity.*;
import lt.envy.battleships.service.GameLogicService;
import lt.envy.battleships.service.GameService;
import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameUtilityService {
    private GameLogicService logicService = new GameLogicService();
    private GameService gameService = new GameService();

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

    public String parseShipyardToUrl(List<Ship> shipyard) {
        StringBuilder builder = new StringBuilder();
        for (Ship ship : shipyard) {
            String startLetter = ship.getStartCoordinate().getColumn();
            String endLetter = ship.getEndCoordinate().getColumn();
            int startDigit = ship.getStartCoordinate().getRow();
            int endDigit = ship.getEndCoordinate().getRow();
            builder.append(startLetter).append(startDigit).append("-").append(endLetter).append(endDigit).append("!");

        }
        System.out.println(builder);
        return builder.toString();
    }

    public void waitForGameStatusChange(GameData game, String status) throws InterruptedException, IOException, ParseException {
        System.out.println("Waiting for other player");
        while (!status.equals(gameService.status(game.getGameId()).getStatus())) {
            Thread.sleep(3333);
            System.out.print(".......");
        }
        System.out.println("");
        System.out.println(gameService.status(game.getGameId()).getStatus());
    }

    public void shotHistory(GameData game) throws ParseException, IOException {

        List<Event> eventList = game.getListOfEvents();
        List<Event> lastThreeEvents = eventList.subList(Math.max(eventList.size() - 3, 0), eventList.size());
        if (lastThreeEvents.size() == 0) {

        }
        System.out.println("...SHOT HISTORY. LAST 3 SHOTS...");
        for (Event e :
                lastThreeEvents) {
            System.out.println("\n" + e);
        }
        System.out.println("..................................");

    }

    public List<Ship> shipLoader(List<Ship> shipyard, GameData game) {
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

        Ship carrier = logicService.generateShip(game, new Coordinate("L", 8), 4, 'h');
        logicService.addShipToShipyard(shipyard, carrier);

        Ship battleCruiser = logicService.generateShip(game, new Coordinate("I", 4), 3, 'v');
        logicService.addShipToShipyard(shipyard, battleCruiser);

        Ship battleCruiser2 = logicService.generateShip(game, new Coordinate("R", 1), 3, 'v');
        logicService.addShipToShipyard(shipyard, battleCruiser2);

        Ship cruiser = logicService.generateShip(game, new Coordinate("I", 1), 2, 'h');
        logicService.addShipToShipyard(shipyard, cruiser);

        Ship cruiser2 = logicService.generateShip(game, new Coordinate("E", 3), 2, 'v');
        logicService.addShipToShipyard(shipyard, cruiser2);

        Ship cruiser3 = logicService.generateShip(game, new Coordinate("R", 6), 2, 'h');
        logicService.addShipToShipyard(shipyard, cruiser3);

        Ship boat = logicService.generateShip(game, new Coordinate("O", 3), 1, 'h');
        logicService.addShipToShipyard(shipyard, boat);

        Ship boat1 = logicService.generateShip(game, new Coordinate("O", 5), 1, 'h');
        logicService.addShipToShipyard(shipyard, boat1);

        Ship boat2 = logicService.generateShip(game, new Coordinate("K", 9), 1, 'h');
        logicService.addShipToShipyard(shipyard, boat2);

        Ship boat3 = logicService.generateShip(game, new Coordinate("A", 8), 1, 'h');
        logicService.addShipToShipyard(shipyard, boat3);


        return shipyard;
    }


}
