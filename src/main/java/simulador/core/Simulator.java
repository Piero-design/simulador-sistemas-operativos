package simulador.core;

import simulador.process.Process;
import simulador.process.ProcessThread;
import simulador.scheduler.*;
import simulador.memory.*;
import simulador.sync.SynchronizationManager;
import simulador.io.IOManager;
import simulador.metrics.MetricsCollector;
import simulador.utils.FileParser;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Simulador principal que coordina todos los módulos del sistema operativo
 */
public class Simulator {
    
    private final CPUScheduler scheduler;
    private final MemoryManager memoryManager;
    private final SynchronizationManager syncManager;
    private final IOManager ioManager;
    private final MetricsCollector metricsCollector;
    private final List<Process> processes;
    private final List<ProcessThread> processThreads;
    private final ExecutorService executor;
    private final List<SimulationListener> listeners;
    
    private volatile boolean running;
    private int currentTime;
    // removed lastSelectedPid: ProcessThread now notifies END directly

    public Simulator(CPUScheduler scheduler, MemoryManager memoryManager) {
        this.scheduler = scheduler;
        this.memoryManager = memoryManager;
        this.syncManager = new SynchronizationManager();
        this.ioManager = new IOManager();
        this.metricsCollector = new MetricsCollector();
        this.processes = new ArrayList<>();
        this.processThreads = new ArrayList<>();
        this.executor = Executors.newCachedThreadPool();
        this.listeners = new ArrayList<>();
        this.running = false;
        this.currentTime = 0;
    }

    /**
     * Carga procesos desde un archivo
     */
    public void loadProcessesFromFile(String filePath) throws IOException {
        List<Process> loadedProcesses = FileParser.parseProcessFile(filePath);
        processes.addAll(loadedProcesses);
        notifyProcessesLoaded(loadedProcesses.size());
    }

    /**
     * Agrega un proceso manualmente
     */
    public void addProcess(Process process) {
        processes.add(process);
    }

    /**
     * Inicia la simulación
     */
    public void start() {
        if (running) {
            return;
        }

        running = true;
        metricsCollector.startSimulation();
        notifySimulationStarted();

        executor.submit(this::runSimulation);
    }

