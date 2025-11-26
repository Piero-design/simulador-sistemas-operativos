package simulador.scheduler;

import simulador.process.Process;

public interface CPUScheduler {
    void addProcess(Process p);
    Process getNextProcess();
    String getName();
}
