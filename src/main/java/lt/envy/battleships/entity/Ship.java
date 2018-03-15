package lt.envy.battleships.entity;

public class Ship {

    private Coordinate startCoordinate;
    private Coordinate endCoordinate;

    public Ship(Coordinate startCoordinate, Coordinate endCoordinate) {
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
    }

    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    public void setStartCoordinate(Coordinate startCoordinate) {
        this.startCoordinate = startCoordinate;
    }

    public Coordinate getEndCoordinate() {
        return endCoordinate;
    }

    public void setEndCoordinate(Coordinate endCoordinate) {
        this.endCoordinate = endCoordinate;
    }

    @Override
    public String toString() {
        return "Start coordinates: \n" +
                "\tRow: " + startCoordinate.getRow() +
                "\n\tCol: " + startCoordinate.getColumn() +
                "\nEnd coordinates: \n" +
                "\tRow: " + endCoordinate.getRow() +
                "\n\tCol: " + endCoordinate.getColumn() + "\n";
    }
}
