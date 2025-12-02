package simulador.scheduler;

import simulador.process.Process;
import java.util.LinkedList;
import java.util.Queue;

public class RoundRobin implements CPUScheduler {

    private Queue<Process> queue = new LinkedList<>();
    // For stronger uniqueness by PID, maintain a PID queue and map
    private Queue<String> pidQueue = new LinkedList<>();
    private java.util.Map<String, Process> pidMap = new java.util.HashMap<>();
    private int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public void addProcess(Process p) {
        // Ignore nulls, terminated processes or duplicates already in the queue
        if (p == null) return;
        String pid = p.getPid();
        // Don't enqueue terminated or currently running processes
        if (p.getState() == Process.State.TERMINATED || p.getState() == Process.State.RUNNING) return;
        if (pidQueue.contains(pid)) {
            // already queued; skip
            return;
        }
        pidQueue.add(pid);
        pidMap.put(pid, p);
        // synchronize queue for backward compatibility (if other code reads queue)
        queue.add(p);
    }

    @Override
    public Process getNextProcess() {
        // Poll until we find a PID that maps to a non-terminated, non-running process
        while (!pidQueue.isEmpty()) {
            String pid = pidQueue.poll();
            Process p = pidMap.remove(pid);
            // also remove one occurrence from legacy queue if present
            queue.removeIf(q -> q.getPid().equals(pid));
            if (p == null) continue;
            if (p.getState() == Process.State.TERMINATED) continue;
            // If somehow the process is already running, skip it
            if (p.getState() == Process.State.RUNNING) continue;
            return p;
        }
        return null;
    }

    public int getQuantum() { return quantum; }

    @Override
    public String getName() {
        return "Round Robin (q=" + quantum + ")";
    }
}
