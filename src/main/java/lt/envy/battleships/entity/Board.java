package lt.envy.battleships.entity;

public class Board {

    private String[][] board;

    public Board(String[][] board) {
        this.board = board;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }
}
