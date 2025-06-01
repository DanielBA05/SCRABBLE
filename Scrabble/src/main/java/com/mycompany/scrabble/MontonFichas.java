
package com.mycompany.scrabble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MontonFichas {
    private List<Ficha> fichas;
    
    public static int cantidades[] = {12, 12, 9, 6, 6, 5, 4, 5, 5, 4, //a pesar de que se vea raro, tenerlos divididos en base a cuando cambian de puntaje permite perderse menos
                                      5, 2, 
                                      4, 2, 2, 2,  
                                      2, 1, 1, 1,  
                                      2,  
                                      1, 1, 1, 1, 1,  
                                      1}; 
    
    public static char letras [] = {'A', 'E', 'O', 'I', 'S', 'N', 'L', 'R', 'U', 'T', //la aclaración respecto al orden se mantiene. Además, es importante aclarar que las letras NO ESTÁN ORDENADAS ALFABÉTICAMENTE, sino en base a sus puntajes para mayor facilidad
                                    'D', 'G',
                                    'C', 'B', 'M', 'P',
                                    'H', 'F', 'V', 'Y',
                                    'Q', 
                                    'J', 'K', 'Ñ', 'W', 'X',
                                    'Z'};
    
    public MontonFichas() {
    fichas = new ArrayList<>();
    inicializarFichas();
    System.out.println("MontonFichas: Fichas iniciales en el mazo: " + fichas.size()); // DEBERÍA SER 100
}
    
    
    private void inicializarFichas(){
        for (int i = 0; i < letras.length; i++){
            for (int j = 0; j < cantidades[i]; j++){
                int puntaje;

                if (i <= 9) { // A, E, O, I, S, N, L, R, U, T
                    puntaje = 1;
                } else if (i <= 11) { // D, G
                    puntaje = 2;
                } else if (i <= 15) { // C, B, M, P
                    puntaje = 3;
                } else if (i <= 19) { // H, F, V, Y
                    puntaje = 4;
                } else if (i == 20) { // Q
                    puntaje = 5;
                } else if (i <= 25) { // J, K, Ñ, W, X
                    puntaje = 8;
                } else { // Z
                    puntaje = 10;
                }

                fichas.add(new Ficha(letras[i], puntaje));
            }
        }

        // Agregar comodines
        fichas.add(new Ficha());
        fichas.add(new Ficha());

        Collections.shuffle(fichas);
    }

    
   
    public Ficha robarFicha(){
        if (fichas.isEmpty()){ 
            return null; //si no existen más fichas, no hay nada que pueda robar
        }
        Ficha robada = fichas.remove(0); //como ya está barajado, no importa cuál tomemos
        System.out.println("Ficha robada: " + robada.getLetra()); //por si quisieramos ver en terminal
        return robada; //retornamos la ficha
    }
    public int getCantidadFichas() {
        return fichas.size();
    }
    
    public void devolverFichas(List<Ficha> fichasDevueltas){
        for (Ficha devuelta : fichasDevueltas){ //para cada ficha devuelta 
            fichas.add(devuelta); //se vuelve a agregar al montón
        }
        Collections.shuffle(fichas); //y se vuelve a barajar 
    }
}
