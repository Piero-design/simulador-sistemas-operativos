package simulador.sync;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

/**
 * Módulo de sincronización para coordinar:
 * - Planificador de CPU
 * - Gestor de Memoria
 * - Gestor de E/S
 */
public class ProcessSync {
    
    // Semáforos para controlar acceso a recursos
    private final Semaphore cpuAvailable;
    private final Semaphore memoryAvailable;
    private final Semaphore ioAvailable;
    
    // Locks para proteger estructuras compartidas
    private final ReentrantLock readyQueueLock;
    private final ReentrantLock blockedQueueLock;
    private final ReentrantLock memoryLock;
    
    // Condition variables para notificaciones
    private final Condition processReady;
    private final Condition memoryReady;
    private final Condition ioComplete;
    
    public ProcessSync() {
        this.cpuAvailable = new Semaphore(1, true); // Solo 1 CPU
        this.memoryAvailable = new Semaphore(1, true);
        this.ioAvailable = new Semaphore(10, true); // Múltiples operaciones E/S
        
        this.readyQueueLock = new ReentrantLock(true);
        this.blockedQueueLock = new ReentrantLock(true);
        this.memoryLock = new ReentrantLock(true);
        
        this.processReady = readyQueueLock.newCondition();
        this.memoryReady = memoryLock.newCondition();
        this.ioComplete = blockedQueueLock.newCondition();
    }
    
    // ===== Control de CPU =====
    public void acquireCPU() throws InterruptedException {
        cpuAvailable.acquire();
    }
    
    public void releaseCPU() {
        cpuAvailable.release();
    }
    
    // ===== Control de Memoria =====
    public void acquireMemory() throws InterruptedException {
        memoryAvailable.acquire();
    }
    
    public void releaseMemory() {
        memoryAvailable.release();
    }
    
    public void lockMemory() {
        memoryLock.lock();
    }
    
    public void unlockMemory() {
        memoryLock.unlock();
    }
    
    public void waitForMemoryReady() throws InterruptedException {
        memoryReady.await();
    }
    
    public void signalMemoryReady() {
        memoryReady.signalAll();
    }
    
    // ===== Control de E/S =====
    public void acquireIO() throws InterruptedException {
        ioAvailable.acquire();
    }
    
    public void releaseIO() {
        ioAvailable.release();
    }
    
    public void signalIOComplete() {
        blockedQueueLock.lock();
        try {
            ioComplete.signalAll();
        } finally {
            blockedQueueLock.unlock();
        }
    }
    
    // ===== Cola de Listos =====
    public void lockReadyQueue() {
        readyQueueLock.lock();
    }
    
    public void unlockReadyQueue() {
        readyQueueLock.unlock();
    }
    
    public void waitForProcessReady() throws InterruptedException {
        processReady.await();
    }
    
    public void signalProcessReady() {
        processReady.signalAll();
    }
    
    // ===== Cola de Bloqueados =====
    public void lockBlockedQueue() {
        blockedQueueLock.lock();
    }
    
    public void unlockBlockedQueue() {
        blockedQueueLock.unlock();
    }
}
