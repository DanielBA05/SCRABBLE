package com.mycompany.scrabble;

import javax.swing.SwingUtilities;

public class Scrabble {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PantallaRegistro();
        });
    }
}

