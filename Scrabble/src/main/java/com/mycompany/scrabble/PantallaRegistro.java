package com.mycompany.scrabble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class PantallaRegistro extends JFrame {
    private JComboBox<Integer> selectorJugadores;
    private JButton botonConfirmarCantidad;
    private List<JTextField> camposNombres;
    private List<JButton> botonesFichas;
    private List<Ficha> fichasJugadores;
    private JTextArea resultadoArea;
    private JButton botonIniciar;
    private MontonFichas monton;
    private List<Jugador> ordenJugadores;


    public PantallaRegistro() {
        setTitle("Registro de Jugadores - SCRABBLE");
        setSize(550, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        monton = new MontonFichas();

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        JPanel panelJugadores = new JPanel(new GridLayout(4, 2, 10, 10));
        camposNombres = new ArrayList<>();
        botonesFichas = new ArrayList<>();
        fichasJugadores = new ArrayList<>(Arrays.asList(null, null, null, null));

        // Título
        JLabel titulo = new JLabel("SCRABBLE", JLabel.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 36));
        titulo.setForeground(Color.BLUE);
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // Selector de cantidad de jugadores y botón confirmar
        JPanel panelSelector = new JPanel();
        panelSelector.add(new JLabel("Cantidad de jugadores:"));
        selectorJugadores = new JComboBox<>(new Integer[]{2, 3, 4});
        selectorJugadores.setSelectedIndex(0);
        panelSelector.add(selectorJugadores);

        botonConfirmarCantidad = new JButton("Confirmar cantidad");
        botonConfirmarCantidad.addActionListener(e -> confirmarCantidadJugadores());
        panelSelector.add(botonConfirmarCantidad);

        panelPrincipal.add(panelSelector, BorderLayout.CENTER);

        // Campos para jugadores y botones de sacar ficha
        for (int i = 0; i < 4; i++) {
            JTextField campo = new JTextField();
            campo.setBorder(BorderFactory.createTitledBorder("Jugador " + (i + 1)));
            campo.setEnabled(false);
            camposNombres.add(campo);

            JButton botonFicha = new JButton("Sacar ficha");
            botonFicha.setEnabled(false);
            int index = i;
            botonFicha.addActionListener(ev -> sacarFicha(index));
            botonesFichas.add(botonFicha);

            JPanel panelFila = new JPanel(new BorderLayout());
            panelFila.add(campo, BorderLayout.CENTER);
            panelFila.add(botonFicha, BorderLayout.EAST);

            panelJugadores.add(panelFila);
        }

        panelPrincipal.add(panelJugadores, BorderLayout.SOUTH);

        // Área de resultados
        resultadoArea = new JTextArea(6, 40);
        resultadoArea.setEditable(false);
        resultadoArea.setBorder(BorderFactory.createTitledBorder("Resultado del sorteo"));

        // Botón para iniciar el juego
        botonIniciar = new JButton("Iniciar juego");
        botonIniciar.addActionListener(this::iniciarJuego);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(new JScrollPane(resultadoArea), BorderLayout.CENTER);
        panelInferior.add(botonIniciar, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.NORTH);
        add(panelInferior, BorderLayout.CENTER);

        // No activamos campos hasta confirmar cantidad
        setVisible(true);
    }

    private void confirmarCantidadJugadores() {
        int cantidad = (Integer) selectorJugadores.getSelectedItem();
        selectorJugadores.setEnabled(false);
        botonConfirmarCantidad.setEnabled(false);

        for (int i = 0; i < 4; i++) {
            camposNombres.get(i).setEnabled(i < cantidad);
            botonesFichas.get(i).setEnabled(i < cantidad);
            camposNombres.get(i).setText("");
            botonesFichas.get(i).setText("Sacar ficha");
            fichasJugadores.set(i, null);
        }
        resultadoArea.setText("");
    }

    private void sacarFicha(int index) {
        int cantidad = (Integer) selectorJugadores.getSelectedItem();

        // Verifica que todos los campos de nombre activos estén llenos
        for (int i = 0; i < cantidad; i++) {
            if (camposNombres.get(i).getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los jugadores deben ingresar su nombre antes de sacar fichas.");
                return;
            }
        }

        if (!camposNombres.get(index).isEnabled()) return;

        Ficha f = monton.robarFicha();
        fichasJugadores.set(index, f);
        botonesFichas.get(index).setText("Sacó: " + f.getLetra());
        botonesFichas.get(index).setEnabled(false); // ✅ Deshabilita el botón

        actualizarResultadoSalida();
    }

    private void iniciarJuego(ActionEvent e) {
        int cantidad = (Integer) selectorJugadores.getSelectedItem();
        List<Jugador> jugadores = new ArrayList<>();
        ordenJugadores = new ArrayList<>(jugadores);
        Set<Character> letrasSacadas = new HashSet<>();
        resultadoArea.setText("");

        for (int i = 0; i < cantidad; i++) {
            String nombre = camposNombres.get(i).getText().trim();
            Ficha ficha = fichasJugadores.get(i);
            if (nombre.isEmpty() || ficha == null) {
                resultadoArea.setText("Todos los jugadores deben ingresar su nombre y sacar una ficha.");
                return;
            }
            jugadores.add(new Jugador(nombre));
            if (!letrasSacadas.add(ficha.getLetra())) {
                resultadoArea.setText("Fichas repetidas detectadas. Se debe repetir el sorteo.");

                // Reiniciar fichas y botones para que vuelvan a sacar
                for (int j = 0; j < cantidad; j++) {
                    fichasJugadores.set(j, null);
                    botonesFichas.get(j).setText("Sacar ficha");
                    botonesFichas.get(j).setEnabled(true); // ✅ Rehabilita el botón
                }
                return;
            }
        }

        // Ordenar jugadores según la ficha
        jugadores.sort((a, b) -> {
            Ficha fa = fichasJugadores.get(jugadores.indexOf(a));
            Ficha fb = fichasJugadores.get(jugadores.indexOf(b));
            return compararFichas(fa, fb);
        });

        StringBuilder sb = new StringBuilder("Orden de salida:\n");
        for (int i = 0; i < jugadores.size(); i++) {
            sb.append((i + 1)).append(". ")
              .append(jugadores.get(i).getNombre())
              .append(" (").append(fichasJugadores.get(i).getLetra()).append(")\n");
        }

        resultadoArea.setText(sb.toString());

        Tablero tablero = new Tablero();
        new TableroVistaSwing(tablero);
        dispose();
    }

    private void actualizarResultadoSalida() {
        int cantidad = (Integer) selectorJugadores.getSelectedItem();
        List<Jugador> jugadores = new ArrayList<>();
        Map<Jugador, Ficha> mapaFichas = new HashMap<>();
        Set<Character> letrasSacadas = new HashSet<>();

        for (int i = 0; i < cantidad; i++) {
            String nombre = camposNombres.get(i).getText().trim();
            Ficha ficha = fichasJugadores.get(i);
            if (nombre.isEmpty() || ficha == null) {
                resultadoArea.setText("Esperando que todos los jugadores ingresen nombre y saquen ficha...");
                return;
            }
            Jugador jugador = new Jugador(nombre);
            jugadores.add(jugador);
            mapaFichas.put(jugador, ficha);
            if (!letrasSacadas.add(ficha.getLetra())) {
                resultadoArea.setText("Fichas repetidas detectadas. Se debe repetir el sorteo.");
                return;
            }
        }

        jugadores.sort((a, b) -> {
            Ficha fa = mapaFichas.get(a);
            Ficha fb = mapaFichas.get(b);
            return compararFichas(fa, fb);
        });

        StringBuilder sb = new StringBuilder("Orden de salida (parcial):\n");
        for (int i = 0; i < jugadores.size(); i++) {
            Ficha ficha = mapaFichas.get(jugadores.get(i));
            sb.append((i + 1)).append(". ")
              .append(jugadores.get(i).getNombre())
              .append(" (").append(ficha.getLetra()).append(")\n");
        }

        resultadoArea.setText(sb.toString());
    }

    private int compararFichas(Ficha f1, Ficha f2) {
        List<Character> alfabetoEspanol = Arrays.asList(
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','ñ','o','p','q','r','s','t','u','v','w','x','y','z'
        );

        char letra1 = Character.toLowerCase(f1.getLetra());
        char letra2 = Character.toLowerCase(f2.getLetra());

        // Prioriza la ficha blanca '-'
        if (letra1 == '-' && letra2 != '-') return -1;
        if (letra2 == '-' && letra1 != '-') return 1;

        int pos1 = alfabetoEspanol.indexOf(letra1);
        int pos2 = alfabetoEspanol.indexOf(letra2);

        return Integer.compare(pos1, pos2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PantallaRegistro::new);
    }
}
