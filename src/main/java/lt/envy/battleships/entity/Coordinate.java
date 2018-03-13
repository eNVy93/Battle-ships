package lt.envy.battleships.entity;

public class Coordinate {

    private String column;
    private int row;

    public Coordinate(String column, int row) {
        this.column = column;
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
