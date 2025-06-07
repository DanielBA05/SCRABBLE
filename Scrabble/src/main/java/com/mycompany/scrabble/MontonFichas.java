package com.mycompany.scrabble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MontonFichas {
    private List<Ficha> fichas;
    
    // Cantidad de fichas por letra (ordenadas por puntaje)
    public static int cantidades[] = {12, 12, 9, 6, 6, 5, 4, 5, 5, 4,
                                      5, 2, 
                                      4, 2, 2, 2,  
                                      2, 1, 1, 1,  
                                      2,  
                                      1, 1, 1, 1, 1,  
                                      1}; 
    
    // Letras ordenadas por puntaje (no alfabéticamente)
    public static char letras [] = {'A', 'E', 'O', 'I', 'S', 'N', 'L', 'R', 'U', 'T',
                                    'D', 'G',
                                    'C', 'B', 'M', 'P',
                                    'H', 'F', 'V', 'Y',
                                    'Q', 
                                    'J', 'K', 'Ñ', 'W', 'X',
                                    'Z'};
    
    public MontonFichas() {
        fichas = new ArrayList<>();
        inicializarFichas();
        System.out.println("MontonFichas: Fichas iniciales en el mazo: " + fichas.size()); // Debe ser 100
    }
    
    // Inicializa las fichas con sus cantidades y puntajes correspondientes
    private void inicializarFichas(){
        for (int i = 0; i < letras.length; i++) {
            for (int j = 0; j < cantidades[i]; j++) {
                int puntaje = calcularPuntaje(i);
                fichas.add(new Ficha(letras[i], puntaje));
            }
        }

        // Añade 2 comodines
        fichas.add(new Ficha());
        fichas.add(new Ficha());

        Collections.shuffle(fichas); // Baraja las fichas
    }

    // Calcula el puntaje según la posición de la letra
    private int calcularPuntaje(int index) {
        if (index <= 9) return 1;    // A-T (1 punto)
        if (index <= 11) return 2;   // D-G (2 puntos)
        if (index <= 15) return 3;   // C-P (3 puntos)
        if (index <= 19) return 4;   // H-Y (4 puntos)
        if (index == 20) return 5;   // Q (5 puntos)
        if (index <= 25) return 8;   // J-X (8 puntos)
        return 10;                   // Z (10 puntos)
    }
    
    // Roba una ficha del montón
    public Ficha robarFicha(){
        if (fichas.isEmpty()) return null;
        Ficha robada = fichas.remove(0);
        System.out.println("Ficha robada: " + robada.getLetra());
        return robada;
    }
    
    // Devuelve la cantidad de fichas restantes
    public int getCantidadFichas() {
        return fichas.size();
    }
    
    // Devuelve fichas al montón y las baraja
    public void devolverFichas(List<Ficha> fichasDevueltas){
        fichas.addAll(fichasDevueltas);
        Collections.shuffle(fichas);
    }
}