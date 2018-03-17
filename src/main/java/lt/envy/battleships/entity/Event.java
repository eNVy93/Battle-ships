package lt.envy.battleships.entity;

import java.util.Date;

public class Event {


    private Coordinate coordinate;
    private long date;
    private String userId;
    private boolean hit;

    public Event(Coordinate coordinate, long date, String userId, boolean hit) {
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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
