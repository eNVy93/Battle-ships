package lt.envy.battleships.entity;

import java.util.Date;

public class Event {

    //TODO event will hold a Coordinate
    //TODO event will hold a date with Date class
    //TODO event will hold a userId;
    //TODO event will hold a boolean for hit

    private Coordinate coordinate;
    private Date date;
    private String userId;
    private boolean hit;

    public Event(Coordinate coordinate, Date date, String userId, boolean hit) {
        this.coordinate = coordinate;
        this.date = date;
        this.userId = userId;
        this.hit = hit;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
