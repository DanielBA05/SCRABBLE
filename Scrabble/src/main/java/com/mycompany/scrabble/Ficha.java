package com.mycompany.scrabble;

public class Ficha {
    private char letra;
    private int puntos;
    private boolean esComodin;
    private Casilla lugar;

    public Ficha(char letra, int puntos) {
        this.letra = letra;
        this.puntos = puntos;
        this.esComodin = false;
        this.lugar = null;
    }

    // Constructor para comodín
    public Ficha() {
        this.letra = '-';
        this.puntos = 0;
        this.esComodin = true;
        this.lugar = null;
    }

    // Constructor copia
    public Ficha(Ficha original) {
        this.letra = original.letra;
        this.puntos = original.puntos;
        this.esComodin = original.esComodin;
        this.lugar = original.lugar;
    }
    // métodos get y set para obtener las variables de la ficha o cambiarlas
    public char getLetra() {
        return letra;
    }
    
    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public void setLetra(char letra) {
        this.letra = letra;
    }

    public int getPuntos() {
        return puntos;
    }

    public boolean esComodin() {
        return esComodin;
    }

    public void setEsComodin(boolean esComodin) {
        this.esComodin = esComodin;
    }
    
    public void setLugar(Casilla casilla){
        this.lugar = casilla;
    }
    
    public Casilla getLugar(){
        return this.lugar;
    }
}
