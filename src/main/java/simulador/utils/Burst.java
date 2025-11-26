package simulador.utils;

/**
 * Representa una ráfaga de CPU o E/S
 */
public class Burst {
    
    public enum Type {
        CPU, IO
    }
    
    private final Type type;
    private final int duration;
    private int remainingTime;
    
    public Burst(Type type, int duration) {
        this.type = type;
        this.duration = duration;
        this.remainingTime = duration;
    }
    
    public Type getType() {
        return type;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public int getRemainingTime() {
        return remainingTime;
    }
    
    public void execute(int time) {
        remainingTime = Math.max(0, remainingTime - time);
    }
    
    public boolean isCompleted() {
        return remainingTime <= 0;
    }
    
    public void reset() {
        remainingTime = duration;
    }
    
    @Override
    public String toString() {
        return type + "(" + duration + ")";
    }
    
    /**
     * Parsea una cadena de ráfaga como "CPU(4)" o "IO(3)"
     */
    public static Burst parse(String burstStr) {
        burstStr = burstStr.trim().toUpperCase();
        
        Type type;
        if (burstStr.startsWith("CPU")) {
            type = Type.CPU;
        } else if (burstStr.startsWith("IO") || burstStr.startsWith("E/S") || burstStr.startsWith("ES")) {
            type = Type.IO;
        } else {
            throw new IllegalArgumentException("Formato de ráfaga inválido: " + burstStr);
        }
        
        // Extraer el número entre paréntesis
        int start = burstStr.indexOf('(');
        int end = burstStr.indexOf(')');
        
        if (start == -1 || end == -1) {
            throw new IllegalArgumentException("Formato de ráfaga inválido: " + burstStr);
        }
        
        String durationStr = burstStr.substring(start + 1, end).trim();
        int duration = Integer.parseInt(durationStr);
        
        return new Burst(type, duration);
    }
}
