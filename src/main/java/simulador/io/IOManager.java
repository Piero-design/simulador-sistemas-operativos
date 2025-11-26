package simulador.io;

import simulador.process.Process;
import java.util.concurrent.*;
import java.util.*;

/**
 * Gestiona las operaciones de E/S de los procesos
 */
public class IOManager {
    
    private final ExecutorService ioExecutor;
    private final Map<String, IOOperation> activeOperations;
    private final BlockingQueue<IOCompletion> completionQueue;

    public IOManager() {
        this.ioExecutor = Executors.newCachedThreadPool();
        this.activeOperations = new ConcurrentHashMap<>();
        this.completionQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Inicia una operación de E/S para un proceso
     */
    public void startIOOperation(Process process, int duration) {
        IOOperation operation = new IOOperation(process, duration);
        activeOperations.put(process.getPid(), operation);
        
        ioExecutor.submit(() -> {
            try {
                // Simula el tiempo de E/S
                Thread.sleep(duration);
                
                // Notifica la finalización
                completionQueue.put(new IOCompletion(process.getPid()));
                activeOperations.remove(process.getPid());
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Verifica si hay operaciones de E/S completadas
     */
    public IOCompletion pollCompletion() {
        return completionQueue.poll();
    }

    /**
     * Espera por una operación de E/S completada (bloqueante)
     */
    public IOCompletion waitForCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        return completionQueue.poll(timeout, unit);
    }

    /**
     * Verifica si un proceso tiene una operación de E/S activa
     */
    public boolean hasActiveOperation(String pid) {
        return activeOperations.containsKey(pid);
    }

    /**
     * Cancela una operación de E/S
     */
    public void cancelOperation(String pid) {
        activeOperations.remove(pid);
    }

    /**
     * Apaga el gestor de E/S
     */
    public void shutdown() {
        ioExecutor.shutdown();
        try {
            if (!ioExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                ioExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            ioExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Clase interna para representar una operación de E/S
    private static class IOOperation {
        final Process process;
        final int duration;
        final long startTime;

        IOOperation(Process process, int duration) {
            this.process = process;
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
        }
    }

    // Clase para notificar la finalización de E/S
    public static class IOCompletion {
        private final String pid;
        private final long completionTime;

        public IOCompletion(String pid) {
            this.pid = pid;
            this.completionTime = System.currentTimeMillis();
        }

        public String getPid() {
            return pid;
        }

        public long getCompletionTime() {
            return completionTime;
        }
    }
}
