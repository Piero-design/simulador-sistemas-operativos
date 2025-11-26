package simulador.scheduler;

import simulador.process.Process;
import java.util.LinkedList;
import java.util.Queue;

public class FCFS implements CPUScheduler {

    private Queue<Process> readyQueue = new LinkedList<>();

    @Override
    public void addProcess(Process p) {
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