    /**
     * Lógica principal de la simulación
     */
    private void runSimulation() {
        try {
            // Ordenar procesos por tiempo de llegada
            processes.sort(Comparator.comparingInt(Process::getArrivalTime));

            // We'll run a single tick-driven loop here to avoid races between threads.
            Map<String, Integer> remainingBurst = new HashMap<>();
            Map<String, Integer> quantumRemaining = new HashMap<>();
            Process currentProcess = null;
            int currentQuantumLeft = Integer.MAX_VALUE;

            while (running && !allProcessesTerminated()) {
                // 1) Handle any I/O completions first
                IOManager.IOCompletion completion;
                while ((completion = ioManager.pollCompletion()) != null) {
                    Process p = findProcessByPid(completion.getPid());
                    if (p != null) {
                        p.setState(Process.State.READY);
                        scheduler.addProcess(p);
                        notifyIOCompleted(p);
                    }
                }

                // 2) Handle arrivals at this time
                for (Process process : processes) {
                    if (process.getState() == Process.State.NEW && process.getArrivalTime() <= getCurrentTime()) {
                        scheduler.addProcess(process);
                        process.setState(Process.State.READY);
                        notifyProcessArrived(process);
                    }
                }

                // 3) If no current process, get one from scheduler
                if (currentProcess == null) {
                    currentProcess = scheduler.getNextProcess();
                    if (currentProcess != null) {
                        // Initialize remaining burst if needed (do NOT reset on every selection)
                        int idx = currentProcess.getCurrentBurstIndex();
                        String burst = currentProcess.getBursts().get(idx);
                        if (burst.startsWith("CPU")) {
                            int start = burst.indexOf('(') + 1;
                            int end = burst.indexOf(')');
                            int duration = Integer.parseInt(burst.substring(start, end));
                            remainingBurst.putIfAbsent(currentProcess.getPid(), duration);
                        }
                        currentProcess.setState(Process.State.RUNNING);
                        int q = Integer.MAX_VALUE;
                        if (scheduler instanceof RoundRobin) q = ((RoundRobin) scheduler).getQuantum();
                        quantumRemaining.put(currentProcess.getPid(), q);
                        currentQuantumLeft = quantumRemaining.get(currentProcess.getPid());
                        notifyProcessExecStart(currentProcess, getCurrentTime());
                    }
                }

                // 3) Execute one time unit for currentProcess if exists
                if (currentProcess != null) {
                    // Execute 1 unit
                    Thread.sleep(100);
                    metricsCollector.addCPUTime(100L);
                    // decrement remaining and quantum
                    String pid = currentProcess.getPid();
                    int rem = remainingBurst.getOrDefault(pid, 0) - 1;
                    remainingBurst.put(pid, rem);
                    int qleft = quantumRemaining.getOrDefault(pid, Integer.MAX_VALUE) - 1;
                    quantumRemaining.put(pid, qleft);

                    // advance logical time by 1
                    advanceTime(1);

                    // Check burst completion
                    if (rem <= 0) {
                        // CPU burst finished
                        notifyProcessExecEnd(currentProcess, getCurrentTime());
                        currentProcess.setCurrentBurstIndex(currentProcess.getCurrentBurstIndex() + 1);
                        // cleanup remaining/quantum for this pid
                        remainingBurst.remove(pid);
                        quantumRemaining.remove(pid);
                        // If next is I/O, start it
                        if (currentProcess.getCurrentBurstIndex() < currentProcess.getBursts().size()) {
                            String nextBurst = currentProcess.getBursts().get(currentProcess.getCurrentBurstIndex());
                            if (nextBurst.startsWith("E/S") || nextBurst.startsWith("I/O")) {
                                int s = nextBurst.indexOf('(') + 1;
                                int e = nextBurst.indexOf(')');
                                int dur = Integer.parseInt(nextBurst.substring(s, e));
                                currentProcess.setState(Process.State.BLOCKED);
                                ioManager.startIOOperation(currentProcess, dur * 100);
                            } else {
                                // Next is CPU: requeue
                                currentProcess.setState(Process.State.READY);
                                scheduler.addProcess(currentProcess);
                            }
                        } else {
                            // Process finished all bursts
                            currentProcess.setState(Process.State.TERMINATED);
                            // ensure maps cleaned
                            remainingBurst.remove(pid);
                            quantumRemaining.remove(pid);
                            currentProcess.setFinishTime(System.currentTimeMillis());
                        }
                        currentProcess = null;
                    } else if (qleft <= 0) {
                        // quantum expired: preempt
                        notifyProcessExecEnd(currentProcess, getCurrentTime());
                        currentProcess.setState(Process.State.READY);
                        scheduler.addProcess(currentProcess);
                        currentProcess = null;
                    }
                } else {
                    // idle tick
                    Thread.sleep(100);
                    advanceTime(1);
                }
            }

            // Finalizar simulación
            finishSimulation();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Detiene la simulación
     */
    public void stop() {
        running = false;
        finishSimulation();
    }

    private void finishSimulation() {
        running = false;
        metricsCollector.endSimulation();

        // Recolectar métricas
        for (ProcessThread thread : processThreads) {
            metricsCollector.recordProcess(thread);
        }

        metricsCollector.setPageFaults(memoryManager.getPageFaults());
        metricsCollector.setPageReplacements(memoryManager.getPageReplacements());

        // Cerrar recursos
        ioManager.shutdown();
        executor.shutdown();

        notifySimulationFinished();
    }

    private boolean allProcessesTerminated() {
        for (Process process : processes) {
            if (process.getState() != Process.State.TERMINATED) {
                return false;
            }
        }
        return true;
    }

    private ProcessThread findThreadByPid(String pid) {
        for (ProcessThread thread : processThreads) {
            if (thread.getProcess().getPid().equals(pid)) {
                return thread;
            }
        }
        return null;
    }

    private Process findProcessByPid(String pid) {
        for (Process process : processes) {
            if (process.getPid().equals(pid)) {
                return process;
            }
        }
        return null;
    }

    // Getters
    public MetricsCollector getMetricsCollector() {
        return metricsCollector;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public CPUScheduler getScheduler() {
        return scheduler;
    }

    public List<Process> getProcesses() {
        return new ArrayList<>(processes);
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized int getCurrentTime() {
        return currentTime;
    }

    /**
     * Avanza el reloj lógico de la simulación de forma atómica y notifica listeners.
     * @param delta unidades a avanzar
     * @return el nuevo tiempo actual
     */
    public synchronized int advanceTime(int delta) {
        this.currentTime += delta;
        notifyTimeAdvanced(this.currentTime);
        return this.currentTime;
    }

    // Sistema de eventos para la UI
    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    private void notifySimulationStarted() {
        for (SimulationListener listener : listeners) {
            listener.onSimulationStarted();
        }
    }

    private void notifySimulationFinished() {
        for (SimulationListener listener : listeners) {
            listener.onSimulationFinished(metricsCollector);
        }
    }

    private void notifyProcessesLoaded(int count) {
        for (SimulationListener listener : listeners) {
            listener.onProcessesLoaded(count);
        }
    }

    private void notifyProcessArrived(Process process) {
        for (SimulationListener listener : listeners) {
            listener.onProcessArrived(process);
        }
    }

    private void notifyProcessStarted(Process process) {
        for (SimulationListener listener : listeners) {
            listener.onProcessStarted(process);
        }
    }

    private void notifyIOCompleted(Process process) {
        for (SimulationListener listener : listeners) {
            listener.onIOCompleted(process);
        }
    }

    private void notifyTimeAdvanced(int time) {
        for (SimulationListener listener : listeners) {
            listener.onTimeAdvanced(time);
        }
    }

    /**
     * Interfaz para escuchar eventos de simulación
     */
    public interface SimulationListener {
        default void onSimulationStarted() {}
        default void onSimulationFinished(MetricsCollector metrics) {}
        default void onProcessesLoaded(int count) {}
        default void onProcessArrived(Process process) {}
        default void onProcessStarted(Process process) {}
        default void onIOCompleted(Process process) {}
        default void onTimeAdvanced(int time) {}
        default void onProcessExecStart(Process process, int time) {}
        default void onProcessExecEnd(Process process, int time) {}
    }

    public void notifyProcessExecStart(Process process, int time) {
        for (SimulationListener listener : listeners) {
            listener.onProcessExecStart(process, time);
        }
        // persist to logfile for Gantt verification
        try {
            SimulationLogger.log("START " + process.getPid() + " " + time);
        } catch (Exception e) {
            System.err.println("[Simulator] Failed to write exec start log: " + e.getMessage());
        }
    }

    public void notifyProcessExecEnd(Process process, int time) {
        for (SimulationListener listener : listeners) {
            listener.onProcessExecEnd(process, time);
        }
        try {
            SimulationLogger.log("END " + process.getPid() + " " + time);
        } catch (Exception e) {
            System.err.println("[Simulator] Failed to write exec end log: " + e.getMessage());
        }
    }
}
