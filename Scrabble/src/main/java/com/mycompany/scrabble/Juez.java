import java.nio.file.*;
import java.util.*;
import java.io.IOException;


//uso hash pq has es lo mejor O(1) y god
public class Juez {
    private Set<String> diccionario; // para setear usando hash

    public Juez(String rutaArchivo) throws IOException { //uso el throw para evitarme chorrocientas líneas de error
        diccionario = new HashSet<>(); // un hashset, again
        for (String linea : Files.readAllLines(Paths.get(rutaArchivo))) { //obtener línea por línea, y en mayúsculas para no confundir al contains
            diccionario.add(linea.toLowerCase());
        }
    }

    public boolean esValida(String palabra) { //validadorinador3000
        return diccionario.contains(palabra.toLowerCase());
    }
}
