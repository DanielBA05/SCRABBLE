package com.mycompany.scrabble;

/**
 *
 * @author jos23
 */
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

    private int obtenerBonificador(int x, int y) {
    // Esquinas y puntos medios de los bordes: Triple Palabra (TRIPP)
    if ((x == 0 || x == 14) && (y == 0 || y == 7 || y == 14)) {
        return Casilla.TRIPP;
    }
    if ((y == 0 || y == 14) && (x == 7)) {
        return Casilla.TRIPP;
    }

    // Centro: Doble Palabra (DOUP)
    if (x == 7 && y == 7) {
        return Casilla.DOUP;
    }

    // Diagonales (excepto esquinas y centro): Doble Palabra (DOUP)
    if ((x == y || x + y == 14) && x != 0 && x != 7 && x != 14 && x!=6 && x!=8 && y!=6 && y!=8 && y != 0 && y != 7 && y != 14 ) {
        return Casilla.DOUP;
    }

    // Triple Letra (TRIPL) 
    if ((x == 5 && (y == 1 || y == 5 || y == 9 || y == 13)) ||
        (x == 9 && (y == 1 || y == 5 || y == 9 || y == 13)) ||
        ((y == 5 || y == 9) && (x == 1 || x == 5 || x == 9 || x == 13))) {
        return Casilla.TRIPL;
    }

    // Doble Letra (DOUL) 
    if ((x == 3 || x == 11) && (y == 0 || y == 7 || y == 14)) {
        return Casilla.DOUL;
    }
    if ((y == 3 || y == 11) && (x == 0 || x == 7 || x == 14)) {
        return Casilla.DOUL;
    }
    if ((x == 6 || x == 8) && (y == 2 || y == 6 || y == 8 || y == 12)) {
        return Casilla.DOUL;
    }
    if ((y == 6 || y == 8) && (x == 2 || x == 6 || x == 8 || x == 12)) {
        return Casilla.DOUL;
    }

    // Resto: casillas normales
    return Casilla.NONE;
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
