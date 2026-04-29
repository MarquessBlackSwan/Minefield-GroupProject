package minefield_Graphics;

import java.util.Random;

public class Field {

    private Cell[][] cells;
    private final int[][] template;
    private final Random rand;

    public Field (int length) {

        this.rand = new Random();
        this.cells = new Cell[length][length];
        this.template = setUpTemplate(40);
        setUpCells();
    }

    private int[][] setUpTemplate (int bombNum) {

        int[][] template = new int[cells.length][cells.length];

        for (int i = bombNum; i > 0; i--){
            template[rand.nextInt(template.length)][rand.nextInt(template.length)] = 9;
        }

        for (int y = 0; y < template.length; y++) {
            for (int x = 0; x < template.length; x++) {
                scanAroundCell(x, y);
            }
        }

        return template;
    }

    private void scanAroundCell (int x, int y) {

        if (template[y+1][x-1] == 9) {
            template[y][x] += 1;
        } else if (template[y+1][x] == 9) {
            template[y][x] += 1;
        } else if (template[y+1][x+1] == 9) {
            template[y][x] += 1;
        } else if (template[y][x-1] == 9) {
            template[y][x] += 1;
        } else if (template[y][x] == 9) {
            template[y][x] += 1;
        } else if (template[y][x+1] == 9) {
            template[y][x] += 1;
        } else if (template[y-1][x-1] == 9) {
            template[y][x] += 1;
        } else if (template[y-1][x] == 9) {
            template[y][x] += 1;
        } else if (template[y-1][x+1] == 9) {
            template[y][x] += 1;
        }
    }

    private void setUpCells () {

        for (int col = 0; col < cells.length; col++) {
            for (int row = 0; row < cells.length; row++) {
                cells[col][row] = new Cell(template[col][row]);
            }
        }
    }


}