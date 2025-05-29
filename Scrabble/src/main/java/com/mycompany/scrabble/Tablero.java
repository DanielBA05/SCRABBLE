package com.mycompany.scrabble;

public class Tablero {
    public static final int FILAS = 15;
    public static final int COLUMNAS = 15;
    private Casilla[][] matriz;

    public Tablero() {
        matriz = new Casilla[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                int mult = obtenerBonificador(i, j);
                matriz[i][j] = new Casilla(i, j, mult);
            }
        }
    }
    
    public Tablero(Casilla[][] original){
        matriz = new Casilla[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                matriz[i][j] = original[i][j];
            }
        }
    }

    private int obtenerBonificador(int x, int y) {
        // Triple Palabra (TRIPP)
        int[][] triplePalabra = {
            {0, 0}, {0, 7}, {0, 14},
            {7, 0}, {7, 14},
            {14, 0}, {14, 7}, {14, 14}
        };

        // Doble Palabra (DOUP)
        int[][] doblePalabra = {
            {1, 1}, {2, 2}, {3, 3}, {4, 4}, {10, 10}, {11, 11}, {12, 12}, {13, 13},
            {1, 13}, {2, 12}, {3, 11}, {4, 10}, {10, 4}, {11, 3}, {12, 2}, {13, 1},
            {7, 7} // centro
        };

        // Triple Letra (TRIPL)
        int[][] tripleLetra = {
            {5, 1}, {5, 5}, {5, 9}, {5, 13},
            {9, 1}, {9, 5}, {9, 9}, {9, 13},
            {1, 5}, {1, 9}, {13, 5}, {13, 9}
        };

        // Doble Letra (DOUL)
        int[][] dobleLetra = {
            {0, 3}, {0, 11}, {2, 6}, {2, 8},
            {3, 0}, {3, 7}, {3, 14}, {6, 2}, {6, 6}, {6, 8}, {6, 12},
            {7, 3}, {7, 11}, {8, 2}, {8, 6}, {8, 8}, {8, 12},
            {11, 0}, {11, 7}, {11, 14}, {12, 6}, {12, 8}, {14, 3}, {14, 11}
        };

        for (int[] pos : triplePalabra) {
            if (x == pos[0] && y == pos[1]) return Casilla.TRIPP;
        }

        for (int[] pos : doblePalabra) {
            if (x == pos[0] && y == pos[1]) return Casilla.DOUP;
        }

        for (int[] pos : tripleLetra) {
            if (x == pos[0] && y == pos[1]) return Casilla.TRIPL;
        }

        for (int[] pos : dobleLetra) {
            if (x == pos[0] && y == pos[1]) return Casilla.DOUL;
        }

        // Casilla normal
        return Casilla.NONE;
    }

    public Casilla[][] getMatriz(){
        return matriz;
    }

    // Métodos clave
    public void colocarFicha(int fila, int columna, Ficha ficha) {
        matriz[fila][columna].setFicha(ficha);
    }

    public Ficha obtenerFicha(int fila, int columna) {
        return matriz[fila][columna].getFicha();
    }

    public Casilla obtenerCasilla(int fila, int columna) {
        return matriz[fila][columna];
    }
    
    public void cambiarCasilla(int fila, int columna, Casilla casilla){
        matriz[fila][columna]=casilla;
    }
    
    public void quitarFichaDeTablero(int fila, int columna) {
    Casilla casilla = obtenerCasilla(fila, columna);
    casilla.quitarFicha();
}
    
    //es un prototipo, de ser necesario lo pueden mover a otra clase
    public void aplicarBonificadores(Casilla casilla, int[] valorLetra, int[] multiplicadorPalabra) {
        if (!casilla.isBonificadorUsado()) {
            switch (casilla.getMultiplier()) {
                case Casilla.DOUL:
                    valorLetra[0] *= 2;
                    break;
                case Casilla.TRIPL:
                    valorLetra[0] *= 3;
                    break;
                case Casilla.DOUP:
                    multiplicadorPalabra[0] *= 2;
                    break;
                case Casilla.TRIPP:
                    multiplicadorPalabra[0] *= 3;
                    break;
            }
            casilla.setBonificadorUsado(true);
        }
    }
}
