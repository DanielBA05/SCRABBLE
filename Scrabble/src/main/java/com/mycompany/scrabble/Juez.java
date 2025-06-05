package com.mycompany.scrabble;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Juez {
    private Set<String> diccionario;
    //Constructor que carga el diccionario desde un archivo de recursos
    public Juez() {
        diccionario = new HashSet<>();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("Diccionario.txt");
            if (is == null) {
                System.out.println("No se encontró el diccionario.");
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String linea;
            while ((linea = br.readLine()) != null) {
                diccionario.add(linea.trim().toLowerCase()); // Manejo de errores para detectar problemas en la carga del archivo.
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace(); // Manejo de errores para detectar problemas en la carga del archivo.
        }
    }

    public boolean esValida(String palabra) {
        return diccionario.contains(palabra.toLowerCase());
    }
}
