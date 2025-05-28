package com.mycompany.scrabble;

import javax.swing.*;
import java.awt.*;

public class TableroVistaSwing extends JFrame {
    private static final int TAM_CASILLA = 40;

    public TableroVistaSwing(Tablero tablero) {
        setTitle("Scrabble - Tablero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(TAM_CASILLA * Tablero.COLUMNAS, TAM_CASILLA * Tablero.FILAS);
        setLocationRelativeTo(null); // Centra la ventana

        JPanel panel = new JPanel(new GridLayout(Tablero.FILAS, Tablero.COLUMNAS));

        for (int fila = 0; fila < Tablero.FILAS; fila++) {
            for (int col = 0; col < Tablero.COLUMNAS; col++) {
                Casilla casilla = tablero.obtenerCasilla(fila, col);
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                Ficha ficha = casilla.getFicha();
                if (ficha != null) {
                    label.setText(ficha.getLetra() + "");
                    label.setFont(new Font("Arial", Font.BOLD, 18));
                    label.setForeground(Color.BLACK);
                } else {
                    label.setText(""); // vacía
                }

                // Colores por tipo de bonificador
                switch (casilla.getMultiplier()) {
                    case Casilla.DOUL:
                        label.setBackground(new Color(173, 216, 230)); // azul claro
                        label.setText("DL");
                        break;
                    case Casilla.TRIPL:
                        label.setBackground(new Color(0, 102, 204)); // azul oscuro
                        label.setText("TL");
                        label.setForeground(Color.WHITE);
                        break;
                    case Casilla.DOUP:
                        label.setBackground(new Color(255, 182, 193)); // rosa
                        label.setText("DP");
                        break;
                    case Casilla.TRIPP:
                        label.setBackground(new Color(255, 69, 0)); // rojo
                        label.setText("TP");
                        label.setForeground(Color.WHITE);
                        break;
                    default:
                        label.setBackground(Color.WHITE); // casilla normal
                }

                panel.add(label);
            }
        }

        add(panel);
        setVisible(true);
    }
}
