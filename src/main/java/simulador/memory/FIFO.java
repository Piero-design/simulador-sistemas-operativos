package simulador.memory;

import java.util.LinkedList;
import java.util.Queue;

public class FIFO implements PageReplacement {

    private Queue<Integer> queue = new LinkedList<>();

    @Override
    public int selectVictimFrame() {
        return queue.poll();
    }

    @Override
    public void useFrame(int frameId) {
        queue.add(frameId);
    }

    @Override
    public String getName() {
        return "FIFO";
    }
}
