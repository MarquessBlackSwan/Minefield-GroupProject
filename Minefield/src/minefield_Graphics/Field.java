package minefield_Graphics;

import java.util.Random;

public class Field {

    private final Cell[][] cells;
    private final int[][] template;
    private final Random rand;
    private final int length;

    private int revealedCount = 0;
    private int flaggedBombs  = 0;
    private final int totalBombs;

    // Callback verso Frame
    public interface GameListener {
        void onGameOver();
        void onVictory();
        void onBombCountChanged(int remaining);
    }

    private GameListener listener;

    public Field(int length, int bombNum) {
        this.length     = length;
        this.totalBombs = bombNum;
        this.rand       = new Random();
        this.cells      = new Cell[length][length];
        this.template   = setUpTemplate(bombNum);
        setUpCells();
    }

    public void setGameListener(GameListener l) { this.listener = l; }

    // --- Setup griglia ---

    private int[][] setUpTemplate(int bombNum) {
        int[][] t = new int[length][length];

        // Piazza le bombe (evita duplicati contando slot liberi)
        int placed = 0;
        while (placed < bombNum) {
            int ry = rand.nextInt(length);
            int rx = rand.nextInt(length);
            if (t[ry][rx] != 9) {
                t[ry][rx] = 9;
                placed++;
            }
        }

        // Calcola i numeri per ogni cella non-bomba
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                if (t[y][x] != 9) {
                    t[y][x] = countAdjacentBombs(t, x, y);
                }
            }
        }

        return t;
    }

    private int countAdjacentBombs(int[][] t, int x, int y) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dy == 0 && dx == 0) continue;
                int ny = y + dy;
                int nx = x + dx;
                if (ny >= 0 && ny < length && nx >= 0 && nx < length) {
                    if (t[ny][nx] == 9) count++;
                }
            }
        }
        return count;
    }

    private void setUpCells() {
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < length; col++) {
                cells[row][col] = new Cell(template[row][col]);
            }
        }
    }

    // --- Azioni giocatore ---

    /**
     * Scopre una cella. Se è una bomba → game over.
     * Se è 0 → flood fill ricorsivo.
     * @return true se la partita continua
     */
    public boolean revealCell(int col, int row) {
        Cell cell = cells[row][col];

        if (!cell.isHidden()) return true;

        if (cell.isBomb()) {
            cell.revealBomb();
            revealAllBombs();
            if (listener != null) listener.onGameOver();
            return false;
        }

        floodReveal(col, row);
        checkVictory();
        return true;
    }

    private void floodReveal(int col, int row) {
        if (col < 0 || col >= length || row < 0 || row >= length) return;
        Cell cell = cells[row][col];
        if (!cell.isHidden() || cell.isFlagged()) return;

        cell.reveal();
        revealedCount++;

        if (cell.getValue() == 0) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dy != 0 || dx != 0) {
                        floodReveal(col + dx, row + dy);
                    }
                }
            }
        }
    }

    /**
     * Piazza o rimuove una bandierina.
     */
    public void toggleFlag(int col, int row) {
        Cell cell = cells[row][col];
        if (cell.isRevealed()) return;

        boolean wasFlagged = cell.isFlagged();
        cell.toggleFlag();

        if (cell.isFlagged()) {
            flaggedBombs += cell.isBomb() ? 1 : 0;
        } else {
            flaggedBombs -= wasFlagged && cell.isBomb() ? 1 : 0;
        }

        if (listener != null) {
            // "bombe rimanenti" = totale - bandiere piazzate (qualunque cella)
            int flagsPlaced = countFlags();
            listener.onBombCountChanged(totalBombs - flagsPlaced);
        }
    }

    private int countFlags() {
        int count = 0;
        for (int row = 0; row < length; row++)
            for (int col = 0; col < length; col++)
                if (cells[row][col].isFlagged()) count++;
        return count;
    }

    private void revealAllBombs() {
        for (int row = 0; row < length; row++)
            for (int col = 0; col < length; col++)
                if (cells[row][col].isBomb()) cells[row][col].revealBomb();
    }

    private void checkVictory() {
        int safeCells = length * length - totalBombs;
        if (revealedCount >= safeCells) {
            if (listener != null) listener.onVictory();
        }
    }

    // --- Getter ---

    public Cell[][] getCells()  { return cells; }
    public int getLength()      { return length; }
    public int getTotalBombs()  { return totalBombs; }
}
