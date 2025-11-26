package simulador.scheduler;

import simulador.process.Process;
import simulador.memory.MemoryManager;
import simulador.sync.ProcessSync;
import simulador.sync.IOManager;
import simulador.utils.Burst;
import simulador.utils.Metrics;

import java.util.*;
import java.util.concurrent.*;

/**
 * Motor del planificador - ejecuta la simulación completa
 */
public class SchedulerEngine {
    
    private final CPUScheduler scheduler;
    private final MemoryManager memoryManager;
    private final ProcessSync sync;
    private final IOManager ioManager;
    private final Metrics metrics;
    
    private final List<Process> allProcesses;
    private final List<Process> readyQueue = new ArrayList<>();
    private final List<Process> blockedQueue = new ArrayList<>();
    private Process runningProcess = null;
    
    private int currentTime = 0;
    private boolean running = false;
    private final int quantum;
    
    // Callbacks para actualizar GUI
    private GUICallback guiCallback;
    
    public SchedulerEngine(List<Process> processes, CPUScheduler scheduler, 
                          MemoryManager memoryManager, ProcessSync sync, 
                          IOManager ioManager, Metrics metrics, int quantum) {
        this.allProcesses = new ArrayList<>(processes);
        this.scheduler = scheduler;
        this.memoryManager = memoryManager;
        this.sync = sync;
        this.ioManager = ioManager;
        this.metrics = metrics;
        this.quantum = quantum;
    }
    
    public void setGUICallback(GUICallback callback) {
        this.guiCallback = callback;
    }
    
