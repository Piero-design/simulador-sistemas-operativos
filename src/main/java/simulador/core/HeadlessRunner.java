package simulador.core;

import simulador.memory.MemoryManager;
import simulador.memory.FIFO;
import simulador.scheduler.RoundRobin;
import java.io.IOException;

public class HeadlessRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println("Usage: java simulador.core.HeadlessRunner <quantum> <process_file>");
            System.exit(1);
        }
        int quantum = Integer.parseInt(args[0]);
        String file = args[1];

        System.out.println("[HeadlessRunner] Running RoundRobin with quantum=" + quantum + " on file=" + file);

        RoundRobin rr = new RoundRobin(quantum);
        // Create MemoryManager with FIFO replacement (defaults chosen to match typical GUI settings)
        int totalFrames = 256; // adjust if needed
        MemoryManager mm = new MemoryManager(totalFrames, new FIFO());
        Simulator sim = new Simulator(rr, mm);

        sim.loadProcessesFromFile(file);
        sim.start();

        // Wait until simulation finishes
        while (sim.isRunning()) {
            Thread.sleep(200);
        }

        System.out.println("[HeadlessRunner] Simulation finished. Metrics:\n" + sim.getMetricsCollector().generateReport());
    }
}
