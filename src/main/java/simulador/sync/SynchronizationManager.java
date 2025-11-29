package simulador.sync;

import simulador.process.Process;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona la sincronización entre el planificador y el módulo de memoria
 */
public class SynchronizationManager {
    
    private final Lock schedulerLock;
    private final Lock memoryLock;
    private final Condition memoryReady;
    private final Condition schedulerReady;
    private final Semaphore cpuSemaphore;
    private final Map<String, Semaphore> processBlockSemaphores;
    private final Map<String, Boolean> memoryAvailableMap;

    public SynchronizationManager() {
        this.schedulerLock = new ReentrantLock();
        this.memoryLock = new ReentrantLock();
        this.memoryReady = memoryLock.newCondition();
        this.schedulerReady = schedulerLock.newCondition();
        this.cpuSemaphore = new Semaphore(1); // Solo un proceso puede usar la CPU
        this.processBlockSemaphores = new HashMap<>();
        this.memoryAvailableMap = new HashMap<>();
    }

    /**
     * El planificador espera a que la memoria esté lista para un proceso
     */
    public void waitForMemory(String pid) throws InterruptedException {
        memoryLock.lock();
        try {
            while (!isMemoryAvailable(pid)) {
                memoryReady.await();
            }
        } finally {
            memoryLock.unlock();
        }
    }

    /**
     * La memoria notifica que está lista para un proceso
     */
    public void notifyMemoryReady(String pid) {
        memoryLock.lock();
        try {
            memoryAvailableMap.put(pid, true);
            memoryReady.signalAll();
        } finally {
            memoryLock.unlock();
        }
    }

    /**
     * Marca que la memoria no está disponible para un proceso
     */
    public void setMemoryUnavailable(String pid) {
        memoryLock.lock();
        try {
            memoryAvailableMap.put(pid, false);
        } finally {
            memoryLock.unlock();
        }
    }

    /**
     * Verifica si la memoria está disponible para un proceso
     */
    private boolean isMemoryAvailable(String pid) {
        return memoryAvailableMap.getOrDefault(pid, false);
    }

    /**
     * Adquiere el acceso a la CPU
     */
    public void acquireCPU() throws InterruptedException {
        cpuSemaphore.acquire();
    }

    /**
     * Libera el acceso a la CPU
     */
    public void releaseCPU() {
        cpuSemaphore.release();
    }

    /**
     * Bloquea un proceso (por E/S o memoria)
     */
    public void blockProcess(String pid) throws InterruptedException {
        Semaphore sem = processBlockSemaphores.computeIfAbsent(pid, k -> new Semaphore(0));
        sem.acquire();
    }

    /**
     * Desbloquea un proceso
     */
    public void unblockProcess(String pid) {
        Semaphore sem = processBlockSemaphores.get(pid);
        if (sem != null) {
            sem.release();
        }
    }

    /**
     * Lock del scheduler para operaciones críticas
     */
    public Lock getSchedulerLock() {
        return schedulerLock;
    }

    /**
     * Lock de memoria para operaciones críticas
     */
    public Lock getMemoryLock() {
        return memoryLock;
    }

    /**
     * Limpia los semáforos de un proceso terminado
     */
    public void cleanupProcess(String pid) {
        processBlockSemaphores.remove(pid);
        memoryAvailableMap.remove(pid);
    }
}
