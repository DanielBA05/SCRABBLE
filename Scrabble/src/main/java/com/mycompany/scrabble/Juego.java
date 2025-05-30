package com.mycompany.scrabble;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Juego {
    private List<Jugador> jugadores;
    private int jugadorActualIndex;
    private Tablero tablero;
    private MontonFichas monton;
    private Casilla[][] tableroCopiaInicioTurno;
    private List<Ficha> fichasAtrilInicioTurno;
    private Ficha fichaSeleccionada;
    private List<Ficha> variasFichas;
    private boolean turnoTerminado;
    private int cantJugadores;
    private final List<Casilla> fichasColocadasEsteTurno = new ArrayList<>();
    public Juez juez;
    
    // Variables para el cambio de fichas
    private boolean modoSeleccionCambio = false;
    private List<Ficha> fichasSeleccionadasCambio = new ArrayList<>();
    //variables pero para separar la primera jugada de las demás
    private boolean primeraJugada = true;
    private static final int CENTRO_FILA = Tablero.FILAS / 2;
    private static final int CENTRO_COLUMNA = Tablero.COLUMNAS / 2;

    public Juego(List<Jugador> jugadoresOrdenados) {
        this.jugadores = new ArrayList<>(jugadoresOrdenados);
        this.tablero = new Tablero();
        this.monton = new MontonFichas();
        this.juez = new Juez();

        this.jugadorActualIndex = 0;
        this.turnoTerminado = false;
        this.cantJugadores = jugadores.size();

        repartirFichasIniciales();
        guardarEstadoInicialTurno();
    }

    // Métodos de acceso
    public List<Casilla> getFichasColocadasEsteTurno() {
        return new ArrayList<>(fichasColocadasEsteTurno);
    }

    public List<Jugador> getJugadores() {
        return new ArrayList<>(jugadores);
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

    public int getMonton() {
        return monton.getCantidadFichas();
    }

    // Métodos para el cambio de fichas
    public List<Ficha> getFichasSeleccionadasParaCambio() {
        return new ArrayList<>(fichasSeleccionadasCambio);
    }

    public boolean isModoSeleccionCambio() {
        return modoSeleccionCambio;
    }

    public void seleccionarFichaParaCambio(Ficha ficha) {
        if (!fichasSeleccionadasCambio.contains(ficha)) {
            fichasSeleccionadasCambio.add(ficha);
        }
    }

    public void deseleccionarFichaParaCambio(Ficha ficha) {
        fichasSeleccionadasCambio.remove(ficha);
    }

    public void cambiarFicha() {
        if (modoSeleccionCambio) {
            terminarCambioFichas();
        } else {
            iniciarCambioFichas();
        }
    }

    private void iniciarCambioFichas() {
        if (!turnoTerminado && fichasColocadasEsteTurno.isEmpty()) {
            if (monton.getCantidadFichas() == 0) {
                JOptionPane.showMessageDialog(null, "No hay fichas en el montón para cambiar.");
                return;
            }
            modoSeleccionCambio = true;
            fichasSeleccionadasCambio.clear();
            
            int maxFichasCambio = Math.min(monton.getCantidadFichas(), getJugadorActual().getFichas().size());
            JOptionPane.showMessageDialog(null, 
                "Selecciona las fichas que deseas cambiar (máximo " + maxFichasCambio + 
                "). Luego haz clic en 'Confirmar Cambio'.");
        } else {
            JOptionPane.showMessageDialog(null, 
                "No puedes cambiar fichas después de colocar fichas en el tablero.");
        }
    }

    public void terminarCambioFichas() {
        if (fichasSeleccionadasCambio.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se seleccionaron fichas para cambiar.");
            modoSeleccionCambio = false;
            return;
        }

        Jugador jugadorActual = getJugadorActual();
        int fichasDisponiblesMonton = monton.getCantidadFichas();
        int fichasACambiar = fichasSeleccionadasCambio.size();

        if (fichasDisponiblesMonton < fichasACambiar) {
            JOptionPane.showMessageDialog(null, 
                "No hay suficientes fichas en el montón. Solo puedes cambiar " + 
                fichasDisponiblesMonton + " fichas.");
            return;
        }

        if (fichasACambiar > jugadorActual.getFichas().size()) {
            JOptionPane.showMessageDialog(null, 
                "No puedes cambiar más fichas de las que tienes en tu atril.");
            return;
        }

        // Cambiar las fichas
        List<Ficha> fichasADevolver = new ArrayList<>(fichasSeleccionadasCambio);
        for (Ficha ficha : fichasSeleccionadasCambio) {
            jugadorActual.getFichas().remove(ficha);
        }

        // Devolver las fichas al montón
        monton.devolverFichas(fichasADevolver);

        // Robar nuevas fichas
        for (int i = 0; i < fichasACambiar; i++) {
            Ficha nuevaFicha = monton.robarFicha();
            jugadorActual.agregarFicha(nuevaFicha);
        }

        JOptionPane.showMessageDialog(null, 
            "Se han cambiado " + fichasACambiar + " fichas.");
        
        fichasAtrilInicioTurno = new ArrayList<>();
        for (Ficha f : jugadorActual.getFichas()) {
            fichasAtrilInicioTurno.add(new Ficha(f)); 
    
        }

        modoSeleccionCambio = false;
        fichasSeleccionadasCambio.clear();
        siguienteTurno();
    }

    // Métodos del juego
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

    public boolean colocarFichaEnTablero(Ficha ficha, int fila, int columna) {
        if (ficha == null) return false;

        if (primeraJugada && (fila != CENTRO_FILA || columna != CENTRO_COLUMNA)) {
            JOptionPane.showMessageDialog(null, 
                "La primera ficha debe colocarse en el centro del tablero (" + 
                CENTRO_FILA + "," + CENTRO_COLUMNA + ")");
            return false;
        }

        Casilla casilla = tablero.obtenerCasilla(fila, columna);
        if (casilla == null) return false;

        if (casilla.getFicha() == null) {
            // Si es un comodín, pedir al usuario que elija la letra
            if (ficha.esComodin()) {
                String letraStr = JOptionPane.showInputDialog(null, 
                    "Elija la letra que representará este comodín (A-Z):", 
                    "Asignar letra a comodín", 
                    JOptionPane.QUESTION_MESSAGE);

                if (letraStr == null || letraStr.isEmpty() || letraStr.length() != 1) {
                    return false; // El usuario canceló o no ingresó una letra válida
                }

                char letraElegida = Character.toUpperCase(letraStr.charAt(0));
                if (letraElegida < 'A' || letraElegida > 'Z') {
                    JOptionPane.showMessageDialog(null, 
                        "Por favor ingrese una letra válida (A-Z).");
                    return false;
                }

                // Crear una copia del comodín con la letra asignada
                Ficha comodinConLetra = new Ficha(ficha);
                comodinConLetra.setLetra(letraElegida);
                comodinConLetra.setPuntos(0); // Los comodines siempre valen 0 puntos

                casilla.setFicha(comodinConLetra);
                getJugadorActual().removerFicha(ficha);
            } else {
                casilla.setFicha(ficha);
                getJugadorActual().removerFicha(ficha);
            }

            fichasColocadasEsteTurno.add(casilla);

            if (primeraJugada) {
                primeraJugada = false;
            }
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
        
        
        if (primeraJugada) {
            boolean centroOcupado = false;
            for (Casilla casilla : casillasColocadasEsteTurno) {
                if (casilla.getX() == CENTRO_FILA && casilla.getY() == CENTRO_COLUMNA) {
                    centroOcupado = true;
                    break;
                }
            }
            if (!centroOcupado) {
                JOptionPane.showMessageDialog(null, 
                    "La primera jugada debe incluir la casilla central (" + 
                    CENTRO_FILA + "," + CENTRO_COLUMNA + ")");
                return false;
            }
        }
        
        List<String> palabras = getPalabrasFormadasEsteTurno(juez, tablero, casillasColocadasEsteTurno);

        if (palabras.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Jugada inválida: no se formaron palabras válidas.");
            return false;
        }

        int puntos = calcularPuntos(palabras, casillasColocadasEsteTurno);
        getJugadorActual().sumarPuntos(puntos);
        turnoTerminado = true;
        
     
        if (primeraJugada) {
            primeraJugada = false;
        }
        
        siguienteTurno();
        return true;
    }

    private int calcularPuntos(List<String> palabras, List<Casilla> casillas) {
        int total = 0;
        for (String palabra : palabras) { 
            int puntosPalabra = 0; 
            int multiplicarPor = 1;
            for (int i = 0; i < palabra.length() && i < casillas.size(); i++){
                Casilla casilla = casillas.get(i);
                Ficha actual = casilla.getFicha();
                switch(casilla.getMultiplier()){
                    case Casilla.DOUL:
                        puntosPalabra += (2 * actual.getPuntos());
                        casilla.disableMultiplier();
                        break;
                    case Casilla.TRIPL:
                        puntosPalabra += (3 * actual.getPuntos());
                        casilla.disableMultiplier();
                        break;
                    case Casilla.DOUP:
                        multiplicarPor *= 2;
                        casilla.disableMultiplier();
                        break;
                    case Casilla.TRIPP:
                        multiplicarPor *= 3;
                        casilla.disableMultiplier();
                        break;
                    default: 
                        puntosPalabra += actual.getPuntos();
                        break;
                }
            }
            total += (puntosPalabra * multiplicarPor);
            casillas = casillas.subList(palabra.length(), casillas.size());
            
        }
        return total;
    }

    public void siguienteTurno() {
        while (getJugadorActual().getFichas().size() < 7 && monton.getCantidadFichas() > 0) {
            Ficha ficha = monton.robarFicha();
            getJugadorActual().agregarFicha(ficha);
        }

        jugadorActualIndex = (jugadorActualIndex + 1) % jugadores.size();
        turnoTerminado = false;
        fichaSeleccionada = null;
        modoSeleccionCambio = false;
        fichasSeleccionadasCambio.clear();
        fichasColocadasEsteTurno.clear();
        guardarEstadoInicialTurno();
    }

    public void reiniciarJugada() {
    getJugadorActual().getFichas().clear();
    fichasColocadasEsteTurno.clear();
    fichasSeleccionadasCambio.clear();
    modoSeleccionCambio = false;

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

    boolean tableroVacio = true;
    for (int i = 0; i < Tablero.FILAS; i++) {
        for (int j = 0; j < Tablero.COLUMNAS; j++) {
            if (tablero.obtenerCasilla(i, j).getFicha() != null) {
                tableroVacio = false;
                break;
            }
        }
        if (!tableroVacio) break;
    }
    primeraJugada = tableroVacio;

    fichaSeleccionada = null;
}

    public List<String> getPalabrasFormadasEsteTurno(Juez juez, Tablero tablero, List<Casilla> casillasColocadasEsteTurno) {
        List<String> palabrasValidas = new ArrayList<>();
        if (casillasColocadasEsteTurno.isEmpty()) {
            return palabrasValidas;
        }

        boolean esHorizontal = true;
        boolean esVertical = true;
        int filaInicial = casillasColocadasEsteTurno.get(0).getX();
        int colInicial = casillasColocadasEsteTurno.get(0).getY();

        if (casillasColocadasEsteTurno.size() > 1) {
            for (int i = 1; i < casillasColocadasEsteTurno.size(); i++) {
                if (casillasColocadasEsteTurno.get(i).getX() != filaInicial) {
                    esHorizontal = false;
                }
                if (casillasColocadasEsteTurno.get(i).getY() != colInicial) {
                    esVertical = false;
                }
            }
        }

        if (!esHorizontal && !esVertical) {
            JOptionPane.showMessageDialog(null, "Las fichas deben colocarse en una sola línea (horizontal o vertical).");
            return new ArrayList<>();
        }

        Set<String> palabrasEncontradas = new HashSet<>();

        if (esHorizontal) {
            int minCol = casillasColocadasEsteTurno.stream().mapToInt(c -> c.getY()).min().orElse(colInicial);
            int maxCol = casillasColocadasEsteTurno.stream().mapToInt(c -> c.getY()).max().orElse(colInicial);

            StringBuilder palabraHoriz = new StringBuilder();
            int currentCol = minCol;
            while (currentCol >= 0 && tablero.obtenerFicha(filaInicial, currentCol) != null) {
                currentCol--;
            }
            currentCol++;

            while (currentCol < Tablero.COLUMNAS && tablero.obtenerFicha(filaInicial, currentCol) != null) {
                palabraHoriz.append(tablero.obtenerFicha(filaInicial, currentCol).getLetra());
                currentCol++;
            }

            if (palabraHoriz.length() >= 2) {
                palabrasEncontradas.add(palabraHoriz.toString());
            }

            for (Casilla c : casillasColocadasEsteTurno) {
                StringBuilder palabraVert = new StringBuilder();
                int currentRow = c.getX();
                int currentColVert = c.getY();

                int r = currentRow;
                while (r >= 0 && tablero.obtenerFicha(r, currentColVert) != null) {
                    r--;
                }
                r++;

                while (r < Tablero.FILAS && tablero.obtenerFicha(r, currentColVert) != null) {
                    palabraVert.append(tablero.obtenerFicha(r, currentColVert).getLetra());
                    r++;
                }
                if (palabraVert.length() >= 2) {
                    palabrasEncontradas.add(palabraVert.toString());
                }
            }
        } else if (esVertical) {
            int minFila = casillasColocadasEsteTurno.stream().mapToInt(c -> c.getX()).min().orElse(filaInicial);
            int maxFila = casillasColocadasEsteTurno.stream().mapToInt(c -> c.getX()).max().orElse(filaInicial);

            StringBuilder palabraVert = new StringBuilder();
            int currentRow = minFila;
            while (currentRow >= 0 && tablero.obtenerFicha(currentRow, colInicial) != null) {
                currentRow--;
            }
            currentRow++;

            while (currentRow < Tablero.FILAS && tablero.obtenerFicha(currentRow, colInicial) != null) {
                palabraVert.append(tablero.obtenerFicha(currentRow, colInicial).getLetra());
                currentRow++;
            }
            if (palabraVert.length() >= 2) {
                palabrasEncontradas.add(palabraVert.toString());
            }

            for (Casilla c : casillasColocadasEsteTurno) {
                StringBuilder palabraHoriz = new StringBuilder();
                int currentColHoriz = c.getY();
                int currentRowHoriz = c.getX();

                int col = currentColHoriz;
                while (col >= 0 && tablero.obtenerFicha(currentRowHoriz, col) != null) {
                    col--;
                }
                col++;

                while (col < Tablero.COLUMNAS && tablero.obtenerFicha(currentRowHoriz, col) != null) {
                    palabraHoriz.append(tablero.obtenerFicha(currentRowHoriz, col).getLetra());
                    col++;
                }
                if (palabraHoriz.length() >= 2) {
                    palabrasEncontradas.add(palabraHoriz.toString());
                }
            }
        }

        for (String p : palabrasEncontradas) {
            if (juez.esValida(p)) {
                palabrasValidas.add(p);
            } else {
                JOptionPane.showMessageDialog(null, "La palabra '" + p + "' no es válida en el diccionario.");
                return new ArrayList<>();
            }
        }

        return palabrasValidas;
    }
}