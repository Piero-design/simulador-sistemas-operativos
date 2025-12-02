package simulador.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class SimulationLogger {
    private static final Path LOG = Path.of("simulation-gantt.log");

    public static synchronized void log(String line) {
        String entry = Instant.now().toString() + " - " + line + System.lineSeparator();
        try {
            Files.writeString(LOG, entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("[SimulationLogger] Error writing log: " + e.getMessage());
        }
    }
}
