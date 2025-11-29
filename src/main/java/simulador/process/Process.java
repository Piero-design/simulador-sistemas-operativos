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
    private int currentBurstIndex = 0;
    private long startTime = 0;
    private long finishTime = 0;

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
    public int getCurrentBurstIndex() { return currentBurstIndex; }
    public long getStartTime() { return startTime; }
    public long getFinishTime() { return finishTime; }

    public void setState(State s) { this.state = s; }
    public void setCurrentBurstIndex(int index) { this.currentBurstIndex = index; }
    public void setStartTime(long time) { this.startTime = time; }
    public void setFinishTime(long time) { this.finishTime = time; }
    
    public int getTotalCPUTime() {
        int total = 0;
        for (String burst : bursts) {
            if (burst.startsWith("CPU")) {
                int start = burst.indexOf('(') + 1;
                int end = burst.indexOf(')');
                total += Integer.parseInt(burst.substring(start, end));
            }
        }
        return total;
    }
    
    @Override
    public String toString() {
        return String.format("Process[%s, arrival=%d, bursts=%d, priority=%d, pages=%d, state=%s]",
                pid, arrivalTime, bursts.size(), priority, pages, state);
    }
}
