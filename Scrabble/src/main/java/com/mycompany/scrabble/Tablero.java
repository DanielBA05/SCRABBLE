package com.mycompany.scrabble;

// Clase que representa el tablero del juego Scrabble
public class Tablero {
    // Dimensiones del tablero
    public static final int FILAS = 15;
    public static final int COLUMNAS = 15;
    private Casilla[][] matriz;  // Matriz de casillas

    // Constructor que inicializa el tablero con casillas
    public Tablero() {
        matriz = new Casilla[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                int mult = obtenerBonificador(i, j);  // Obtiene bonificador para la casilla
                matriz[i][j] = new Casilla(i, j, mult);  // Crea nueva casilla
            }
        }
    }
    
    // Constructor copia
    public Tablero(Casilla[][] original){
        matriz = new Casilla[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                matriz[i][j] = original[i][j];  // Copia cada casilla
            }
        }
    }

    // Determina el tipo de bonificador para una posición
    private int obtenerBonificador(int x, int y) {
        // Coordenadas de casillas con triple palabra
        int[][] triplePalabra = {
            {0, 0}, {0, 7}, {0, 14},
            {7, 0}, {7, 14},
            {14, 0}, {14, 7}, {14, 14}
        };

        // Coordenadas de casillas con doble palabra
        int[][] doblePalabra = {
            {1, 1}, {2, 2}, {3, 3}, {4, 4}, {10, 10}, {11, 11}, {12, 12}, {13, 13},
            {1, 13}, {2, 12}, {3, 11}, {4, 10}, {10, 4}, {11, 3}, {12, 2}, {13, 1},
            {7, 7} // centro
        };

        // Coordenadas de casillas con triple letra
        int[][] tripleLetra = {
            {5, 1}, {5, 5}, {5, 9}, {5, 13},
            {9, 1}, {9, 5}, {9, 9}, {9, 13},
            {1, 5}, {1, 9}, {13, 5}, {13, 9}
        };

        // Coordenadas de casillas con doble letra
        int[][] dobleLetra = {
            {0, 3}, {0, 11}, {2, 6}, {2, 8},
            {3, 0}, {3, 7}, {3, 14}, {6, 2}, {6, 6}, {6, 8}, {6, 12},
            {7, 3}, {7, 11}, {8, 2}, {8, 6}, {8, 8}, {8, 12},
            {11, 0}, {11, 7}, {11, 14}, {12, 6}, {12, 8}, {14, 3}, {14, 11}
        };

        // Verifica si la posición es triple palabra
        for (int[] pos : triplePalabra) {
            if (x == pos[0] && y == pos[1]) return Casilla.TRIPP;
        }

        // Verifica si la posición es doble palabra
        for (int[] pos : doblePalabra) {
            if (x == pos[0] && y == pos[1]) return Casilla.DOUP;
        }

        // Verifica si la posición es triple letra
        for (int[] pos : tripleLetra) {
            if (x == pos[0] && y == pos[1]) return Casilla.TRIPL;
        }

        // Verifica si la posición es doble letra
        for (int[] pos : dobleLetra) {
            if (x == pos[0] && y == pos[1]) return Casilla.DOUL;
        }

        // Casilla normal sin bonificación
        return Casilla.NONE;
    }

    // Devuelve la matriz de casillas
    public Casilla[][] getMatriz(){
        return matriz;
    }

    // Coloca una ficha en una posición específica
    public void colocarFicha(int fila, int columna, Ficha ficha) {
        matriz[fila][columna].setFicha(ficha);
    }

    // Obtiene la ficha en una posición
    public Ficha obtenerFicha(int fila, int columna) {
        return matriz[fila][columna].getFicha();
    }

    // Obtiene la casilla en una posición
    public Casilla obtenerCasilla(int fila, int columna) {
        return matriz[fila][columna];
    }
    
    // Reemplaza una casilla en una posición
    public void cambiarCasilla(int fila, int columna, Casilla casilla){
        matriz[fila][columna]=casilla;
    }
    
    // Elimina una ficha del tablero
    public void quitarFichaDeTablero(int fila, int columna) {
        Casilla casilla = obtenerCasilla(fila, columna);
        casilla.quitarFicha();
    }
}