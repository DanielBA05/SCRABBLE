
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
    }
    
    
    private void inicializarFichas(){
        for (int i = 0; i < 27; i++){ //nos va a servir como indice para tanto letras como cantidades
            for (int j = 1; j <= cantidades[i]; j++){ //esto va a ser para que se hagan la cantidad de repeticiones determinada para cada letra
                if ((i >= 0) && (i <= 9)){ //es decir, si esta en el primer grupo de letras
                    fichas.add(new Ficha(letras[i], 1)); //si está entre A y T (esas incluidas), vale 1, asi que ponemos eso en la parte de puntos
                } else if (i == 10 || i == 11){ 
                    fichas.add(new Ficha(letras[i], 2)); //D y G valen 2
                } else if (i >= 12 && i <= 15){
                    fichas.add(new Ficha(letras[i], 3)); //entre C y P valen 3
                } else if (i >= 16 && i <= 19){
                    fichas.add(new Ficha(letras[i], 4)); //entre H y Y valen 4
                } else if (i == 20){
                    fichas.add(new Ficha(letras[i], 5)); //Q es 5
                } else if (i >= 21 && i <= 25){
                    fichas.add(new Ficha(letras[i], 8)); //entre J y X valen 8
                } else {
                    fichas.add(new Ficha(letras[i], 10)); //y Z vale 10
                }
            }
        }
        
    fichas.add(new Ficha('-', 0)); //agregamos dos comodines que no sabia como nombrar
    fichas.add(new Ficha('-', 0));
    
    Collections.shuffle(fichas); //y revolvemos la lista
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
