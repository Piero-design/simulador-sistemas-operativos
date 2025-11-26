package simulador.process;

import java.util.List;

public class Process {
    public enum State {
        NEW, READY, RUNNING, BLOCKED, TERMINATED
    }

    private String pid;
    private int arrivalTime;
    private List<String> bursts; // CPU(4), E/S(3), CPU(5)
    private int priority;
    private int pages;
    private State state = State.NEW;

    public Process(String pid, int arrivalTime, List<String> bursts, int priority, int pages) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.bursts = bursts;
        this.priority = priority;
        this.pages = pages;
    }

    public String getPid() { return pid; }
    public int getArrivalTime() { return arrivalTime; }
    public List<String> getBursts() { return bursts; }
    public int getPriority() { return priority; }
    public int getPages() { return pages; }
    public State getState() { return state; }

    public void setState(State s) { this.state = s; }
}
