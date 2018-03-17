package lt.envy.battleships.entity;

import java.util.ArrayList;
import java.util.List;

public class Game {

    //TODO think of what atributes Game class will have

    // Game should have a Board (either take it from response or hardcode it.
    // gameId --
    // status
    // an array or list of Events
    // a field for declaring a winner
    // field for nextPlayersTurn

    //TODO think of what methods this classes' service will hold
    //TODO method for taking a turn

    public static int[] SHIPYARD_CONFIGURATION = {4,3,3,2,2,2,1,1,1,1};

    private String gameId;
    private String status;
    private List<Event> listOfEvents;
    private String winnerId;
    private String nextTurnForUserId;
    private List<String> columns;
    private List<Long> rows;
    private String[][] playerBoard;
    private String[][] enemyBoard;
    private List<Ship> shipyard;


    public Game(String gameId, String status, List<Event> listOfEvents, String winnerId, String nextTurnForUserId, List<String> columns, List<Long> rows) {
        this.gameId = gameId;
        this.status = status;
        this.listOfEvents = listOfEvents;
        this.winnerId = winnerId;
        this.nextTurnForUserId = nextTurnForUserId;
        this.columns = columns;
        this.rows = rows;
        shipyard = new ArrayList<>();
    }

    public List<Ship> getShipyard() {
        return shipyard;
    }

    public void setShipyard(List<Ship> shipyard) {
        this.shipyard = shipyard;
    }

    public String[][] getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerBoard(String[][] playerBoard) {
        this.playerBoard = playerBoard;
    }

    public String[][] getEnemyBoard() {
        return enemyBoard;
    }

    public void setEnemyBoard(String[][] enemyBoard) {
        this.enemyBoard = enemyBoard;
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
