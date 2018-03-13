package lt.envy.battleships.entity;

import java.util.ArrayList;
import java.util.List;

public class Game {

    //TODO think of what atributes Game class will have

    //TODO Game should have a Board (either take it from response or hardcode it.
    //TODO gameId --
    //TODO status
    //TODO an array or list of Events
    //TODO a field for declaring a winner
    //TODO field for nextPlayersTurn

    //TODO think of what methods this classes' service will hold
    //TODO methods to join a user to the game
    //TODO method for taking a turn

    String gameId;
    String status;
    List<Event> listOfEvents = new ArrayList<>();
    String winnerId;
    String nextTurnForUserId;

    public Game(String gameId) {
        this.gameId = gameId;
    }
}
