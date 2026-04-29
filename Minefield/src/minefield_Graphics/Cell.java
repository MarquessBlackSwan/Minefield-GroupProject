package minefield_Graphics;

import javax.swing.*;

public class Cell extends JPanel {

    private final int is;
    private final ImageIcon icon;

    public Cell (int is) {
        this.is = is;

        this.icon = (is != 0 && is <= 9) ? new ImageIcon("Minefield/assets/"+is) : null;
    }


}
