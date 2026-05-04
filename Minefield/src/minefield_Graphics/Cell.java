package minefield_Graphics; // con questo comando, dico al compilatore che il seguente file appartiene alla cartella "minefield_Graphics"

import javax.swing.*; // importo Swing
import java.awt.*; 
import java.awt.event.*; // con il precedente e il seguente comando, importo awt (vecchio e nuovo), utile per i colori e trasmette delle informazione da una parte all'altra

public class Cell extends JButton { // inizializzo la classe Cell che estende la classe JButton appartenente a Swing (buttone)

    // Stato della cella
    public enum State { HIDDEN, REVEALED, FLAGGED } // enum = raccolte di variabili, con valore personalizzabile

    private final int value;      // 0 = vuoto, 1-8 = numero, 9 = bomba
    private State state; // inizializzo la variabile state (tipologia: State - uno dei valori)

    // Palette colori per i numeri (classico Minesweeper)
    private static final Color[] NUMBER_COLORS = { // array che contiene degli attributi (colori)
        null,                          // 0 - non usato
        new Color(0, 0, 255),         // 1 - blu
        new Color(0, 128, 0),         // 2 - verde
        new Color(255, 0, 0),         // 3 - rosso
        new Color(0, 0, 128),         // 4 - blu scuro
        new Color(128, 0, 0),         // 5 - rosso scuro
        new Color(0, 128, 128),       // 6 - ciano
        new Color(0, 0, 0),           // 7 - nero
        new Color(128, 128, 128),     // 8 - grigio
    };
// colori aggiuntivi per le stati della cellula 
    private static final Color COLOR_HIDDEN   = new Color(189, 189, 189);
    private static final Color COLOR_REVEALED = new Color(224, 224, 224);
    private static final Color COLOR_BOMB     = new Color(255, 80, 80);
    private static final Color COLOR_FLAG     = new Color(255, 200, 50);
    private static final Color COLOR_BORDER_LIGHT = new Color(255, 255, 255);
    private static final Color COLOR_BORDER_DARK  = new Color(120, 120, 120);

    public Cell(int value) { // costruttore Cell con valore di input 
        this.value = value; // impone questo valore uguale a value
        this.state = State.HIDDEN; // stato della cella nascosto come standard

        setPreferredSize(new Dimension(35, 35)); // grandezza bottone
        setFont(new Font("Arial", Font.BOLD, 14)); // font utilizzato
        setBorderPainted(false); // contorno non colore, con true colorato
        setFocusPainted(false); // dopo aver cliccato, non si colora (con true si colorerebbe)
        setContentAreaFilled(false); // riempe l'interno del bottone (non lo colora)
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // crea un nuovo cursore per il gioco
    }
    
    // --- Metodi di stato ---

    public void reveal() { // Metodo "reveal" (rivela la casella se cliccata)
        if (state == State.HIDDEN) { // controllo se è nascosta, 
            state = State.REVEALED; // se è nascosta, la rivelo e 
            repaint(); // la aggiorno (la grafica)
        }
    }

    public void toggleFlag() { // Metodo "toggleFlag" (impone una bandiera se clicca la cella)
        if (state == State.HIDDEN) { // controllo se la cella è nascosta,
            state = State.FLAGGED; // se è nascosta, impongo una bandiera 
        } else if (state == State.FLAGGED) { // controllo se la cella è bandierata
            state = State.HIDDEN; // se è già bandierata, la nascondo
        }
        repaint(); // la aggiorno (la grafica)
    }

    public boolean isRevealed() { return state == State.REVEALED; }  // se lo stato è rivelato, restituisco un vero, altrimenti un false.
    public boolean isFlagged()  { return state == State.FLAGGED; } // se lo stato è bandierato, restituisco un vero, altrimenti un false.
    public boolean isHidden()   { return state == State.HIDDEN; } // se lo stato è nascosto, restituisco un vero, altrimenti un false.
    public boolean isBomb()     { return value == 9; } // ritorno un true se la cella è uguale a 9, altrimenti un false.
    public int getValue()       { return value; } // ritorna il valore 

    // Mostra la bomba (game over)
    public void revealBomb() { // Metodo "revealBomb" (per gameOver)
        state = State.REVEALED; // impone lo stato della cella uguale a rivelato
        repaint(); // la aggiorno (la grafica)
    }
// (fine 04/05)
    // --- Rendering ---

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        switch (state) {
            case HIDDEN   -> paintHidden(g2, w, h);
            case REVEALED -> paintRevealed(g2, w, h);
            case FLAGGED  -> paintFlagged(g2, w, h);
        }

        g2.dispose();
    }

    private void paintHidden(Graphics2D g2, int w, int h) {
        // Sfondo rialzato stile classic minesweeper
        g2.setColor(COLOR_HIDDEN);
        g2.fillRect(0, 0, w, h);

        // Bordo 3D
        g2.setColor(COLOR_BORDER_LIGHT);
        g2.fillRect(0, 0, w, 2);
        g2.fillRect(0, 0, 2, h);
        g2.setColor(COLOR_BORDER_DARK);
        g2.fillRect(0, h - 2, w, 2);
        g2.fillRect(w - 2, 0, 2, h);
    }

    private void paintRevealed(Graphics2D g2, int w, int h) {
        // Sfondo
        if (value == 9) {
            g2.setColor(COLOR_BOMB);
        } else {
            g2.setColor(COLOR_REVEALED);
        }
        g2.fillRect(0, 0, w, h);

        // Bordo sottile
        g2.setColor(COLOR_BORDER_DARK);
        g2.drawRect(0, 0, w - 1, h - 1);

        if (value == 9) {
            // Disegna bomba
            drawBomb(g2, w, h);
        } else if (value > 0) {
            // Disegna numero
            g2.setColor(NUMBER_COLORS[value]);
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            FontMetrics fm = g2.getFontMetrics();
            String text = String.valueOf(value);
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
        }
    }

    private void paintFlagged(Graphics2D g2, int w, int h) {
        // Sfondo come hidden
        paintHidden(g2, w, h);

        // Bandierina
        int cx = w / 2;
        int cy = h / 2;

        // Asta
        g2.setColor(new Color(60, 60, 60));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(cx - 1, cy + 6, cx - 1, cy - 7);

        // Triangolo bandiera
        int[] xPts = { cx - 1, cx - 1, cx + 8 };
        int[] yPts = { cy - 7, cy + 1, cy - 3 };
        g2.setColor(new Color(220, 50, 50));
        g2.fillPolygon(xPts, yPts, 3);

        // Base
        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(cx - 5, cy + 6, 9, 2);
    }

    private void drawBomb(Graphics2D g2, int w, int h) {
        int cx = w / 2;
        int cy = h / 2;
        int r  = 7;

        // Corpo circolare
        g2.setColor(Color.BLACK);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Riflesso
        g2.setColor(new Color(255, 255, 255, 150));
        g2.fillOval(cx - r / 2, cy - r / 2 - 1, r / 2, r / 3);

        // Spine
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(cx, cy - r - 2, cx, cy + r + 2);
        g2.drawLine(cx - r - 2, cy, cx + r + 2, cy);
        g2.drawLine(cx - 5, cy - 5, cx + 5, cy + 5);
        g2.drawLine(cx + 5, cy - 5, cx - 5, cy + 5);
    }
}
