package com.mycompany.scrabble;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Juego {
    private List<Jugador> jugadores;
    private int jugadorActualIndex;
    private Tablero tablero;
    private MontonFichas monton;
    private Casilla[][] tableroCopiaInicioTurno;
    private List<Ficha> fichasAtrilInicioTurno;
    private Ficha fichaSeleccionada;
    private boolean turnoTerminado;
    private boolean fichaRobada;
    private int cantJugadores;
    private final List<Casilla> fichasColocadasEsteTurno = new ArrayList<>();
    public Juez juez;

    public Juego(List<String> nombresJugadores, String rutaDiccionario) throws IOException {
        jugadores = new ArrayList<>();
        for (String nombre : nombresJugadores) {
            jugadores.add(new Jugador(nombre));
        }

        tablero = new Tablero();
        monton = new MontonFichas();
        juez = new Juez();

        jugadorActualIndex = determinarPrimerJugador();
        turnoTerminado = false;
        fichaRobada = false;
        cantJugadores = jugadores.size();

        repartirFichasIniciales();
        guardarEstadoInicialTurno();
    }

    public List<Casilla> getFichasColocadasEsteTurno() {
        return new ArrayList<>(fichasColocadasEsteTurno);
    }

    public List<Jugador> getJugadores() {
        return new ArrayList<>(jugadores);
    }

    private void repartirFichasIniciales() {
        for (Jugador jugador : jugadores) {
            while (jugador.getFichas().size() < 7) {
                Ficha ficha = monton.robarFicha();
                if (ficha != null) {
                    jugador.agregarFicha(ficha);
                } else {
                    break;
                }
            }
        }
    }

    private int determinarPrimerJugador() {
        List<Ficha> fichasSorteo = new ArrayList<>();
        List<Integer> indicesEmpatados = new ArrayList<>();

        while (true) {
            fichasSorteo.clear();
            indicesEmpatados.clear();
            char mejorLetra = 'Z' + 1;
            int ganador = -1;

            for (Jugador jugador : jugadores) {
                Ficha ficha = monton.robarFicha();
                if (ficha == null) continue;
                fichasSorteo.add(ficha);
                char letra = ficha.getLetra();

                if (letra == '-') {
                    mejorLetra = '-';
                    indicesEmpatados.clear();
                    indicesEmpatados.add(fichasSorteo.indexOf(ficha));
                    break;
                }

                if (letra < mejorLetra) {
                    mejorLetra = letra;
                    ganador = fichasSorteo.indexOf(ficha);
                    indicesEmpatados.clear();
                    indicesEmpatados.add(ganador);
                } else if (letra == mejorLetra) {
                    indicesEmpatados.add(fichasSorteo.indexOf(ficha));
                }
            }

            monton.devolverFichas(fichasSorteo);

            if (indicesEmpatados.size() == 1) {
                return indicesEmpatados.get(0);
            }
        }
    }

    private void guardarEstadoInicialTurno() {
        tableroCopiaInicioTurno = new Casilla[Tablero.FILAS][Tablero.COLUMNAS];
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                Casilla original = tablero.obtenerCasilla(i, j);
                tableroCopiaInicioTurno[i][j] = new Casilla(original);
            }
        }
        fichasAtrilInicioTurno = new ArrayList<>();
        for (Ficha f : getJugadorActual().getFichas()) {
            fichasAtrilInicioTurno.add(new Ficha(f));
        }
    }

    public Jugador getJugadorActual() {
        return jugadores.get(jugadorActualIndex);
    }

    public List<Ficha> getFichasJugadorActual() {
        return new ArrayList<>(getJugadorActual().getFichas());
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Ficha getFichaSeleccionada() {
        return fichaSeleccionada;
    }

    public void setFichaSeleccionada(Ficha ficha) {
        this.fichaSeleccionada = ficha;
    }

    public boolean colocarFichaEnTablero(Ficha ficha, int fila, int columna) {
        if (ficha == null) return false;
        
        Casilla casilla = tablero.obtenerCasilla(fila, columna);
        if (casilla == null) return false;
        
        if (casilla.getFicha() == null) {
            casilla.setFicha(ficha);
            getJugadorActual().removerFicha(ficha);
            fichasColocadasEsteTurno.add(casilla);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "La casilla ya está ocupada.");
            return false;
        }
    }

    public boolean terminarTurno(List<Casilla> casillasColocadasEsteTurno, Juez juez) {
        if (casillasColocadasEsteTurno.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No has colocado ninguna ficha este turno.");
            return false;
        }
        List<String> palabras = getPalabrasFormadasEsteTurno(juez, tablero, casillasColocadasEsteTurno);

        if (palabras.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Jugada inválida: no se formaron palabras válidas.");
            return false;
        }

        int puntos = calcularPuntos(palabras, casillasColocadasEsteTurno);
        getJugadorActual().sumarPuntos(puntos);
        turnoTerminado = true;
        siguienteTurno();
        return true;
    }

    private int calcularPuntos(List<String> palabras, List<Casilla> casillas) {
        int total = 0;
        for (String palabra : palabras) {
            total += palabra.length() * 10;
        }
        
        for (Casilla casilla : casillas) {
            switch (casilla.getMultiplier()) {
                case Casilla.DOUL:
                    total *= 2;
                    break;
                case Casilla.TRIPL:
                    total *= 3;
                    break;
                case Casilla.DOUP:
                    total += 10;
                    break;
                case Casilla.TRIPP:
                    total += 20;
                    break;
            }
        }
        return total;
    }

    public void siguienteTurno() {
        while (getJugadorActual().getFichas().size() < 7 && monton.getCantidadFichas() > 0) {
            Ficha ficha = monton.robarFicha();
            if (ficha != null) {
                getJugadorActual().agregarFicha(ficha);
            }
        }

        jugadorActualIndex = (jugadorActualIndex + 1) % jugadores.size();
        turnoTerminado = false;
        fichaRobada = false;
        fichaSeleccionada = null;
        fichasColocadasEsteTurno.clear();
        guardarEstadoInicialTurno();
    }

    public void reiniciarJugada() {
        getJugadorActual().getFichas().clear();
        fichasColocadasEsteTurno.clear();
        
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                tablero.quitarFichaDeTablero(i, j);
            }
        }
        
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                Casilla c = tableroCopiaInicioTurno[i][j];
                tablero.cambiarCasilla(i, j, new Casilla(c));
                
            }
        }
        
        for (Ficha f : fichasAtrilInicioTurno) {
            getJugadorActual().agregarFicha(new Ficha(f));
        }
        
        fichaSeleccionada = null;
    }

    public void robarFicha() {
        if (!turnoTerminado && !fichaRobada && monton.getCantidadFichas() > 0) {
            Ficha ficha = monton.robarFicha();
            if (ficha != null) {
                getJugadorActual().agregarFicha(ficha);
                fichaRobada = true;
                siguienteTurno();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No se pueden robar más fichas este turno o el montón está vacío.");
        }
    }

    public int getMonton() {
        return monton.getCantidadFichas();
    }

    public List<String> getPalabrasFormadasEsteTurno(Juez juez, Tablero tablero, List<Casilla> casillasColocadasEsteTurno) {
        List<String> palabrasValidas = new ArrayList<>();
        boolean[][] visitadoH = new boolean[Tablero.FILAS][Tablero.COLUMNAS];
        boolean[][] visitadoV = new boolean[Tablero.FILAS][Tablero.COLUMNAS];

        for (Casilla casilla : casillasColocadasEsteTurno) {
            // Obtener posición de la casilla de otra manera
            int fila = -1;
            int col = -1;

            // Buscar la posición de la casilla en el tablero
            for (int i = 0; i < Tablero.FILAS; i++) {
                for (int j = 0; j < Tablero.COLUMNAS; j++) {
                    if (tablero.obtenerCasilla(i, j) == casilla) {
                        fila = i;
                        col = j;
                        break;
                    }
                }
                if (fila != -1) break;
            }

            if (fila == -1 || col == -1) continue; // Casilla no encontrada

            if (!visitadoH[fila][col]) {
                StringBuilder palabraHorizontal = new StringBuilder();
                int c = col;
                while (c >= 0 && tablero.obtenerFicha(fila, c) != null) c--;
                c++;
                int inicio = c;
                while (c < Tablero.COLUMNAS && tablero.obtenerFicha(fila, c) != null) {
                    palabraHorizontal.append(tablero.obtenerFicha(fila, c).getLetra());
                    visitadoH[fila][c] = true;
                    c++;
                }
                if (c - inicio >= 2 && juez.esValida(palabraHorizontal.toString())) {
                    palabrasValidas.add(palabraHorizontal.toString());
                }
            }

            if (!visitadoV[fila][col]) {
                StringBuilder palabraVertical = new StringBuilder();
                int f = fila;
                while (f >= 0 && tablero.obtenerFicha(f, col) != null) f--;
                f++;
                int inicio = f;
                while (f < Tablero.FILAS && tablero.obtenerFicha(f, col) != null) {
                    palabraVertical.append(tablero.obtenerFicha(f, col).getLetra());
                    visitadoV[f][col] = true;
                    f++;
                }
                if (f - inicio >= 2 && juez.esValida(palabraVertical.toString())) {
                    palabrasValidas.add(palabraVertical.toString());
                }
            }
        }

        return palabrasValidas;
    }
}