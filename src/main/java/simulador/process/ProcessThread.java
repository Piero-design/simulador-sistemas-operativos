package simulador.process;

import simulador.memory.MemoryManager;
import simulador.sync.SynchronizationManager;
import simulador.io.IOManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread que representa la ejecución de un proceso
 */
public class ProcessThread extends Thread {
    
    private final Process process;
    private final MemoryManager memoryManager;
    private final SynchronizationManager syncManager;
    private final IOManager ioManager;
    private final List<ExecutionEvent> executionLog;
    private int currentBurstIndex;
    private int remainingBurstTime;
    private long startTime;
    private long endTime;
    private long waitingTime;
    private long turnaroundTime;

    public ProcessThread(Process process, MemoryManager memoryManager, 
                        SynchronizationManager syncManager, IOManager ioManager) {
        super("Thread-" + process.getPid());
        this.process = process;
        this.memoryManager = memoryManager;
        this.syncManager = syncManager;
        this.ioManager = ioManager;
        this.executionLog = new ArrayList<>();
        this.currentBurstIndex = 0;
        this.remainingBurstTime = 0;
        this.waitingTime = 0;
    }

    @Override
    public void run() {
        try {
            process.setState(Process.State.READY);
            logEvent("Process created and ready");

            // Inicializar memoria del proceso
            memoryManager.initializeProcess(process);
            
            // Procesar todas las ráfagas
            while (currentBurstIndex < process.getBursts().size()) {
                String burst = process.getBursts().get(currentBurstIndex);
                
                if (burst.startsWith("CPU")) {
                    executeCPUBurst(burst);
                } else if (burst.startsWith("E/S") || burst.startsWith("I/O")) {
                    executeIOBurst(burst);
                }
                
                currentBurstIndex++;
            }

            // Proceso terminado
            process.setState(Process.State.TERMINATED);
            endTime = System.currentTimeMillis();
            memoryManager.releaseProcessPages(process.getPid());
            syncManager.cleanupProcess(process.getPid());
            logEvent("Process terminated");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logEvent("Process interrupted");
        }
    }

    private void executeCPUBurst(String burst) throws InterruptedException {
        int duration = parseBurstDuration(burst);
        remainingBurstTime = duration;

        logEvent("CPU burst started: " + duration + " units");
        
        // Esperar a que la memoria esté disponible
        if (!memoryManager.loadPages(process, process.getPages())) {
            process.setState(Process.State.BLOCKED);
            logEvent("Blocked waiting for memory");
            syncManager.waitForMemory(process.getPid());
        }

        // Adquirir CPU
        syncManager.acquireCPU();
        process.setState(Process.State.RUNNING);
        
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        logEvent("Running on CPU");

        // Simular ejecución de CPU
        Thread.sleep(duration * 100); // 100ms por unidad de tiempo
        remainingBurstTime = 0;

        // Liberar CPU
        syncManager.releaseCPU();
        process.setState(Process.State.READY);
        
        logEvent("CPU burst completed");
    }

    private void executeIOBurst(String burst) throws InterruptedException {
        int duration = parseBurstDuration(burst);
        
        logEvent("I/O burst started: " + duration + " units");
        
        process.setState(Process.State.BLOCKED);
        
        // Iniciar operación de E/S
        ioManager.startIOOperation(process, duration * 100);
        
        // Esperar a que termine la E/S
        syncManager.blockProcess(process.getPid());
        
        process.setState(Process.State.READY);
        logEvent("I/O burst completed");
    }

    private int parseBurstDuration(String burst) {
        // Parsea "CPU(4)" o "E/S(3)" -> devuelve el número
        int start = burst.indexOf('(') + 1;
        int end = burst.indexOf(')');
        return Integer.parseInt(burst.substring(start, end));
    }

    private void logEvent(String event) {
        executionLog.add(new ExecutionEvent(System.currentTimeMillis(), event));
    }

    public Process getProcess() {
        return process;
    }

    public List<ExecutionEvent> getExecutionLog() {
        return new ArrayList<>(executionLog);
    }

    public int getCurrentBurstIndex() {
        return currentBurstIndex;
    }

    public int getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public void setRemainingBurstTime(int time) {
        this.remainingBurstTime = time;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public void addWaitingTime(long time) {
        this.waitingTime += time;
    }

    public long getTurnaroundTime() {
        if (endTime > 0 && startTime > 0) {
            return endTime - startTime;
        }
        return 0;
    }

    // Clase interna para registrar eventos
    public static class ExecutionEvent {
        private final long timestamp;
        private final String description;

        public ExecutionEvent(long timestamp, String description) {
            this.timestamp = timestamp;
            this.description = description;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + description;
        }
    }
}
