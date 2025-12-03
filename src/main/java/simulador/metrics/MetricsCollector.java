package simulador.metrics;

import simulador.process.Process;
import simulador.process.ProcessThread;
import java.util.*;

/**
 * Recolecta y calcula métricas de desempeño del sistema
 */
public class MetricsCollector {
    
    private final List<ProcessMetrics> processMetrics;
    private long totalCPUTime;
    private long totalIdleTime;
    private long totalContextSwitchTime;
    private long simulationStartTime;
    private long simulationEndTime;
    private int totalPageFaults;
    private int totalPageReplacements;

    public MetricsCollector() {
        this.processMetrics = new ArrayList<>();
        this.totalCPUTime = 0;
        this.totalIdleTime = 0;
        this.totalContextSwitchTime = 0;
    }

    public void startSimulation() {
        simulationStartTime = System.currentTimeMillis();
    }

    public void endSimulation() {
        simulationEndTime = System.currentTimeMillis();
    }

    public void recordProcess(ProcessThread thread) {
        Process process = thread.getProcess();
        recordProcess(process, thread.getWaitingTime(), thread.getTurnaroundTime(), calculateResponseTime(thread));
    }

    public void recordProcess(Process process, long waitingMillis, long turnaroundMillis, long responseMillis) {
        ProcessMetrics metrics = new ProcessMetrics();

        metrics.pid = process.getPid();
        metrics.arrivalTime = process.getArrivalTime();
        metrics.waitingTime = waitingMillis;
        metrics.turnaroundTime = turnaroundMillis;
        metrics.burstTime = calculateTotalBurstTime(process);
        metrics.responseTime = responseMillis;

        processMetrics.add(metrics);
    }

    public void addCPUTime(long time) {
        totalCPUTime += time;
    }

    public void addIdleTime(long time) {
        totalIdleTime += time;
    }

    public void addContextSwitchTime(long time) {
        totalContextSwitchTime += time;
    }

    public void setPageFaults(int faults) {
        this.totalPageFaults = faults;
    }

    public void setPageReplacements(int replacements) {
        this.totalPageReplacements = replacements;
    }

    private int calculateTotalBurstTime(Process process) {
        int total = 0;
        for (String burst : process.getBursts()) {
            if (burst.startsWith("CPU")) {
                int start = burst.indexOf('(') + 1;
                int end = burst.indexOf(')');
                total += Integer.parseInt(burst.substring(start, end));
            }
        }
        return total;
    }

    private long calculateResponseTime(ProcessThread thread) {
        // Tiempo desde llegada hasta primera ejecución
        List<ProcessThread.ExecutionEvent> log = thread.getExecutionLog();
        for (ProcessThread.ExecutionEvent event : log) {
            if (event.getDescription().contains("Running on CPU")) {
                return event.getTimestamp() - simulationStartTime;
            }
        }
        return 0;
    }

    public double getAverageWaitingTime() {
        if (processMetrics.isEmpty()) return 0.0;
        return processMetrics.stream()
                .mapToLong(m -> m.waitingTime)
                .average()
                .orElse(0.0);
    }

    public double getAverageTurnaroundTime() {
        if (processMetrics.isEmpty()) return 0.0;
        return processMetrics.stream()
                .mapToLong(m -> m.turnaroundTime)
                .average()
                .orElse(0.0);
    }

    public double getAverageResponseTime() {
        if (processMetrics.isEmpty()) return 0.0;
        return processMetrics.stream()
                .mapToLong(m -> m.responseTime)
                .average()
                .orElse(0.0);
    }

    public double getCPUUtilization() {
        long totalTime = simulationEndTime - simulationStartTime;
        if (totalTime == 0) return 0.0;
        long busyTime = totalCPUTime + totalContextSwitchTime;
        return (double) busyTime / totalTime * 100.0;
    }

    public long getTotalContextSwitchTime() {
        return totalContextSwitchTime;
    }

    public int getTotalPageFaults() {
        return totalPageFaults;
    }

    public int getTotalPageReplacements() {
        return totalPageReplacements;
    }

    public List<ProcessMetrics> getProcessMetrics() {
        return new ArrayList<>(processMetrics);
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== SIMULATION METRICS ===\n\n");
        
        report.append("Process Metrics:\n");
        report.append(String.format("%-8s %-12s %-12s %-12s %-12s\n", 
                "PID", "Arrival", "Waiting", "Turnaround", "Response"));
        report.append("-".repeat(60)).append("\n");
        
        for (ProcessMetrics m : processMetrics) {
            report.append(String.format("%-8s %-12d %-12d %-12d %-12d\n",
                    m.pid, m.arrivalTime, m.waitingTime, m.turnaroundTime, m.responseTime));
        }
        
        report.append("\nAverage Metrics:\n");
        report.append(String.format("Average Waiting Time: %.2f ms\n", getAverageWaitingTime()));
        report.append(String.format("Average Turnaround Time: %.2f ms\n", getAverageTurnaroundTime()));
        report.append(String.format("Average Response Time: %.2f ms\n", getAverageResponseTime()));
        report.append(String.format("CPU Utilization: %.2f%%\n", getCPUUtilization()));
        report.append(String.format("Context Switch Time: %d ms\n", totalContextSwitchTime));
        
        report.append("\nMemory Metrics:\n");
        report.append(String.format("Total Page Faults: %d\n", totalPageFaults));
        report.append(String.format("Total Page Replacements: %d\n", totalPageReplacements));
        
        return report.toString();
    }

    // Clase interna para almacenar métricas de un proceso
    public static class ProcessMetrics {
        public String pid;
        public int arrivalTime;
        public long waitingTime;
        public long turnaroundTime;
        public long responseTime;
        public int burstTime;

        @Override
        public String toString() {
            return String.format("PID: %s, Waiting: %d, Turnaround: %d, Response: %d",
                    pid, waitingTime, turnaroundTime, responseTime);
        }
    }
}
