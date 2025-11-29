package simulador.utils;

import simulador.process.Process;
import java.io.*;
import java.util.*;

/**
 * Parser para leer archivos de entrada de procesos
 */
public class FileParser {
    
    /**
     * Lee un archivo de procesos y retorna una lista de procesos
     * Formato: PID tiempo_llegada ráfagas prioridad páginas
     * Ejemplo: P1 0 CPU(4),E/S(3),CPU(5) 1 4
     */
    public static List<Process> parseProcessFile(String filePath) throws IOException {
        List<Process> processes = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Ignorar líneas vacías y comentarios
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                try {
                    Process process = parseLine(line);
                    processes.add(process);
                } catch (Exception e) {
                    throw new IOException("Error parsing line " + lineNumber + ": " + line, e);
                }
            }
        }
        
        return processes;
    }
    
    private static Process parseLine(String line) {
        String[] parts = line.split("\\s+");
        
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid format. Expected: PID arrivalTime bursts priority pages");
        }
        
        String pid = parts[0];
        int arrivalTime = Integer.parseInt(parts[1]);
        List<String> bursts = parseBursts(parts[2]);
        int priority = Integer.parseInt(parts[3]);
        int pages = Integer.parseInt(parts[4]);
        
        return new Process(pid, arrivalTime, bursts, priority, pages);
    }
    
    private static List<String> parseBursts(String burstsStr) {
        List<String> bursts = new ArrayList<>();
        String[] burstArray = burstsStr.split(",");
        
        for (String burst : burstArray) {
            burst = burst.trim();
            if (!burst.isEmpty()) {
                bursts.add(burst);
            }
        }
        
        return bursts;
    }
    
    /**
     * Valida que un archivo tenga el formato correcto
     */
    public static boolean validateFile(String filePath) {
        try {
            List<Process> processes = parseProcessFile(filePath);
            return !processes.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Lee la configuración del simulador desde un archivo
     * Formato:
     * FRAMES=10
     * SCHEDULER=RR
     * QUANTUM=3
     * MEMORY_ALGORITHM=LRU
     */
    public static SimulationConfig parseConfigFile(String filePath) throws IOException {
        SimulationConfig config = new SimulationConfig();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    switch (key) {
                        case "FRAMES":
                            config.totalFrames = Integer.parseInt(value);
                            break;
                        case "SCHEDULER":
                            config.schedulerType = value;
                            break;
                        case "QUANTUM":
                            config.quantum = Integer.parseInt(value);
                            break;
                        case "MEMORY_ALGORITHM":
                            config.memoryAlgorithm = value;
                            break;
                    }
                }
            }
        }
        
        return config;
    }
    
    /**
     * Clase para almacenar la configuración de la simulación
     */
    public static class SimulationConfig {
        public int totalFrames = 10;
        public String schedulerType = "FCFS";
        public int quantum = 3;
        public String memoryAlgorithm = "FIFO";
        
        @Override
        public String toString() {
            return String.format("Config[frames=%d, scheduler=%s, quantum=%d, memory=%s]",
                    totalFrames, schedulerType, quantum, memoryAlgorithm);
        }
    }
}
