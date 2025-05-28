/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.scrabble;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jos23
 */

public class Juego {
    private List<Jugador> jugadores;
    private int jugadorActualIndex;
    private Tablero tablero;
    private MontonFichas monton;
    private Ficha[][] tableroCopiaInicioTurno;
    private List<Ficha> fichasAtrilInicioTurno;
    private Ficha fichaSeleccionada;
    private boolean turnoTerminado;
    private boolean fichaRobada;
    private boolean jugadaValida;
    private final List<Ficha> fichasColocadasEsteTurno = new ArrayList<>();
    private Juez juez;

    public Juego(List<String> nombresJugadores) {
    jugadores = new ArrayList<>();
    for (String nombre : nombresJugadores) {
        jugadores.add(new Jugador(nombre));
    }

    tablero = new Tablero();
    monton = new MontonFichas();

    jugadorActualIndex = determinarPrimerJugador();
    turnoTerminado = false;
    fichaRobada = false;
    jugadaValida = true;

    repartirFichasIniciales();
    guardarEstadoInicialTurno();
}
    private void repartirFichasIniciales() {
    for (Jugador jugador : jugadores) {
        while (jugador.getFichas().size() < 14) {
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
            fichasSorteo.add(ficha);
            char letra = ficha.getLetra();

            if (letra == '-') {
                mejorLetra = '-';
                indicesEmpatados.clear();
                indicesEmpatados.add(fichasSorteo.indexOf(ficha));
                break; // el comodín tiene prioridad
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

        // llamamos a devolverfichas
        monton.devolverFichas(fichasSorteo);

        // Si hay un solo ganador, retornamos su índice
        if (indicesEmpatados.size() == 1) {
            return indicesEmpatados.get(0);
        }

        // Si hubo empate, repetimos el sorteo
    }
}

    private void guardarEstadoInicialTurno() {
        tableroCopiaInicioTurno = new Ficha[Tablero.FILAS][Tablero.COLUMNAS];
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                Ficha original = tablero.obtenerFicha(i, j);
                if (original != null) {
                    tableroCopiaInicioTurno[i][j] = new Ficha(original);
                }
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
        return getJugadorActual().getFichas();
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

   public void colocarFichaEnTablero(Ficha ficha, int fila, int columna) {
    Casilla casilla = tablero.obtenerCasilla(fila, columna);
    
    if (casilla.getFicha() == null) {
        casilla.setFicha(ficha);
        getJugadorActual().removerFicha(ficha);
    } else {
        System.out.println("La casilla ya está ocupada. No se puede colocar la ficha.");
    }
}




    public boolean terminarTurno(List<Casilla> casillasColocadasEsteTurno, Juez juez) {
    List<String> palabras = getPalabrasFormadasEsteTurno(juez, tablero, casillasColocadasEsteTurno);

    if (palabras.isEmpty()) {
        System.out.println("Jugada inválida: no se formaron palabras válidas.");
        jugadaValida = false;
        return false;
    }

    // Aquí podrías asignar puntos al jugador, etc.
    jugadaValida = true;
    turnoTerminado = true;
    siguienteTurno();
    return true;
}


    public void siguienteTurno() {
        jugadorActualIndex = (jugadorActualIndex + 1) % jugadores.size();
        turnoTerminado = false;
        fichaRobada = false;
        jugadaValida = true;
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
                Ficha f = tableroCopiaInicioTurno[i][j];
                if (f != null) {
                    tablero.colocarFicha(i, j, new Ficha(f));
                }
            }
        }
        for (Ficha f : fichasAtrilInicioTurno) {
            getJugadorActual().agregarFicha(new Ficha(f));
        }
        fichaSeleccionada = null;
    }

    public void robarFicha() {
        if (!turnoTerminado && !fichaRobada) {
            Ficha ficha = monton.robarFicha();
            if (ficha != null) {
                getJugadorActual().agregarFicha(ficha);
                fichaRobada = true;
                jugadaValida = false;
                siguienteTurno();
            }
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
        int fila = casilla.getY();
        int col = casilla.getX();

        // Palabra horizontal
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

        // Palabra vertical
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
