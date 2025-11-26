package simulador.process;

import java.util.List;

public class Process {
    public enum State {
        NEW, READY, RUNNING, BLOCKED_MEM, BLOCKED_IO, TERMINATED
    }

    private String pid;
    private int arrivalTime;
    private List<String> bursts; // CPU(4), E/S(3), CPU(5)
    private int priority;
    private int pages;
    private List<Integer> pageReferences;
    private State state = State.NEW;
    
    // Para métricas
    private int currentBurstIndex = 0;
    private long startTime = -1;
    private long finishTime = -1;
    private long waitTime = 0;

    public Process(String pid, int arrivalTime, List<String> bursts, int priority, int pages) {
        this(pid, arrivalTime, bursts, priority, pages, null);
    }
    
    public Process(String pid, int arrivalTime, List<String> bursts, int priority, int pages, List<Integer> pageReferences) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.bursts = bursts;
        this.priority = priority;
        this.pages = pages;
        this.pageReferences = pageReferences;
    }

    public String getPid() { return pid; }
    public int getArrivalTime() { return arrivalTime; }
    public List<String> getBursts() { return bursts; }
    public int getPriority() { return priority; }
    public int getPages() { return pages; }
    public List<Integer> getPageReferences() { return pageReferences; }
    public State getState() { return state; }
    public int getCurrentBurstIndex() { return currentBurstIndex; }
    public long getStartTime() { return startTime; }
    public long getFinishTime() { return finishTime; }
    public long getWaitTime() { return waitTime; }

    public void setState(State s) { this.state = s; }
    public void setCurrentBurstIndex(int index) { this.currentBurstIndex = index; }
    public void setStartTime(long time) { this.startTime = time; }
    public void setFinishTime(long time) { this.finishTime = time; }
    public void setWaitTime(long time) { this.waitTime = time; }
    
    public String getCurrentBurst() {
        if (currentBurstIndex < bursts.size()) {
            return bursts.get(currentBurstIndex);
        }
        return null;
    }
    
    public void nextBurst() {
        currentBurstIndex++;
    }
    
    public boolean hasMoreBursts() {
        return currentBurstIndex < bursts.size();
    }
}
