package simulador.utils;

import simulador.process.Process;
import java.util.*;

/**
 * Sistema de recolección y cálculo de métricas del simulador
 */
public class Metrics {
    
    // Métricas de planificación
    private int totalProcesses = 0;
    private long totalWaitTime = 0;
    private long totalTurnaroundTime = 0;
    private long totalResponseTime = 0;
    private long cpuBusyTime = 0;
    private long totalSimulationTime = 0;
    
    // Métricas de memoria
    private int totalPageFaults = 0;
    private int totalPageReplacements = 0;
    private Map<String, Integer> pageFaultsByProcess = new HashMap<>();
    
    // Métricas de E/S
    private int totalIOOperations = 0;
    private long totalIOTime = 0;
    
    // Tiempos por proceso
    private Map<String, Long> arrivalTimes = new HashMap<>();
    private Map<String, Long> startTimes = new HashMap<>();
    private Map<String, Long> finishTimes = new HashMap<>();
    private Map<String, Long> waitTimes = new HashMap<>();
    
    // ===== Registro de eventos =====
    
    public void recordProcessArrival(Process process) {
        totalProcesses++;
        arrivalTimes.put(process.getPid(), (long) process.getArrivalTime());
    }
    
    public void recordProcessStart(String pid, long time) {
        startTimes.put(pid, time);
    }
    
    public void recordProcessFinish(String pid, long time) {
        finishTimes.put(pid, time);
    }
    
    public void recordWaitTime(String pid, long time) {
        waitTimes.put(pid, time);
        totalWaitTime += time;
    }
    
    public void recordCPUBusy(long time) {
        cpuBusyTime += time;
    }
    
    public void recordPageFault(String pid) {
        totalPageFaults++;
        pageFaultsByProcess.put(pid, pageFaultsByProcess.getOrDefault(pid, 0) + 1);
    }
    
    public void recordPageReplacement() {
        totalPageReplacements++;
    }
    
    public void recordIOOperation(long duration) {
        totalIOOperations++;
        totalIOTime += duration;
    }
    
    public void setSimulationTime(long time) {
        this.totalSimulationTime = time;
    }
    
    // ===== Cálculo de métricas =====
    
    public double getAverageWaitTime() {
        return totalProcesses > 0 ? (double) totalWaitTime / totalProcesses : 0;
    }
    
    public double getAverageTurnaroundTime() {
        if (totalProcesses == 0) return 0;
        
        long total = 0;
        for (String pid : finishTimes.keySet()) {
            long arrival = arrivalTimes.getOrDefault(pid, 0L);
            long finish = finishTimes.get(pid);
            total += (finish - arrival);
        }
        
        return (double) total / totalProcesses;
    }
    
    public double getAverageResponseTime() {
        if (totalProcesses == 0) return 0;
        
        long total = 0;
        for (String pid : startTimes.keySet()) {
            long arrival = arrivalTimes.getOrDefault(pid, 0L);
            long start = startTimes.get(pid);
            total += (start - arrival);
        }
        
        return (double) total / totalProcesses;
    }
    
    public double getCPUUtilization() {
        return totalSimulationTime > 0 ? (double) cpuBusyTime / totalSimulationTime * 100 : 0;
    }
    
    public double getPageFaultRate() {
        return totalProcesses > 0 ? (double) totalPageFaults / totalProcesses : 0;
    }
    
    // ===== Generación de reportes =====
    
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append("║        REPORTE DE MÉTRICAS DEL SIMULADOR       ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        
        // Métricas de planificación
        sb.append("║ PLANIFICACIÓN DE CPU                           ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Procesos totales:            %17d ║\n", totalProcesses));
        sb.append(String.format("║ Tiempo promedio de espera:   %17.2f ║\n", getAverageWaitTime()));
        sb.append(String.format("║ Tiempo promedio de retorno:  %17.2f ║\n", getAverageTurnaroundTime()));
        sb.append(String.format("║ Tiempo promedio de respuesta:%17.2f ║\n", getAverageResponseTime()));
        sb.append(String.format("║ Utilización de CPU:          %16.2f%% ║\n", getCPUUtilization()));
        
        // Métricas de memoria
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append("║ GESTIÓN DE MEMORIA                             ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Fallos de página totales:    %17d ║\n", totalPageFaults));
        sb.append(String.format("║ Reemplazos realizados:       %17d ║\n", totalPageReplacements));
        sb.append(String.format("║ Tasa de fallos de página:    %17.2f ║\n", getPageFaultRate()));
        
        // Métricas de E/S
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append("║ ENTRADA/SALIDA                                 ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Operaciones de E/S:          %17d ║\n", totalIOOperations));
        sb.append(String.format("║ Tiempo total en E/S:         %17d ║\n", totalIOTime));
        
        sb.append("╚════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    public Map<String, Object> getMetricsMap() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("totalProcesses", totalProcesses);
        metrics.put("averageWaitTime", getAverageWaitTime());
        metrics.put("averageTurnaroundTime", getAverageTurnaroundTime());
        metrics.put("averageResponseTime", getAverageResponseTime());
        metrics.put("cpuUtilization", getCPUUtilization());
        metrics.put("totalPageFaults", totalPageFaults);
        metrics.put("totalPageReplacements", totalPageReplacements);
        metrics.put("pageFaultRate", getPageFaultRate());
        metrics.put("totalIOOperations", totalIOOperations);
        metrics.put("totalIOTime", totalIOTime);
        
        return metrics;
    }
}
