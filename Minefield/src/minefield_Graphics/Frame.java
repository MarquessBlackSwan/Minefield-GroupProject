package minefield_Graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Frame extends JFrame implements Field.GameListener {

    // --- Costanti ---
    private static final int CELL_SIZE   = 35;
    private static final int BOMB_COUNT  = 40;
    private static final Color BG_DARK   = new Color(30, 30, 35);
    private static final Color BG_PANEL  = new Color(45, 45, 52);
    private static final Color ACCENT    = new Color(255, 80, 80);
    private static final Color TEXT_COL  = new Color(230, 230, 230);
    private static final Color TEXT_DIM  = new Color(140, 140, 160);

    // --- Stato ---
    private final int gridSize;
    private Field field;
    private boolean gameActive = false;
    private boolean firstClick = true;

    // --- Timer ---
    private Timer swingTimer;
    private int elapsedSeconds = 0;

    // --- Componenti UI ---
    private JPanel gridPanel;
    private JLabel bombCountLabel;
    private JLabel timerLabel;
    private JLabel statusLabel;
    private JButton restartBtn;

    // =========================================================
    //  Costruttore
    // =========================================================

    public Frame(int gridSize) {
        this.gridSize = gridSize;
        initTimer();
        buildFrame();
        newGame();
    }

    // =========================================================
    //  Setup frame
    // =========================================================

    private void buildFrame() {
        setTitle("Minefield");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildGridContainer(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Top bar: titolo + stato ---
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_DARK);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 20, 8, 20));

        JLabel title = new JLabel("MINEFIELD");
        title.setForeground(ACCENT);
        title.setFont(new Font("Courier New", Font.BOLD, 22));

        statusLabel = new JLabel("Clicca per iniziare");
        statusLabel.setForeground(TEXT_DIM);
        statusLabel.setFont(new Font("Courier New", Font.PLAIN, 13));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        bar.add(title, BorderLayout.WEST);
        bar.add(statusLabel, BorderLayout.EAST);

        return bar;
    }

    // --- Pannello info: bombe, pulsante restart, timer ---
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(70, 70, 80)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Bombe
        bombCountLabel = makeLedLabel("💣  " + BOMB_COUNT);
        JPanel leftSection = labeledSection("BOMBE", bombCountLabel);

        // Timer
        timerLabel = makeLedLabel("000");
        JPanel rightSection = labeledSection("TEMPO", timerLabel);

        // Restart
        restartBtn = new JButton("↺  NUOVA PARTITA");
        styleButton(restartBtn);
        restartBtn.addActionListener(e -> newGame());

        bar.add(leftSection,  BorderLayout.WEST);
        bar.add(restartBtn,   BorderLayout.CENTER);
        bar.add(rightSection, BorderLayout.EAST);

        return bar;
    }

    private JLabel makeLedLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Courier New", Font.BOLD, 20));
        lbl.setForeground(ACCENT);
        return lbl;
    }

    private JPanel labeledSection(String title, JLabel value) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setBackground(BG_PANEL);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Courier New", Font.PLAIN, 9));
        lbl.setForeground(TEXT_DIM);
        p.add(lbl, BorderLayout.NORTH);
        p.add(value, BorderLayout.SOUTH);
        return p;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(60, 60, 70));
        btn.setForeground(TEXT_COL);
        btn.setFont(new Font("Courier New", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 100), 1),
            BorderFactory.createEmptyBorder(6, 18, 6, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(CENTER_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(80, 80, 95));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 70));
            }
        });
    }

    // --- Container griglia ---
    private JScrollPane buildGridContainer() {
        gridPanel = new JPanel();
        gridPanel.setBackground(BG_DARK);

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBackground(BG_DARK);
        return scroll;
    }

    // =========================================================
    //  Logica di gioco
    // =========================================================

    private void newGame() {
        // Reset timer
        swingTimer.stop();
        elapsedSeconds = 0;
        timerLabel.setText("000");

        // Reset stato
        gameActive  = true;
        firstClick  = true;
        field       = new Field(gridSize, BOMB_COUNT);
        field.setGameListener(this);

        bombCountLabel.setText("💣  " + BOMB_COUNT);
        statusLabel.setForeground(TEXT_DIM);
        statusLabel.setText("Clicca per iniziare");

        buildGrid();
    }

    private void buildGrid() {
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(gridSize, gridSize, 1, 1));
        gridPanel.setPreferredSize(new Dimension(
            gridSize * CELL_SIZE + gridSize - 1,
            gridSize * CELL_SIZE + gridSize - 1
        ));

        Cell[][] cells = field.getCells();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                final int r = row, c = col;
                Cell cell = cells[row][col];

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!gameActive) return;
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            handleLeftClick(c, r);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            handleRightClick(c, r);
                        }
                    }
                });

                gridPanel.add(cell);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
        pack();
    }

    private void handleLeftClick(int col, int row) {
        if (firstClick) {
            firstClick = false;
            swingTimer.start();
            statusLabel.setForeground(TEXT_COL);
            statusLabel.setText("In gioco…");
        }
        field.revealCell(col, row);
        gridPanel.repaint();
    }

    private void handleRightClick(int col, int row) {
        field.toggleFlag(col, row);
        gridPanel.repaint();
    }

    // =========================================================
    //  Timer
    // =========================================================

    private void initTimer() {
        swingTimer = new Timer(1000, e -> {
            elapsedSeconds++;
            timerLabel.setText(String.format("%03d", Math.min(elapsedSeconds, 999)));
        });
    }

    // =========================================================
    //  GameListener callbacks
    // =========================================================

    @Override
    public void onGameOver() {
        swingTimer.stop();
        gameActive = false;
        statusLabel.setForeground(ACCENT);
        statusLabel.setText("💥  Sei saltato in aria!");
        gridPanel.repaint();

        // Mostra dialog dopo un breve delay
        Timer delay = new Timer(400, e -> showEndDialog(false));
        delay.setRepeats(false);
        delay.start();
    }

    @Override
    public void onVictory() {
        swingTimer.stop();
        gameActive = false;
        statusLabel.setForeground(new Color(80, 200, 120));
        statusLabel.setText("🏆  Hai vinto in " + elapsedSeconds + "s!");
        gridPanel.repaint();

        Timer delay = new Timer(400, e -> showEndDialog(true));
        delay.setRepeats(false);
        delay.start();
    }

    @Override
    public void onBombCountChanged(int remaining) {
        bombCountLabel.setText("💣  " + remaining);
    }

    // =========================================================
    //  Dialog fine partita
    // =========================================================

    private void showEndDialog(boolean won) {
        String[] options = { "Nuova partita", "Esci" };
        String msg = won
            ? "Congratulazioni! Completato in " + elapsedSeconds + " secondi."
            : "Boom! Hai calpestato una mina.";
        String title = won ? "Vittoria!" : "Game Over";

        int choice = JOptionPane.showOptionDialog(
            this, msg, title,
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
            null, options, options[0]
        );

        if (choice == 0) {
            newGame();
        } else {
            System.exit(0);
        }
    }
}
