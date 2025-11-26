package simulador.utils;

import simulador.process.Process;
import java.io.*;
import java.util.*;

/**
 * Parser para leer archivos de procesos
 * Formato esperado: PID arrivalTime bursts priority pages [pageReferences]
 * Ejemplo: P1 0 CPU(4),IO(3),CPU(5) 1 4 [0,1,2,1,0,3,2]
 */
public class ProcessParser {
    
    public static List<Process> parseFile(String filename) throws IOException {
        List<Process> processes = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignorar líneas vacías y comentarios
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                Process process = parseLine(line);
                if (process != null) {
                    processes.add(process);
                }
            }
        }
        
        return processes;
    }
    
    private static Process parseLine(String line) {
        try {
            // Dividir por espacios, pero mantener el contenido entre paréntesis y corchetes
            String[] parts = line.split("\\s+");
            
            if (parts.length < 5) {
                System.err.println("Línea inválida (faltan campos): " + line);
                return null;
            }
            
            String pid = parts[0];
            int arrivalTime = Integer.parseInt(parts[1]);
            String burstsStr = parts[2];
            int priority = Integer.parseInt(parts[3]);
            int pages = Integer.parseInt(parts[4]);
            
            // Parsear ráfagas
            List<String> bursts = parseBursts(burstsStr);
            
            // Parsear referencias de páginas (opcional)
            List<Integer> pageReferences = new ArrayList<>();
            if (parts.length > 5 && parts[5].startsWith("[")) {
                pageReferences = parsePageReferences(parts[5]);
            } else {
                // Generar referencias aleatorias si no se proporcionan
                pageReferences = generateRandomReferences(pages, bursts.size() * 10);
            }
            
            return new Process(pid, arrivalTime, bursts, priority, pages, pageReferences);
            
        } catch (Exception e) {
            System.err.println("Error parseando línea: " + line);
            e.printStackTrace();
            return null;
        }
    }
    
    private static List<String> parseBursts(String burstsStr) {
        List<String> bursts = new ArrayList<>();
        
        // Ejemplo: CPU(4),IO(3),CPU(5)
        String[] parts = burstsStr.split(",");
        
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                bursts.add(part);
            }
        }
        
        return bursts;
    }
    
    private static List<Integer> parsePageReferences(String refsStr) {
        List<Integer> refs = new ArrayList<>();
        
        // Quitar corchetes y dividir por comas
        refsStr = refsStr.replace("[", "").replace("]", "");
        String[] parts = refsStr.split(",");
        
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                refs.add(Integer.parseInt(part));
            }
        }
        
        return refs;
    }
    
    private static List<Integer> generateRandomReferences(int maxPage, int count) {
        List<Integer> refs = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            refs.add(random.nextInt(maxPage));
        }
        
        return refs;
    }
    
    /**
     * Valida que un archivo de procesos tenga el formato correcto
     */
    public static boolean validateFile(String filename) {
        try {
            List<Process> processes = parseFile(filename);
            return !processes.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
