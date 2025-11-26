package simulador.memory;

import java.util.*;

public class Optimal implements PageReplacement {

    private Set<Integer> frames = new HashSet<>();
    private List<Integer> futureReferences;
    private int currentIndex = 0;

    public Optimal() {
        this.futureReferences = new ArrayList<>();
    }

    public void setFutureReferences(List<Integer> references) {
        this.futureReferences = references;
        this.currentIndex = 0;
    }

    @Override
    public int selectVictimFrame() {
        if (frames.isEmpty()) {
            return -1;
        }

        // Encuentra el marco que no se usará por más tiempo
        int victimFrame = -1;
        int farthestUse = -1;

        for (int frame : frames) {
            int nextUse = findNextUse(frame);
            if (nextUse > farthestUse) {
                farthestUse = nextUse;
                victimFrame = frame;
            }
        }

        frames.remove(victimFrame);
        return victimFrame;
    }

    private int findNextUse(int frame) {
        for (int i = currentIndex; i < futureReferences.size(); i++) {
            if (futureReferences.get(i) == frame) {
                return i;
            }
        }
        return Integer.MAX_VALUE; // No se usará más
    }

    @Override
    public void useFrame(int frameId) {
        frames.add(frameId);
        currentIndex++;
    }

    @Override
    public String getName() {
        return "Optimal";
    }

    public void removeFrame(int frameId) {
        frames.remove(frameId);
    }

    public void advanceIndex() {
        currentIndex++;
    }
}
