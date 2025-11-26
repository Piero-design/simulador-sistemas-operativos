package simulador;

import simulador.gui.MainWindow;
import simulador.process.Process;
import simulador.scheduler.*;
import simulador.memory.*;
import simulador.sync.*;
import simulador.utils.*;

import javax.swing.SwingUtilities;
import java.util.*;

/**
 * Clase principal del simulador de Sistema Operativo
 * 
 * Este simulador integra:
 * - Planificación de CPU (FCFS, SJF, Round Robin)
 * - Gestión de Memoria Virtual (FIFO, LRU, Óptimo)
 * - Sincronización entre módulos
 * - Gestión de E/S
 * 
 * @author Tu Equipo
 * @version 1.0
 */
public class SimuladorMain {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   SIMULADOR DE SISTEMA OPERATIVO");
        System.out.println("   Planificación de Procesos y Memoria Virtual");
        System.out.println("═══════════════════════════════════════════════\n");
        
        // Iniciar GUI en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            
            // Ejemplo de uso - puedes comentar esto y usar los botones de la GUI
            runExampleSimulation(window);
        });
    }
    
    /**
     * Ejemplo de simulación automática
     * Esta es una demostración de cómo usar el simulador
     */
    private static void runExampleSimulation(MainWindow window) {
        try {
            window.appendLog("═══════════════════════════════════════");
            window.appendLog("Cargando configuración del simulador...");
            window.appendLog("═══════════════════════════════════════\n");
            
            // 1. Cargar procesos desde archivo
            List<Process> processes = ProcessParser.parseFile("procesos.txt");
            window.appendLog("✓ Cargados " + processes.size() + " procesos desde procesos.txt\n");
            
            // 2. Configurar parámetros
            int totalFrames = 16;
            String schedAlgorithm = "RR"; // FCFS, SJF, RR
            int quantum = 3;
            String memAlgorithm = "LRU"; // FIFO, LRU, Optimal
            
            window.appendLog("Configuración:");
            window.appendLog("  - Algoritmo de planificación: " + schedAlgorithm);
            if (schedAlgorithm.equals("RR")) {
                window.appendLog("  - Quantum: " + quantum);
            }
            window.appendLog("  - Algoritmo de memoria: " + memAlgorithm);
            window.appendLog("  - Marcos de memoria: " + totalFrames);
            window.appendLog("");
            
            // 3. Crear componentes
            ProcessSync sync = new ProcessSync();
            PageReplacement memAlg = createMemoryAlgorithm(memAlgorithm, processes);
            MemoryManager memoryManager = new MemoryManager(totalFrames, memAlg);
            CPUScheduler scheduler = createScheduler(schedAlgorithm, quantum);
            IOManager ioManager = new IOManager(sync);
            Metrics metrics = new Metrics();
            
            window.appendLog("✓ Componentes inicializados\n");
            window.appendLog("═══════════════════════════════════════");
            window.appendLog("Iniciando simulación...");
            window.appendLog("═══════════════════════════════════════\n");
            
            // 4. Simular (versión simplificada)
            int currentTime = 0;
            List<Process> readyQueue = new ArrayList<>();
            List<Process> blockedQueue = new ArrayList<>();
            Process runningProcess = null;
            
            // Ordenar procesos por tiempo de llegada
            processes.sort(Comparator.comparingInt(Process::getArrivalTime));
            
            // Agregar procesos que llegan al tiempo 0
            for (Process p : processes) {
                if (p.getArrivalTime() == 0) {
                    p.setState(Process.State.READY);
                    scheduler.addProcess(p);
                    readyQueue.add(p);
                    metrics.recordProcessArrival(p);
                    window.appendLog("[t=" + currentTime + "] " + p.getPid() + " -> LISTO");
                }
            }
            
            // Actualizar GUI inicial
            window.updateQueues(readyQueue, blockedQueue, runningProcess, scheduler.getName());
            window.updateMemory(memoryManager);
            
            // Simular algunos pasos
            for (int step = 0; step < 5 && !readyQueue.isEmpty(); step++) {
                currentTime++;
                
                // Obtener siguiente proceso
                runningProcess = scheduler.getNextProcess();
                if (runningProcess != null) {
                    runningProcess.setState(Process.State.RUNNING);
                    readyQueue.remove(runningProcess);
                    
                    window.appendLog("[t=" + currentTime + "] " + runningProcess.getPid() + " -> EJECUTANDO");
                    
                    // Cargar páginas en memoria
                    if (memoryManager.loadProcessPages(runningProcess)) {
                        window.appendLog("[t=" + currentTime + "] Páginas cargadas para " + runningProcess.getPid());
                    }
                    
                    // Agregar entrada al Gantt
                    window.addGanttEntry(runningProcess.getPid(), currentTime, 2);
                    
                    // Actualizar GUI
                    window.updateQueues(readyQueue, blockedQueue, runningProcess, scheduler.getName());
                    window.updateMemory(memoryManager);
                    
                    Thread.sleep(1000); // Pausa para visualización
                    
                    // Volver a ready (simulación simplificada)
                    runningProcess.setState(Process.State.READY);
                    if (runningProcess.hasMoreBursts()) {
                        scheduler.addProcess(runningProcess);
                        readyQueue.add(runningProcess);
                    }
                }
            }
            
            window.appendLog("\n═══════════════════════════════════════");
            window.appendLog("Simulación completada (demo simplificada)");
            window.appendLog("═══════════════════════════════════════");
            window.appendLog(memoryManager.getMemoryStatus());
            window.appendLog("\nNota: Esta es una demostración básica.");
            window.appendLog("Para implementar la simulación completa,");
            window.appendLog("integre todos los módulos según el diseño.");
            
            // Cerrar IO Manager
            ioManager.shutdown();
            
        } catch (Exception e) {
            window.appendLog("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static CPUScheduler createScheduler(String algorithm, int quantum) {
        switch (algorithm.toUpperCase()) {
            case "FCFS":
                return new FCFS();
            case "SJF":
                return new SJF();
            case "RR":
                return new RoundRobin(quantum);
            default:
                return new FCFS();
        }
    }
    
    private static PageReplacement createMemoryAlgorithm(String algorithm, List<Process> processes) {
        switch (algorithm.toUpperCase()) {
            case "FIFO":
                return new FIFO();
            case "LRU":
                return new LRU();
            case "OPTIMAL":
            case "OPT":
                // Para Optimal necesitamos las referencias futuras
                List<Integer> allRefs = new ArrayList<>();
                for (Process p : processes) {
                    if (p.getPageReferences() != null) {
                        allRefs.addAll(p.getPageReferences());
                    }
                }
                return new Optimal(allRefs);
            default:
                return new FIFO();
        }
    }
}
