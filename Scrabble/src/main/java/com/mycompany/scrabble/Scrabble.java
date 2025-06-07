package com.mycompany.scrabble;

import javax.swing.SwingUtilities;

// Clase principal del juego Scrabble
public class Scrabble {
    
    // Punto de entrada de la aplicación
    public static void main(String[] args) {
        // Ejecuta la interfaz gráfica en el hilo de eventos
        SwingUtilities.invokeLater(() -> {
            new PantallaRegistro(); // Muestra la pantalla inicial
        });
    }
}
