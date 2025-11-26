package simulador.scheduler;

import simulador.process.Process;
import java.util.LinkedList;
import java.util.Queue;

public class RoundRobin implements CPUScheduler {

    private Queue<Process> queue = new LinkedList<>();
    private int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public void addProcess(Process p) {
        queue.add(p);
    }

    @Override
    public Process getNextProcess() {
        return queue.poll();
    }

    public int getQuantum() { return quantum; }

    @Override
    public String getName() {
        return "Round Robin (q=" + quantum + ")";
    }
}
