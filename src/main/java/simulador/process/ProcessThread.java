package simulador.process;

import simulador.memory.MemoryManager;
import simulador.sync.SynchronizationManager;
import simulador.io.IOManager;
import simulador.scheduler.CPUScheduler;
import simulador.scheduler.RoundRobin;
import simulador.metrics.MetricsCollector;
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
    private final CPUScheduler scheduler;
    private final MetricsCollector metricsCollector;
    private final simulador.core.Simulator simulator;
    private long waitStart;

    public ProcessThread(Process process, MemoryManager memoryManager,
                        SynchronizationManager syncManager, IOManager ioManager,
                        CPUScheduler scheduler, MetricsCollector metricsCollector,
                        simulador.core.Simulator simulator) {
        super("Thread-" + process.getPid());
        this.process = process;
        this.memoryManager = memoryManager;
        this.syncManager = syncManager;
        this.ioManager = ioManager;
        this.executionLog = new ArrayList<>();
        this.currentBurstIndex = 0;
        this.remainingBurstTime = 0;
        this.waitingTime = 0;
        this.scheduler = scheduler;
        this.metricsCollector = metricsCollector;
        this.simulator = simulator;
        this.waitStart = 0;
    }

    @Override
    public void run() {
        try {
            process.setState(Process.State.READY);
            logEvent("Process created and ready");
            // mark waiting start (process is ready and waiting for CPU)
            waitStart = System.currentTimeMillis();

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

        // Determine quantum (if RoundRobin)
        int quantum = Integer.MAX_VALUE;
        if (scheduler instanceof RoundRobin) {
            quantum = ((RoundRobin) scheduler).getQuantum();
        }

        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        // Ejecutar con preempción por quantum
        while (remainingBurstTime > 0) {
                // Attempt to acquire CPU
            syncManager.acquireCPU();
            try {
                // We'll advance logical time atomically after executing runUnits
                // update waiting time if applicable
                if (waitStart > 0) {
                    waitingTime += System.currentTimeMillis() - waitStart;
                    waitStart = 0;
                }

                process.setState(Process.State.RUNNING);
                logEvent("Running on CPU (remaining=" + remainingBurstTime + ")");

                int runUnits = Math.min(remainingBurstTime, quantum);

                // Execute unit-by-unit so arrivals and scheduler can observe time progression
                for (int i = 0; i < runUnits; i++) {
                    Thread.sleep(100); // 1 unidad = 100ms
                    remainingBurstTime--;
                    try {
                        if (simulator != null) simulator.advanceTime(1);
                    } catch (Exception ex) {
                        // ignore
                    }
                }

                // record CPU time
                if (metricsCollector != null) {
                    metricsCollector.addCPUTime(runUnits * 100L);
                }

                // Notify END at the current logical time
                try {
                    if (simulator != null) simulator.notifyProcessExecEnd(process, simulator.getCurrentTime());
                } catch (Exception ex) {
                    // ignore logging errors
                }

                // finished runUnits
                if (remainingBurstTime > 0) {
                    // quantum expired — preempt
                    logEvent("Quantum expired for pid=" + process.getPid() + ", remaining=" + remainingBurstTime);
                    process.setState(Process.State.READY);
                    // re-add to scheduler queue so it can be scheduled again
                    try {
                        scheduler.addProcess(process);
                    } catch (Exception ex) {
                        // ignore if scheduler doesn't support add at this point
                    }
                } else {
                    logEvent("CPU burst completed");
                    process.setState(Process.State.READY);
                }

            } finally {
                // Release CPU for next process / context switch
                syncManager.releaseCPU();
            }

            if (remainingBurstTime > 0) {
                // Block this thread until scheduler unblocks it
                logEvent("Blocking thread pid=" + process.getPid() + " waiting to be rescheduled");
                syncManager.blockProcess(process.getPid());
                logEvent("Resumed thread pid=" + process.getPid());
            }
        }
    }

    private void executeIOBurst(String burst) throws InterruptedException {
        int duration = parseBurstDuration(burst);
        
        logEvent("I/O burst started: " + duration + " units");
        
        process.setState(Process.State.BLOCKED);
        
        // Iniciar operación de E/S
        ioManager.startIOOperation(process, duration * 100);
        
        // Esperar a que termine la E/S
        syncManager.blockProcess(process.getPid());

        // When unblocked after I/O completion, move to READY and start waiting timer
        process.setState(Process.State.READY);
        waitStart = System.currentTimeMillis();
        logEvent("I/O burst completed");
    }

    private int parseBurstDuration(String burst) {
        // Parsea "CPU(4)" o "E/S(3)" -> devuelve el número
        int start = burst.indexOf('(') + 1;
        int end = burst.indexOf(')');
        return Integer.parseInt(burst.substring(start, end));
    }

    private void logEvent(String event) {
        long ts = System.currentTimeMillis();
        executionLog.add(new ExecutionEvent(ts, event));
        System.out.println("[ProcessThread] [" + ts + "] PID=" + process.getPid() + " - " + event);
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
