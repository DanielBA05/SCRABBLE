package com.mycompany.scrabble;

public class Ficha {
    private char letra;
    private int puntos;
    
    public Ficha(char letras, int punto){
        this.letra=letras;
        this.puntos=punto;
    }
    
    public Ficha(Ficha original){
        this.letra=original.letra;
        this.puntos=original.puntos;
    }

    public char getLetra() {
        return letra;
    }

    public int getPuntos() {
        return puntos;
    }

}
