package com.mycompany.scrabble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors; // Aunque ya no se usa, lo mantendremos por si acaso.
import java.io.IOException; // Esta importación puede volverse innecesaria si no se lanza IOException.
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class PantallaRegistro extends JFrame {
    private JComboBox<Integer> selectorJugadores;
    private JButton botonConfirmarCantidad;
    private List<JTextField> camposNombres;
    private List<JButton> botonesFichas;
    private List<Ficha> fichasJugadores;
    private JTextArea resultadoArea;
    private JButton botonIniciar;
    private MontonFichas monton;
    private List<Jugador> ordenJugadores; // Esta lista guardará los objetos Jugador ya ordenados
    private Juego juego;

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
                for (int j = 0; j < cantidad; j++) {
                    fichasJugadores.set(j, null);
                    botonesFichas.get(j).setText("Sacar ficha");
                    botonesFichas.get(j).setEnabled(true);
                }
                return;
            }
        }

        // Ordenar jugadores según ficha
        jugadores.sort((a, b) -> {
            // Se necesita una forma de asociar el jugador 'a' o 'b' con la ficha que sacó.
            // La forma actual 'jugadores.indexOf(a)' puede ser ineficiente si la lista es grande.
            // Para simplificar, asumiremos que el índice en 'jugadores' corresponde al de 'fichasJugadores'.
            // Sin embargo, si 'jugadores' ha sido reordenado o filtrado, esto podría fallar.
            // Una solución más robusta sería usar un Map<Jugador, Ficha> desde el principio.
            // Por ahora, para que funcione, nos aseguraremos de que 'jugadores' y 'fichasJugadores' estén alineados
            // o que la asociación Ficha-Jugador se haga de otra manera.

            // La forma más robusta es como lo haces en actualizarResultadoSalida(), usando un mapa.
            // Pero como 'fichasJugadores' está indexado directamente a los campos de texto,
            // y 'jugadores' se crea en orden de los campos de texto ANTES de ser ordenado,
            // el '.indexOf(a)' aquí puede ser problemático si 'jugadores' ya fue alterado.

            // Para este punto, 'jugadores' NO está ordenado, y 'fichasJugadores' SÍ guarda la ficha de cada índice.
            // La lógica para la comparación necesita el índice ORIGINAL del jugador.
            // Una manera de hacerlo sería mantener un mapa temporal o asociar la ficha al Jugador.
            // Para mantener la lógica actual, asumiremos que 'jugadores' y 'fichasJugadores'
            // guardan la relación por índice inicial.

            // Mejor aún: Creamos el mapa que ya usas en actualizarResultadoSalida para ser coherentes.
            Map<Jugador, Ficha> mapaFichasParaSorteo = new HashMap<>();
            for (int i = 0; i < cantidad; i++) {
                mapaFichasParaSorteo.put(jugadores.get(i), fichasJugadores.get(i));
            }

            Ficha fa = mapaFichasParaSorteo.get(a);
            Ficha fb = mapaFichasParaSorteo.get(b);
            return compararFichas(fa, fb);
        });


        // Guardar el orden para uso posterior
        ordenJugadores = new ArrayList<>(jugadores); // ¡Aquí se guarda el orden definitivo de Jugador!

        // Mostrar orden en el resultado de la pantalla de registro
        StringBuilder sb = new StringBuilder("Orden de salida:\n");
        for (int i = 0; i < ordenJugadores.size(); i++) {
            // Debes obtener la ficha del jugador en el orden actual para mostrarla.
            // Como 'fichasJugadores' ya no está alineado con 'ordenJugadores',
            // necesitamos encontrar la ficha original de cada jugador.
            // Para simplificar, podemos volver a usar 'mapaFichasParaSorteo'.
            Jugador jugadorActual = ordenJugadores.get(i);
            Ficha fichaDelJugador = null;
            for (int j = 0; j < cantidad; j++) {
                if (camposNombres.get(j).getText().trim().equals(jugadorActual.getNombre())) {
                    fichaDelJugador = fichasJugadores.get(j);
                    break;
                }
            }

            sb.append((i + 1)).append(". ")
              .append(jugadorActual.getNombre())
              .append(" (").append(fichaDelJugador != null ? fichaDelJugador.getLetra() : "?").append(")\n");
        }
        resultadoArea.setText(sb.toString());

        // Crear el objeto Juego con los JUGADORES ordenados
        // y sin la necesidad de la ruta del diccionario aquí
        try {
            //juego = new Juego(nombres, "ruta/al/diccionario.txt"); // LÍNEA ANTERIOR CON ERROR
            juego = new Juego(ordenJugadores); // ¡LÍNEA CORRECTA AHORA! Pasar la List<Jugador> ordenada
        } catch (Exception ex) { // Capturar Exception general, ya que Juez ya maneja IOException internamente.
            JOptionPane.showMessageDialog(this, "Error al iniciar el juego: " + ex.getMessage());
            ex.printStackTrace(); // Para depuración
            return;
        }

        // Crear y mostrar la vista del tablero
        TableroVistaSwing vista = new TableroVistaSwing(juego);

        dispose();
    }

    private void actualizarResultadoSalida() {
        int cantidad = (Integer) selectorJugadores.getSelectedItem();
        List<Jugador> jugadoresActuales = new ArrayList<>(); // Usamos un nombre diferente para evitar confusión
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
            jugadoresActuales.add(jugador);
            mapaFichas.put(jugador, ficha);
            if (!letrasSacadas.add(ficha.getLetra())) {
                resultadoArea.setText("Fichas repetidas detectadas. Se debe repetir el sorteo.");
                // Restablecer botones y fichas para un nuevo sorteo
                for (int j = 0; j < cantidad; j++) {
                    fichasJugadores.set(j, null);
                    botonesFichas.get(j).setText("Sacar ficha");
                    botonesFichas.get(j).setEnabled(true);
                }
                return;
            }
        }

        jugadoresActuales.sort((a, b) -> {
            Ficha fa = mapaFichas.get(a);
            Ficha fb = mapaFichas.get(b);
            return compararFichas(fa, fb);
        });

        StringBuilder sb = new StringBuilder("Orden de salida (parcial):\n");
        for (int i = 0; i < jugadoresActuales.size(); i++) {
            Ficha ficha = mapaFichas.get(jugadoresActuales.get(i));
            sb.append((i + 1)).append(". ")
              .append(jugadoresActuales.get(i).getNombre())
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