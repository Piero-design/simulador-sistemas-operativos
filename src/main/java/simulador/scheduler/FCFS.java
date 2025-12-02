package simulador.scheduler;

import simulador.process.Process;
import java.util.LinkedList;
import java.util.Queue;

public class FCFS implements CPUScheduler {

    private Queue<Process> readyQueue = new LinkedList<>();

    @Override
    public void addProcess(Process p) {
        if (p == null) return;
        if (p.getState() == Process.State.TERMINATED || p.getState() == Process.State.RUNNING) return;
        if (readyQueue.contains(p)) return;
        readyQueue.add(p);
    }

    @Override
    public Process getNextProcess() {
        return readyQueue.poll();
    }

    @Override
    public String getName() {
        return "FCFS";
    }
}
