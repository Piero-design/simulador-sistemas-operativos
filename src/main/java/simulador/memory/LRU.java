package simulador.memory;

import java.util.HashMap;
import java.util.Map;

public class LRU implements PageReplacement {

    private Map<Integer, Long> lastUsedTime = new HashMap<>();
    private long currentTime = 0;

    @Override
    public int selectVictimFrame() {
        if (lastUsedTime.isEmpty()) {
            return -1;
        }
        
        int victimFrame = -1;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<Integer, Long> entry : lastUsedTime.entrySet()) {
            if (entry.getValue() < oldestTime) {
                oldestTime = entry.getValue();
                victimFrame = entry.getKey();
            }
        }
        
        lastUsedTime.remove(victimFrame);
        return victimFrame;
    }

    @Override
    public void useFrame(int frameId) {
        lastUsedTime.put(frameId, currentTime++);
    }

    @Override
    public String getName() {
        return "LRU";
    }
    
    public void removeFrame(int frameId) {
        lastUsedTime.remove(frameId);
    }
}
