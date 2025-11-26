package simulador.memory;

public interface PageReplacement {
    int selectVictimFrame();
    void useFrame(int frameId);
    String getName();
}
