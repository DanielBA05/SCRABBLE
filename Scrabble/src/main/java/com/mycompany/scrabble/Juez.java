package com.mycompany.scrabble;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Juez {
    // Almacena las palabras válidas del diccionario
    private Set<String> diccionario;

    // Carga el diccionario desde el archivo "Diccionario.txt"
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
                // Añade cada palabra en minúsculas al diccionario
                diccionario.add(linea.trim().toLowerCase());
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Verifica si una palabra existe en el diccionario (ignorando mayúsculas)
    public boolean esValida(String palabra) {
        return diccionario.contains(palabra.toLowerCase());
    }
}