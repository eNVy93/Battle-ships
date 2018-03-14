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

    private String gameId;
    private String status;
    private List<Event> listOfEvents;
    private String winnerId;
    private String nextTurnForUserId;
    private List<String> columns;
    private List<Long> rows;


    public Game(String gameId, String status, String winnerId, String nextTurnForUserId) {
        this.gameId = gameId;
        this.status = status;
        this.winnerId = winnerId;
        this.nextTurnForUserId = nextTurnForUserId;
    }

    public Game(String gameId, String status, List<Event> listOfEvents, String winnerId, String nextTurnForUserId, List<String> columns, List<Long> rows) {
        this.gameId = gameId;
        this.status = status;
        this.listOfEvents = listOfEvents;
        this.winnerId = winnerId;
        this.nextTurnForUserId = nextTurnForUserId;
        this.columns = columns;
        this.rows = rows;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Long> getRows() {
        return rows;
    }

    public void setRows(List<Long> rows) {
        this.rows = rows;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Event> getListOfEvents() {
        return listOfEvents;
    }

    public void setListOfEvents(List<Event> listOfEvents) {
        this.listOfEvents = listOfEvents;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getNextTurnForUserId() {
        return nextTurnForUserId;
    }

    public void setNextTurnForUserId(String nextTurnForUserId) {
        this.nextTurnForUserId = nextTurnForUserId;
    }
}
