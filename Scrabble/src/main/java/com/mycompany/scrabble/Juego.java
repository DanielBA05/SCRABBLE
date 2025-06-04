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
    //variables para controlar la primera jugada
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
                return false;
            }

            char letraElegida = Character.toUpperCase(letraStr.charAt(0));
            if (letraElegida < 'A' || letraElegida > 'Z') {
                JOptionPane.showMessageDialog(null, 
                    "Por favor ingrese una letra válida (A-Z).");
                return false;
            }

            Ficha comodinConLetra = new Ficha(ficha);
            comodinConLetra.setLetra(letraElegida);
            comodinConLetra.setPuntos(0);

            casilla.setFicha(comodinConLetra);
            comodinConLetra.setLugar(casilla);
            getJugadorActual().removerFicha(ficha);
        } else {
            casilla.setFicha(ficha);
            ficha.setLugar(casilla);
            getJugadorActual().removerFicha(ficha);
        }
        
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
    
    
    if (primeraJugada) {
        boolean centroOcupado = false;
        for (Casilla casilla : casillasColocadasEsteTurno) {
            if (casilla.getX() == CENTRO_FILA && casilla.getY() == CENTRO_COLUMNA) {
                centroOcupado = true;
                break;
            }
        }
        
        if (!centroOcupado && tablero.obtenerCasilla(CENTRO_FILA, CENTRO_COLUMNA).getFicha() == null) {
            JOptionPane.showMessageDialog(null, 
                "La primera jugada debe incluir la casilla central (" + 
                CENTRO_FILA + "," + CENTRO_COLUMNA + ")");
            return false;
        }
    } 
    
    else {
        boolean conectada = false;
        for (Casilla casilla : casillasColocadasEsteTurno) {
            
            int x = casilla.getX();
            int y = casilla.getY();
            
            // validamos para cada casilla adyacente, o se aarriba, abajo, izquierda y derecha
            if (x > 0 && tablero.obtenerCasilla(x-1, y).getFicha() != null && 
                !casillasColocadasEsteTurno.contains(tablero.obtenerCasilla(x-1, y))) {
                conectada = true;
                break;
            }
           
            if (x < tablero.FILAS-1 && tablero.obtenerCasilla(x+1, y).getFicha() != null && 
                !casillasColocadasEsteTurno.contains(tablero.obtenerCasilla(x+1, y))) {
                conectada = true;
                break;
            }
           
            if (y > 0 && tablero.obtenerCasilla(x, y-1).getFicha() != null && 
                !casillasColocadasEsteTurno.contains(tablero.obtenerCasilla(x, y-1))) {
                conectada = true;
                break;
            }
            
            if (y < tablero.COLUMNAS && tablero.obtenerCasilla(x, y+1).getFicha() != null && 
                !casillasColocadasEsteTurno.contains(tablero.obtenerCasilla(x, y+1))) {
                conectada = true;
                break;
            }
        }
        
        if (!conectada) {
            JOptionPane.showMessageDialog(null, 
                "Las fichas colocadas deben conectarse con al menos una ficha existente en el tablero.");
            return false;
        }
    }

    // validamos con el judge, calculamos los puntos, actualizamos y pasamos turno
    List<List<Ficha>> palabrasFormadas = getPalabrasFormadasEsteTurno(juez, tablero, casillasColocadasEsteTurno);
    if (palabrasFormadas.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No se formaron palabras válidas.");
        return false;
    }

    int puntos = calcularPuntos(palabrasFormadas, casillasColocadasEsteTurno);
    getJugadorActual().sumarPuntos(puntos);

    primeraJugada = false;
    turnoTerminado = true;
    siguienteTurno();
    return true;
}

    private int calcularPuntos(List<List<Ficha>> palabras, List<Casilla> casillas) {
    int total = 0;
        System.out.println(palabras.size());
    for (List<Ficha> palabra : palabras) {
        int puntosPalabra = 0;
        int multiplicarPor = 1;
        System.out.println("LENGTH DE LA PALABRA: " + palabra.size());
        
        for (int i = 0; i < palabra.size(); i++) {
            Casilla casilla = palabra.get(i).getLugar();
            System.out.println(casilla);
            Ficha actual = palabra.get(i);

            switch(casilla.getMultiplier()) {
                case Casilla.DOUL:
                    puntosPalabra += (2 * actual.getPuntos());
                    casilla.disableMultiplier();
                    break;
                case Casilla.TRIPL:
                    puntosPalabra += (3 * actual.getPuntos());
                    casilla.disableMultiplier();
                    break;
                case Casilla.DOUP:
                    puntosPalabra += actual.getPuntos();
                    multiplicarPor *= 2;
                    casilla.disableMultiplier();
                    break;
                case Casilla.TRIPP:
                    puntosPalabra += actual.getPuntos();
                    multiplicarPor *= 3;
                    casilla.disableMultiplier();
                    break;
                default: 
                    puntosPalabra += actual.getPuntos();
                    break;
            }
            System.out.println(puntosPalabra);
        }
        
        total += (puntosPalabra * multiplicarPor);
        
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
        Jugador jugadorActual = getJugadorActual();
        List<Casilla> casillasAQuitar = new ArrayList<>(fichasColocadasEsteTurno); // Copia para iterar
        fichasColocadasEsteTurno.clear(); // Limpiar la lista para el próximo intento

        for (Casilla casillaColocada : casillasAQuitar) {
            Ficha fichaEnCasilla = casillaColocada.getFicha();
            if (fichaEnCasilla != null) {
                casillaColocada.setFicha(null); // Quitar la ficha de la casilla
            }
        }

        jugadorActual.getFichas().clear();
        for (Ficha f : fichasAtrilInicioTurno) {
            jugadorActual.agregarFicha(new Ficha(f)); // Añadir copias para evitar referencias cruzadas
        }

        fichaSeleccionada = null;
        modoSeleccionCambio = false;
        fichasSeleccionadasCambio.clear();

        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                Casilla originalAlInicioTurno = tableroCopiaInicioTurno[i][j];
                tablero.cambiarCasilla(i, j, new Casilla(originalAlInicioTurno));
            }
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
    }

    public List<List<Ficha>> getPalabrasFormadasEsteTurno(Juez juez, Tablero tablero, List<Casilla> casillasColocadasEsteTurno) {
        List<List<Ficha>> palabrasEnFichas = new ArrayList<>();
        List<List<Ficha>> palabrasEnFichasValidas = new ArrayList<>();
        List<String> palabrasValidas = new ArrayList<>();
        if (casillasColocadasEsteTurno.isEmpty()) {
            return new ArrayList<>();
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
            List<Ficha> nuevaPalabra = new ArrayList<>();
            int currentCol = minCol;
            while (currentCol >= 0 && tablero.obtenerFicha(filaInicial, currentCol) != null) {
                currentCol--;
            }
            currentCol++;

            while (currentCol < Tablero.COLUMNAS && tablero.obtenerFicha(filaInicial, currentCol) != null) {
                palabraHoriz.append(tablero.obtenerFicha(filaInicial, currentCol).getLetra());
                nuevaPalabra.add(tablero.obtenerFicha(filaInicial, currentCol));
                currentCol++;
            }

            if (palabraHoriz.length() >= 2) {
                palabrasEncontradas.add(palabraHoriz.toString());
                palabrasEnFichas.add(new ArrayList<>(nuevaPalabra));
            }
            nuevaPalabra.clear();
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
                    nuevaPalabra.add(tablero.obtenerFicha(r, currentColVert));
                    r++;
                }
                if (palabraVert.length() >= 2) {
                    palabrasEncontradas.add(palabraVert.toString());
                    palabrasEnFichas.add(new ArrayList<>(nuevaPalabra));
                }
                nuevaPalabra.clear();
            }
        } else if (esVertical) {
            int minFila = casillasColocadasEsteTurno.stream().mapToInt(c -> c.getX()).min().orElse(filaInicial);
            int maxFila = casillasColocadasEsteTurno.stream().mapToInt(c -> c.getX()).max().orElse(filaInicial);

            StringBuilder palabraVert = new StringBuilder();
            List<Ficha> nuevaPalabra = new ArrayList<>();
            int currentRow = minFila;
            while (currentRow >= 0 && tablero.obtenerFicha(currentRow, colInicial) != null) {
                currentRow--;
            }
            currentRow++;

            while (currentRow < Tablero.FILAS && tablero.obtenerFicha(currentRow, colInicial) != null) {
                palabraVert.append(tablero.obtenerFicha(currentRow, colInicial).getLetra());
                nuevaPalabra.add(tablero.obtenerFicha(currentRow, colInicial));
                currentRow++;
            }
            if (palabraVert.length() >= 2) {
                palabrasEncontradas.add(palabraVert.toString());
                palabrasEnFichas.add(new ArrayList<>(nuevaPalabra));
            }
            nuevaPalabra.clear();

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
                    nuevaPalabra.add(tablero.obtenerFicha(currentRowHoriz, col));
                    col++;
                }
                if (palabraHoriz.length() >= 2) {
                    palabrasEncontradas.add(palabraHoriz.toString());
                    palabrasEnFichas.add(new ArrayList<>(nuevaPalabra));
                }
                nuevaPalabra.clear();
            }
        }
        int c = 0;
        for (String p : palabrasEncontradas) {
            if (juez.esValida(p)) {
                palabrasEnFichasValidas.add(palabrasEnFichas.get(c));
                palabrasValidas.add(p);
            } else {
                JOptionPane.showMessageDialog(null, "La palabra '" + p + "' no es válida en el diccionario.");
                return new ArrayList<>();
            }
            c++;
        }

        return palabrasEnFichasValidas;
    }
}
