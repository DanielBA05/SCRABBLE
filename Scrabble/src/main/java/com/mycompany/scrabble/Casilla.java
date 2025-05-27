/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.scrabble;

/**
 *
 * @author jos23
 */
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
    private boolean bonificadorUsado;

    public Casilla(int x, int y, int multiplier) {
        this.x = x;
        this.y = y;
        this.multiplier = multiplier;
        this.ficha = null;
        this.bonificadorUsado = false;
    }

    // Getters y setters
    public int getX() {
        return x; }
    public int getY() {
        return y; }
    public Ficha getFicha() {
        return ficha; }
    public void setFicha(Ficha ficha) {
        this.ficha = ficha; }
    public int getMultiplier() {
        return multiplier; }
    public boolean isBonificadorUsado() {
        return bonificadorUsado; }
    public void setBonificadorUsado(boolean usado) { 
        this.bonificadorUsado = usado; }
}
