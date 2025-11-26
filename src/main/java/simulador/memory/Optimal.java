package simulador.memory;

import java.util.*;

public class Optimal implements PageReplacement {

    private List<Integer> futureReferences;
    private Set<Integer> currentFrames = new HashSet<>();
    private int currentIndex = 0;

    public Optimal(List<Integer> futureReferences) {
        this.futureReferences = futureReferences;
    }

    @Override
    public int selectVictimFrame() {
        if (currentFrames.isEmpty()) return -1;
        
        int victim = -1;
        int farthestUse = -1;
        
        for (int frame : currentFrames) {
            int nextUse = findNextUse(frame);
            if (nextUse > farthestUse) {
                farthestUse = nextUse;
                victim = frame;
            }
        }
        
        currentFrames.remove(victim);
        return victim;
    }

    private int findNextUse(int page) {
        for (int i = currentIndex; i < futureReferences.size(); i++) {
            if (futureReferences.get(i) == page) {
                return i;
            }
        }
        return Integer.MAX_VALUE; // Nunca se usará
    }

    @Override
    public void useFrame(int frameId) {
        currentFrames.add(frameId);
        currentIndex++;
    }

    @Override
    public String getName() {
        return "Óptimo";
    }
    
    public void setCurrentIndex(int index) {
        this.currentIndex = index;
    }
}