    /**
     * Inicia la simulación completa
     */
    public void start() {
        running = true;
        
        log("═══════════════════════════════════════");
        log("INICIANDO SIMULACIÓN");
        log("Algoritmo: " + scheduler.getName());
        log("Procesos totales: " + allProcesses.size());
        log("═══════════════════════════════════════\n");
        
        // Ordenar procesos por tiempo de llegada
        allProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));
        
        // Registrar procesos
        for (Process p : allProcesses) {
            metrics.recordProcessArrival(p);
        }
        
        // Loop principal de simulación
        while (running && !allProcessesCompleted()) {
            
            // 1. Verificar llegadas de nuevos procesos
            checkArrivals();
            
            // 2. Si no hay proceso ejecutando, despachar uno
            if (runningProcess == null && !readyQueue.isEmpty()) {
                dispatchNextProcess();
            }
            
            // 3. Ejecutar el proceso actual
            if (runningProcess != null) {
                executeCurrentProcess();
            }
            
            // 4. Actualizar GUI
            updateGUI();
            
            // 5. Avanzar tiempo
            currentTime++;
            
            // Pausa para visualización
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Simulación completada
        finishSimulation();
    }
    
    /**
     * Verifica si llegaron nuevos procesos
     */
    private void checkArrivals() {
        for (Process p : allProcesses) {
            if (p.getArrivalTime() == currentTime && p.getState() == Process.State.NEW) {
                p.setState(Process.State.READY);
                p.setStartTime(currentTime);
                
                // Cargar páginas en memoria
                try {
                    sync.lockMemory();
                    memoryManager.loadProcessPages(p);
                    sync.unlockMemory();
                } catch (Exception e) {
                    log("Error cargando páginas de " + p.getPid());
                }
                
                scheduler.addProcess(p);
                readyQueue.add(p);
                
                log("[t=" + currentTime + "] " + p.getPid() + " -> LISTO (llegada)");
                metrics.recordProcessStart(p.getPid(), currentTime);
            }
        }
    }
    
    /**
     * Despacha el siguiente proceso a ejecutar
     */
    private void dispatchNextProcess() {
        runningProcess = scheduler.getNextProcess();
        if (runningProcess != null) {
            runningProcess.setState(Process.State.RUNNING);
            readyQueue.remove(runningProcess);
            
            log("[t=" + currentTime + "] " + runningProcess.getPid() + " -> EJECUTANDO");
            
            // Registrar inicio si es la primera vez
            if (runningProcess.getStartTime() == -1) {
                runningProcess.setStartTime(currentTime);
                metrics.recordProcessStart(runningProcess.getPid(), currentTime);
            }
            
            // Agregar al diagrama de Gantt
            if (guiCallback != null) {
                guiCallback.addGanttEntry(runningProcess.getPid(), currentTime, quantum);
            }
        }
    }
    
    /**
     * Ejecuta la ráfaga actual del proceso
     */
    private void executeCurrentProcess() {
        if (runningProcess == null || !runningProcess.hasMoreBursts()) {
            return;
        }
        
        String currentBurstStr = runningProcess.getCurrentBurst();
        Burst burst = Burst.parse(currentBurstStr);
        
        if (burst.getType() == Burst.Type.CPU) {
            // Ejecutar ráfaga de CPU
            int timeToExecute = Math.min(quantum, burst.getDuration());
            
            log("[t=" + currentTime + "] " + runningProcess.getPid() + 
                " ejecutando CPU por " + timeToExecute + " unidades");
            
            metrics.recordCPUBusy(timeToExecute);
            
            // Actualizar Gantt con duración real
            if (guiCallback != null) {
                guiCallback.addGanttEntry(runningProcess.getPid(), currentTime, timeToExecute);
            }
            
            // Simular ejecución (ya avanzamos el tiempo en el loop)
            runningProcess.nextBurst();
            
            // Verificar si terminó
            if (!runningProcess.hasMoreBursts()) {
                // Proceso terminado
                runningProcess.setState(Process.State.TERMINATED);
                runningProcess.setFinishTime(currentTime);
                
                log("[t=" + currentTime + "] " + runningProcess.getPid() + " -> TERMINADO");
                
                metrics.recordProcessFinish(runningProcess.getPid(), currentTime);
                metrics.recordWaitTime(runningProcess.getPid(), runningProcess.getWaitTime());
                
                // Liberar memoria
                sync.lockMemory();
                memoryManager.releaseProcessMemory(runningProcess.getPid());
                sync.unlockMemory();
                
                runningProcess = null;
                
            } else {
                // Verificar próxima ráfaga
                String nextBurstStr = runningProcess.getCurrentBurst();
                Burst nextBurst = Burst.parse(nextBurstStr);
                
                if (nextBurst.getType() == Burst.Type.IO) {
                    // Bloquear por E/S
                    runningProcess.setState(Process.State.BLOCKED_IO);
                    blockedQueue.add(runningProcess);
                    
                    log("[t=" + currentTime + "] " + runningProcess.getPid() + " -> BLOQUEADO (E/S)");
                    
                    // Iniciar operación de E/S
                    int ioDuration = nextBurst.getDuration();
                    Process processRef = runningProcess;
                    
                    ioManager.startIO(processRef, ioDuration, (p) -> {
                        // Callback cuando termina E/S
                        p.setState(Process.State.READY);
                        p.nextBurst();
                        
                        sync.lockReadyQueue();
                        blockedQueue.remove(p);
                        readyQueue.add(p);
                        scheduler.addProcess(p);
                        sync.unlockReadyQueue();
                        
                        log("[t=" + currentTime + "] " + p.getPid() + " -> LISTO (E/S completada)");
                    });
                    
                    metrics.recordIOOperation(ioDuration);
                    runningProcess = null;
                    
                } else {
                    // Volver a Ready (preemption o fin de quantum)
                    runningProcess.setState(Process.State.READY);
                    readyQueue.add(runningProcess);
                    scheduler.addProcess(runningProcess);
                    
                    log("[t=" + currentTime + "] " + runningProcess.getPid() + " -> LISTO (quantum)");
                    
                    runningProcess = null;
                }
            }
        }
    }
    
    /**
     * Verifica si todos los procesos terminaron
     */
    private boolean allProcessesCompleted() {
        for (Process p : allProcesses) {
            if (p.getState() != Process.State.TERMINATED) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Finaliza la simulación y muestra resultados
     */
    private void finishSimulation() {
        log("\n═══════════════════════════════════════");
        log("SIMULACIÓN COMPLETADA");
        log("═══════════════════════════════════════");
        
        metrics.setSimulationTime(currentTime);
        log(metrics.generateReport());
        log(memoryManager.getMemoryStatus());
        
        ioManager.shutdown();
        running = false;
    }
    
    /**
     * Actualiza la GUI
     */
    private void updateGUI() {
        if (guiCallback != null) {
            guiCallback.onUpdate(readyQueue, blockedQueue, runningProcess, 
                               scheduler.getName(), memoryManager);
        }
    }
    
    /**
     * Log de eventos
     */
    private void log(String message) {
        System.out.println(message);
        if (guiCallback != null) {
            guiCallback.onLog(message);
        }
    }
    
    public void stop() {
        running = false;
    }
    
    public int getCurrentTime() {
        return currentTime;
    }
    
    public Metrics getMetrics() {
        return metrics;
    }
    
    /**
     * Interfaz para callbacks de GUI
     */
    public interface GUICallback {
        void onUpdate(List<Process> ready, List<Process> blocked, 
                     Process running, String schedulerName, MemoryManager memory);
        void onLog(String message);
        void addGanttEntry(String pid, int startTime, int duration);
    }
}
