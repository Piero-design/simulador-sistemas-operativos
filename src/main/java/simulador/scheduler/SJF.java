package simulador.scheduler;

import simulador.process.Process;
import java.util.*;

public class SJF implements CPUScheduler {

    private PriorityQueue<Process> queue;

    public SJF() {
        queue = new PriorityQueue<>(Comparator.comparingInt(
                p -> Integer.parseInt(p.getBursts().get(0).replace("CPU(", "").replace(")", ""))
        ));
    }

    @Override
    public void addProcess(Process p) {
        queue.add(p);
    }

    @Override
    public Process getNextProcess() {
        return queue.poll();
    }

    @Override
    public String getName() {
        return "SJF";
    }
}
