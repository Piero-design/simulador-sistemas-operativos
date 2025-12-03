package simulador.core;

import simulador.memory.MemoryManager;
import simulador.memory.FIFO;
import simulador.scheduler.CPUScheduler;
import simulador.scheduler.FCFS;
import simulador.scheduler.RoundRobin;
import simulador.scheduler.SJF;
import java.io.IOException;
import java.util.Locale;

public class HeadlessRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println("Usage: java simulador.core.HeadlessRunner <scheduler> [quantum] <process_file> [--cs N]");
            System.out.println("Schedulers: FCFS | SJF | RR");
            System.out.println("Example RR: java simulador.core.HeadlessRunner RR 3 procesos.txt --cs 1");
            System.out.println("Example FCFS: java simulador.core.HeadlessRunner FCFS procesos.txt");
            System.exit(1);
        }
        String schedulerArg = args[0].toUpperCase(Locale.ROOT);
        int argIndex = 1;
        int quantum = 3; // default for RR if not provided
        CPUScheduler scheduler;
        switch (schedulerArg) {
            case "RR":
            case "ROUNDROBIN":
                if (args.length < 3) {
                    System.out.println("[HeadlessRunner] Round Robin requires a quantum argument.");
                    System.exit(1);
                }
                quantum = Integer.parseInt(args[argIndex]);
                argIndex++;
                scheduler = new RoundRobin(quantum);
                break;
            case "SJF":
                scheduler = new SJF();
                break;
            case "FCFS":
            default:
                scheduler = new FCFS();
                break;
        }

        int contextSwitchCost = 0;
        String file = null;
        while (argIndex < args.length) {
            String token = args[argIndex];
            if (token.equalsIgnoreCase("--cs") || token.equalsIgnoreCase("-cs")) {
                if (argIndex + 1 >= args.length) {
                    System.out.println("[HeadlessRunner] Missing value for --cs option.");
                    System.exit(1);
                }
                contextSwitchCost = Integer.parseInt(args[++argIndex]);
            } else if (token.toLowerCase(Locale.ROOT).startsWith("--cs=")) {
                contextSwitchCost = Integer.parseInt(token.substring(token.indexOf('=') + 1));
            } else if (file == null) {
                file = token;
            } else {
                System.out.println("[HeadlessRunner] Unknown argument: " + token);
            }
            argIndex++;
        }

        if (file == null) {
            System.out.println("[HeadlessRunner] Missing process file argument.");
            System.exit(1);
        }

        System.out.printf("[HeadlessRunner] Running %s on file=%s", scheduler.getName(), file);
        if (scheduler instanceof RoundRobin) {
            System.out.printf(" (quantum=%d)", quantum);
        }
        if (contextSwitchCost > 0) {
            System.out.printf(" [cs=%d]", contextSwitchCost);
        }
        System.out.println();

        // Create MemoryManager with FIFO replacement (defaults chosen to match typical GUI settings)
        int totalFrames = 256; // adjust if needed
        MemoryManager mm = new MemoryManager(totalFrames, new FIFO());
        Simulator sim = new Simulator(scheduler, mm, contextSwitchCost);

        sim.loadProcessesFromFile(file);
        sim.start();

        // Wait until simulation finishes
        while (sim.isRunning()) {
            Thread.sleep(200);
        }

        System.out.println("[HeadlessRunner] Simulation finished. Metrics:\n" + sim.getMetricsCollector().generateReport());
    }
}
