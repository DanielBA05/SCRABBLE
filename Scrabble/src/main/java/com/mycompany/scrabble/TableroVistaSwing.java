package com.mycompany.scrabble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TableroVistaSwing extends JFrame {
    private Juego juego;
    private Tablero tablero;

    private static final int TAM_CASILLA_BASE = 40;
    private int tamCasillaActual = TAM_CASILLA_BASE;

    private JPanel panelTablero;
    private JPanel panelIzquierdo;
    private JPanel panelContenedorTablero;

    private JLabel jugadorActualLabel;
    private JPanel atrilPanel;
    private JTextArea jugadoresArea;

    private JButton btnTerminarTurno;
    private JButton btnPasarTurno;
    private JButton btnCambiarFichas;
    private JButton btnReiniciarJugada;

    public TableroVistaSwing(Juego juego) {
        this.juego = juego;
        this.tablero = juego.getTablero();

        setTitle("Scrabble - Juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(
            TAM_CASILLA_BASE * Tablero.COLUMNAS + 350, // Aumentado para mejor visualización
            TAM_CASILLA_BASE * Tablero.FILAS
        ));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Panel contenedor del tablero para centrarlo
        panelContenedorTablero = new JPanel(new GridBagLayout());
        panelContenedorTablero.setBackground(Color.GRAY);

        // Panel tablero (cuadrado)
        panelTablero = new JPanel(new GridLayout(Tablero.FILAS, Tablero.COLUMNAS));
        actualizarTablero();

        // Panel izquierdo con datos y botones (más ancho para mejor visualización)
        panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BorderLayout());
        panelIzquierdo.setPreferredSize(new Dimension(350, 0));
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Arriba: Jugador actual y atril
        JPanel panelArribaIzq = new JPanel(new BorderLayout());
        panelArribaIzq.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        jugadorActualLabel = new JLabel();
        jugadorActualLabel.setFont(new Font("Arial", Font.BOLD, 18));
        jugadorActualLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jugadorActualLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        panelArribaIzq.add(jugadorActualLabel, BorderLayout.NORTH);

        // Configuración mejorada del atril
        atrilPanel = new JPanel();
        atrilPanel.setLayout(new BoxLayout(atrilPanel, BoxLayout.X_AXIS));
        atrilPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JScrollPane scrollAtril = new JScrollPane(atrilPanel);
        scrollAtril.setPreferredSize(new Dimension(330, 120));
        scrollAtril.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollAtril.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelArribaIzq.add(scrollAtril, BorderLayout.CENTER);

        panelIzquierdo.add(panelArribaIzq, BorderLayout.NORTH);

        // Centro: Botones con mejor espaciado
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 5, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnTerminarTurno = new JButton("Terminar Turno");
        btnPasarTurno = new JButton("Pasar Turno");
        btnCambiarFichas = new JButton("Cambiar Fichas");
        btnReiniciarJugada = new JButton("Reiniciar Jugada");

        // Estilo consistente para los botones
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        btnTerminarTurno.setFont(buttonFont);
        btnPasarTurno.setFont(buttonFont);
        btnCambiarFichas.setFont(buttonFont);
        btnReiniciarJugada.setFont(buttonFont);

        panelBotones.add(btnTerminarTurno);
        panelBotones.add(btnPasarTurno);
        panelBotones.add(btnCambiarFichas);
        panelBotones.add(btnReiniciarJugada);

        panelIzquierdo.add(panelBotones, BorderLayout.CENTER);

        // Abajo: Orden de jugadores con mejor formato
        jugadoresArea = new JTextArea();
        jugadoresArea.setEditable(false);
        jugadoresArea.setFont(new Font("Arial", Font.PLAIN, 14));
        jugadoresArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollJugadores = new JScrollPane(jugadoresArea);
        scrollJugadores.setPreferredSize(new Dimension(330, 180));
        panelIzquierdo.add(scrollJugadores, BorderLayout.SOUTH);

        // Agregar a frame
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelContenedorTablero, BorderLayout.CENTER);

        // Cargar datos iniciales
        actualizarTodo();

        // Agregar eventos botones
        agregarEventosBotones();

        // Listener para redimensionamiento
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ajustarTamanioTablero();
            }
        });
    }

    private void ajustarTamanioTablero() {
        int altoDisponible = panelContenedorTablero.getHeight();
        int anchoDisponible = panelContenedorTablero.getWidth();

        int ladoMaximo = Math.min(altoDisponible, anchoDisponible);
        tamCasillaActual = ladoMaximo / Tablero.FILAS;

        panelTablero.setPreferredSize(new Dimension(
            tamCasillaActual * Tablero.COLUMNAS,
            tamCasillaActual * Tablero.FILAS
        ));

        panelContenedorTablero.removeAll();
        panelContenedorTablero.add(panelTablero);
        panelContenedorTablero.revalidate();
        panelContenedorTablero.repaint();

        actualizarTablero();
    }

    private void ajustarTamanioAtril() {
        int anchoDisponible = panelIzquierdo.getWidth() - 40; // Margen
        List<Ficha> fichas = juego.getFichasJugadorActual();
        int cantidadFichas = fichas.size();
        
        if (cantidadFichas > 0) {
            int anchoBoton = Math.min(80, anchoDisponible / Math.max(7, cantidadFichas));
            anchoBoton = Math.max(50, anchoBoton); // Mínimo 50px
            
            for (Component comp : atrilPanel.getComponents()) {
                if (comp instanceof JButton) {
                    comp.setPreferredSize(new Dimension(anchoBoton, 60));
                }
            }
            
            atrilPanel.revalidate();
            atrilPanel.repaint();
        }
    }

    private void actualizarTodo() {
        actualizarJugadorActual();
        actualizarAtril();
        actualizarTablero();
        actualizarOrdenJugadores();
    }

    private void actualizarJugadorActual() {
    Jugador jugadorActual = juego.getJugadorActual();
    jugadorActualLabel.setText("<html>Turno de: " + jugadorActual.getNombre() + 
                             "<br>Puntos: " + jugadorActual.getPuntos() + "</html>");
}

    private void actualizarAtril() {
        atrilPanel.removeAll();
        List<Ficha> fichas = juego.getFichasJugadorActual();
        List<Ficha> fichasSeleccionadas = juego.getFichasSeleccionadasParaCambio();

        final int ANCHO_FICHA = 50;
        final int ALTO_FICHA = 60;

        for (Ficha ficha : fichas) {
            JButton btnFicha = new JButton(ficha.getLetra() + "");
            btnFicha.setPreferredSize(new Dimension(ANCHO_FICHA, ALTO_FICHA));
            btnFicha.setMinimumSize(new Dimension(ANCHO_FICHA, ALTO_FICHA));
            btnFicha.setMaximumSize(new Dimension(ANCHO_FICHA, ALTO_FICHA));
            btnFicha.setFont(new Font("Arial", Font.BOLD, 24));

            // Estilo visual
            btnFicha.setMargin(new Insets(5, 5, 5, 5));
            btnFicha.setFocusPainted(false);
            btnFicha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            // Resaltar según selección
            if (ficha == juego.getFichaSeleccionada()) {
                btnFicha.setBackground(new Color(255, 255, 150));
                btnFicha.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else if (fichasSeleccionadas != null && fichasSeleccionadas.contains(ficha)) {
                btnFicha.setBackground(new Color(150, 255, 150));
                btnFicha.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            } else {
                btnFicha.setBackground(new Color(230, 230, 230));
            }

            btnFicha.addActionListener(e -> {
                if (juego.isModoSeleccionCambio()) {
                    if (fichasSeleccionadas.contains(ficha)) {
                        juego.deseleccionarFichaParaCambio(ficha);
                    } else {
                        juego.seleccionarFichaParaCambio(ficha);
                    }
                    actualizarAtril();
                } else {
                    if (juego.getFichaSeleccionada() == ficha) {
                        juego.setFichaSeleccionada(null);
                    } else {
                        juego.setFichaSeleccionada(ficha);
                    }
                    actualizarAtril();
                }
            });

            atrilPanel.add(btnFicha);
        }

        atrilPanel.revalidate();
        atrilPanel.repaint();
    }
    
    private int calcularAnchoFichaAtril(int cantidadFichas) {
        // Lógica para calcular el ancho basado en la cantidad de fichas
        if (cantidadFichas <= 3) return 80;
        if (cantidadFichas <= 5) return 70;
        if (cantidadFichas <= 7) return 60;
        return 50; // Para más de 7 fichas
    }

    private void actualizarTablero() {
        panelTablero.removeAll();

        for (int fila = 0; fila < Tablero.FILAS; fila++) {
            for (int col = 0; col < Tablero.COLUMNAS; col++) {
                Casilla casilla = tablero.obtenerCasilla(fila, col);
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setFont(new Font("Arial", Font.BOLD, calcularTamanoFuente()));

                Ficha ficha = casilla.getFicha();
                if (ficha != null) {
                    label.setText(ficha.getLetra() + "");
                    label.setForeground(Color.BLACK);
                }

                switch (casilla.getMultiplier()) {
                    case Casilla.DOUL:
                        label.setBackground(new Color(173, 216, 230));
                        if (ficha == null) label.setText("DL");
                        break;
                    case Casilla.TRIPL:
                        label.setBackground(new Color(0, 102, 204));
                        if (ficha == null) {
                            label.setText("TL");
                            label.setForeground(Color.WHITE);
                        }
                        break;
                    case Casilla.DOUP:
                        label.setBackground(new Color(255, 182, 193));
                        if (ficha == null) label.setText("DP");
                        break;
                    case Casilla.TRIPP:
                        label.setBackground(new Color(255, 69, 0));
                        if (ficha == null) {
                            label.setText("TP");
                            label.setForeground(Color.WHITE);
                        }
                        break;
                    default:
                        label.setBackground(Color.WHITE);
                }

                final int f = fila;
                final int c = col;
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Ficha seleccionada = juego.getFichaSeleccionada();
                        if (seleccionada != null && casilla.getFicha() == null) {
                            boolean colocada = juego.colocarFichaEnTablero(seleccionada, f, c);
                            if (colocada) {
                                juego.getFichasColocadasEsteTurno().add(casilla);
                                juego.setFichaSeleccionada(null);
                                actualizarTodo();
                            } else {
                                JOptionPane.showMessageDialog(null, "No se puede colocar aquí.");
                            }
                        }
                    }
                });

                panelTablero.add(label);
            }
        }

        panelTablero.revalidate();
        panelTablero.repaint();
    }

    private int calcularTamanoFuente() {
        return Math.max(12, tamCasillaActual / 2);
    }

    private void actualizarOrdenJugadores() {
        StringBuilder sb = new StringBuilder("Orden de jugadores:\n");
        List<Jugador> jugadoresOrdenados = juego.getJugadores();
        for (int i = 0; i < jugadoresOrdenados.size(); i++) {
            sb.append(i + 1).append(". ").append(jugadoresOrdenados.get(i).getNombre()).append("\n");
        }
        jugadoresArea.setText(sb.toString());
    }
    
    private void agregarEventosBotones() {
        btnTerminarTurno.addActionListener(e -> {
            boolean exito = juego.terminarTurno(juego.getFichasColocadasEsteTurno(), juego.juez);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Turno terminado correctamente.");
                actualizarTodo();
            } else {
                JOptionPane.showMessageDialog(this, "Jugada inválida.");
            }
        });

        btnPasarTurno.addActionListener(e -> {
            juego.reiniciarJugada();
            actualizarTodo();
            juego.siguienteTurno();
            actualizarTodo();
        });

        btnCambiarFichas.addActionListener(e -> {
            juego.cambiarFicha();
            actualizarTodo();

            // Actualizar el texto del botón según el modo
            if (juego.isModoSeleccionCambio()) {
                btnCambiarFichas.setText("Confirmar Cambio");
                btnTerminarTurno.setEnabled(false);
                btnPasarTurno.setEnabled(false);
                btnReiniciarJugada.setEnabled(false);
            } else {
                btnCambiarFichas.setText("Cambiar Fichas");
                btnTerminarTurno.setEnabled(true);
                btnPasarTurno.setEnabled(true);
                btnReiniciarJugada.setEnabled(true);
            }
        });

        btnReiniciarJugada.addActionListener(e -> {
            juego.reiniciarJugada();
            actualizarTodo();
        });
    }
}