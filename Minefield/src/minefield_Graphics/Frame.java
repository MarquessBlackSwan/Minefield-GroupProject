package minefield_Graphics;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    private final int square;



    public Frame (int square) {

        this.square = square;
        setUpFrame();
    }

    private void setUpFrame() {

        super.setBounds(new Rectangle(findWidth(), findHeight()));
        super.setLayout(new BorderLayout());
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setLocationRelativeTo(null);
        this.addComponent();

        super.setVisible(true);
    }

    private void addComponent () {


    }

    private int findHeight () {
        return (square*35)+150;
    }

    private int findWidth () {
        return (square*35)+100;
    }
}
