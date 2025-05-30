package com.mycompany.scrabble;
import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private String nombre;
    private List<Ficha> fichas;
    private int puntos;
    
    public Jugador(String nombre) {
        this.nombre = nombre;
        this.fichas = new ArrayList<>();
        this.puntos = 0;
    }
    // Suma puntos a los puntos del jugador
    public void sumarPuntos(int puntosASumar) {
        this.puntos += puntosASumar;
    }

    // Agrega una ficha a la lista del jugador
    public void agregarFicha(Ficha ficha) {
        fichas.add(ficha);
    }

    // Elimina una ficha de la lista del jugador
    public void removerFicha(Ficha ficha) {
        fichas.remove(ficha);
    }

    // Devuelve la lista de fichas actuales del jugador
    public List<Ficha> getFichas() {
        return fichas;
    }

    // Elimina todas las fichas del jugador (reinicio)
    public void resetFichas() {
        this.fichas.clear();
    }

    // Devuelve el nombre del jugador
    public String getNombre() {
        return nombre;
    }

    // Devuelve la cantidad de puntos acumulados
    public int getPuntos() {
        return puntos;
    }
}
