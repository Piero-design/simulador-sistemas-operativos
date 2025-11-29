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

            // Inicializar threads de procesos
            for (Process process : processes) {
                ProcessThread thread = new ProcessThread(process, memoryManager, syncManager, ioManager);
                processThreads.add(thread);
            }

            // Crear un thread para manejar las llegadas de procesos
            Thread arrivalHandler = new Thread(() -> {
                try {
                    for (Process process : processes) {
                        // Esperar hasta el tiempo de llegada
                        while (currentTime < process.getArrivalTime() && running) {
                            Thread.sleep(100);
                            currentTime++;
                        }

                        if (!running) break;

                        // Agregar proceso al scheduler
                        scheduler.addProcess(process);
                        process.setState(Process.State.READY);
                        notifyProcessArrived(process);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            arrivalHandler.start();

            // Thread principal del scheduler
            Thread schedulerThread = new Thread(() -> {
                try {
                    while (running && !allProcessesTerminated()) {
                        Process nextProcess = scheduler.getNextProcess();
                        
                        if (nextProcess != null) {
                            // Buscar el thread correspondiente
                            ProcessThread thread = findThreadByPid(nextProcess.getPid());
                            
                            if (thread != null && !thread.isAlive()) {
                                // Iniciar el thread si no está corriendo
                                thread.start();
                                notifyProcessStarted(nextProcess);
                            } else if (thread != null) {
                                // Continuar ejecución
                                syncManager.notifyMemoryReady(nextProcess.getPid());
                            }
                        }

                        Thread.sleep(100);
                        currentTime++;
                        notifyTimeAdvanced(currentTime);
                    }

                    // Esperar a que todos los threads terminen
                    for (ProcessThread thread : processThreads) {
                        if (thread.isAlive()) {
                            thread.join(5000);
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            schedulerThread.start();

            // Thread para manejar E/S completadas
            Thread ioCompletionThread = new Thread(() -> {
                try {
                    while (running) {
                        IOManager.IOCompletion completion = ioManager.pollCompletion();
                        if (completion != null) {
                            syncManager.unblockProcess(completion.getPid());
                            Process process = findProcessByPid(completion.getPid());
                            if (process != null) {
                                process.setState(Process.State.READY);
                                scheduler.addProcess(process);
                                notifyIOCompleted(process);
                            }
                        }
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            ioCompletionThread.start();

            // Esperar a que termine el scheduler
            schedulerThread.join();

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

    public int getCurrentTime() {
        return currentTime;
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
    }
}
