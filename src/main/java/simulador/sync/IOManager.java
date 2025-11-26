package simulador.sync;

import simulador.process.Process;
import java.util.concurrent.*;

/**
 * Gestor de operaciones de E/S
 * Maneja ráfagas de E/S de forma asíncrona
 */
public class IOManager {
    
    private final ProcessSync sync;
    private final ExecutorService ioExecutor;
    private final ConcurrentHashMap<String, CompletableFuture<Void>> activeIO;
    private volatile boolean running;
    
    public IOManager(ProcessSync sync) {
        this.sync = sync;
        this.ioExecutor = Executors.newFixedThreadPool(5);
        this.activeIO = new ConcurrentHashMap<>();
        this.running = true;
    }
    
    /**
     * Inicia una operación de E/S para un proceso
     */
    public void startIO(Process process, int duration, IOCompleteCallback callback) {
        if (!running) return;
        
        CompletableFuture<Void> ioTask = CompletableFuture.runAsync(() -> {
            try {
                sync.acquireIO();
                System.out.println("[E/S] " + process.getPid() + " inicia E/S por " + duration + " unidades");
                
                // Simular operación de E/S
                Thread.sleep(duration * 100); // 100ms por unidad de tiempo
                
                System.out.println("[E/S] " + process.getPid() + " completó E/S");
                
                // Notificar completado
                if (callback != null) {
                    callback.onIOComplete(process);
                }
                
                sync.signalIOComplete();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                sync.releaseIO();
                activeIO.remove(process.getPid());
            }
        }, ioExecutor);
        
        activeIO.put(process.getPid(), ioTask);
    }
    
    /**
     * Cancela una operación de E/S en curso
     */
    public void cancelIO(String pid) {
        CompletableFuture<Void> task = activeIO.get(pid);
        if (task != null) {
            task.cancel(true);
            activeIO.remove(pid);
        }
    }
    
    /**
     * Espera a que todas las operaciones de E/S terminen
     */
    public void waitForAllIO() {
        CompletableFuture<Void>[] tasks = activeIO.values().toArray(new CompletableFuture[0]);
        CompletableFuture.allOf(tasks).join();
    }
    
    /**
     * Cierra el gestor de E/S
     */
    public void shutdown() {
        running = false;
        waitForAllIO();
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
    
    /**
     * Callback para notificar cuando se completa una operación de E/S
     */
    public interface IOCompleteCallback {
        void onIOComplete(Process process);
    }
}
