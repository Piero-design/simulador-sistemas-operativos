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
            
            // Configurar callback para iniciar simulación cuando se presione el botón
            window.setOnStartCallback(() -> {
                new Thread(() -> runFullSimulation(window)).start();
            });
            
            // Auto-iniciar simulación (para pruebas)
            // Comentar esta línea si quieres que espere al botón
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Esperar a que la ventana se renderice
                    SwingUtilities.invokeLater(() -> {
                        window.appendLog("Auto-iniciando simulación en 1 segundo...");
                    });
                    Thread.sleep(1000);
                    runFullSimulation(window);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
    
    /**
     * Simulación completa y funcional
     */
    private static void runFullSimulation(MainWindow window) {
        new Thread(() -> {
            try {
                window.appendLog("═══════════════════════════════════════");
                window.appendLog("Cargando configuración del simulador...");
                window.appendLog("═══════════════════════════════════════\n");
                
                // 1. Cargar procesos desde archivo
                List<Process> processes = ProcessParser.parseFile("procesos.txt");
                window.appendLog("✓ Cargados " + processes.size() + " procesos desde procesos.txt\n");
                
                // 2. Configurar parámetros
                int totalFrames = 12;
                String schedAlgorithm = "RR"; // FCFS, SJF, RR
                int quantum = 2;
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
                
                // 4. Crear y configurar el motor del planificador
                SchedulerEngine engine = new SchedulerEngine(
                    processes, scheduler, memoryManager, sync, ioManager, metrics, quantum
                );
                
                // Configurar callback para actualizar GUI
                engine.setGUICallback(new SchedulerEngine.GUICallback() {
                    @Override
                    public void onUpdate(List<Process> ready, List<Process> blocked, 
                                       Process running, String schedulerName, MemoryManager memory) {
                        SwingUtilities.invokeLater(() -> {
                            window.updateQueues(ready, blocked, running, schedulerName);
                            window.updateMemory(memory);
                        });
                    }
                    
                    @Override
                    public void onLog(String message) {
                        SwingUtilities.invokeLater(() -> window.appendLog(message));
                    }
                    
                    @Override
                    public void addGanttEntry(String pid, int startTime, int duration) {
                        SwingUtilities.invokeLater(() -> window.addGanttEntry(pid, startTime, duration));
                    }
                });
                
                // 5. Iniciar simulación
                engine.start();
                
                // 6. Mostrar métricas finales
                SwingUtilities.invokeLater(() -> {
                    window.simulationCompleted();
                });
                
            } catch (Exception e) {
                window.appendLog("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
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
