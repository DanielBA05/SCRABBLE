package com.mycompany.scrabble;

public class Casilla {
    private int x;
    private int y;
    private Ficha ficha;

    // Bonificadores
    public static final int NONE = 0;
    public static final int DOUL = 1;   // Doble letra
    public static final int TRIPL = 2;  // Triple letra
    public static final int DOUP = 3;   // Doble palabra
    public static final int TRIPP = 4;  // Triple palabra

    private int multiplier;

    public Casilla(int x, int y, int multiplier) {
        this.x = x;
        this.y = y;
        this.multiplier = multiplier;
        this.ficha = null;

    }
    
    public Casilla(Casilla original) {
        this.x = original.x;
        this.y = original.y;
        this.multiplier = original.multiplier;
        this.ficha = original.ficha != null ? new Ficha(original.ficha) : null;
    }
    
    public void quitarFicha() {
    this.ficha = null;
}
    public int getX() {
        return x; 
    }
    public int getY() {
        return y;
    }
    public Ficha getFicha() {
        return ficha; 
    }
    public void setFicha(Ficha ficha) {
        this.ficha = ficha;
    }
    public int getMultiplier() {
        return multiplier;
    }
    public void disableMultiplier(){
        this.multiplier = 0;
    }
}
