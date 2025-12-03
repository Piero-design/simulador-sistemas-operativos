package simulador.gui;

import simulador.process.Process;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel que muestra el diagrama de Gantt de la ejecución de procesos
 */
public class GanttPanel extends JPanel {
    
    private final List<GanttEntry> cpuData;
    private final List<GanttEntry> ioData;
    private int maxTime;
    private int pixelsPerUnit = 30;
    private static final int PROCESS_HEIGHT = 40;
    private static final int TOP_MARGIN = 50;
    private static final int LEFT_MARGIN = 80;
    private static final int BOTTOM_MARGIN = 50;
    private static final Color CONTEXT_SWITCH_COLOR = new Color(169, 169, 169);
    
    private Map<String, Color> processColors;
    private int colorIndex = 0;
    private final Color[] COLORS = {
        new Color(100, 149, 237), // Cornflower Blue
        new Color(255, 127, 80),  // Coral
        new Color(144, 238, 144), // Light Green
        new Color(255, 182, 193), // Light Pink
        new Color(221, 160, 221), // Plum
        new Color(255, 218, 185), // Peach Puff
        new Color(176, 224, 230), // Powder Blue
        new Color(240, 230, 140)  // Khaki
    };

    public GanttPanel() {
        cpuData = new ArrayList<>();
        ioData = new ArrayList<>();
        processColors = new HashMap<>();
        maxTime = 0;
        
        setPreferredSize(new Dimension(800, 380));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            "Diagrama de Gantt - Planificación de CPU",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
    }

    /**
     * Agrega una entrada al diagrama de Gantt
     */
    public void addEntry(String pid, int startTime, int endTime) {
        addCpuEntry(pid, startTime, endTime);
    }

    public void addCpuEntry(String pid, int startTime, int endTime) {
        addSegment(cpuData, pid, startTime, endTime, false);
    }

    private void addSegment(List<GanttEntry> target, String pid, int startTime, int endTime, boolean contextSwitch) {
        if (endTime <= startTime) {
            return;
        }

        GanttEntry entry = new GanttEntry(pid, startTime, endTime, contextSwitch);
        target.add(entry);

        if (!contextSwitch && !processColors.containsKey(pid)) {
            processColors.put(pid, COLORS[colorIndex % COLORS.length]);
            colorIndex++;
        }

        maxTime = Math.max(maxTime, endTime);
        repaint();
    }

    public void addContextSwitch(int startTime, int endTime) {
        addSegment(cpuData, "CS", startTime, endTime, true);
    }

    public void addIoEntry(String pid, int startTime, int endTime) {
        addSegment(ioData, pid, startTime, endTime, false);
    }

    /**
     * Limpia el diagrama
     */
    public void clear() {
        cpuData.clear();
        ioData.clear();
        processColors.clear();
        maxTime = 0;
        colorIndex = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cpuData.isEmpty() && ioData.isEmpty()) {
            // Mostrar mensaje cuando no hay datos
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            String msg = "No hay datos para mostrar. Inicia la simulación.";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
            return;
        }

        // Dibujar barras del diagrama
        drawGanttBars(g2d);
        drawIoBars(g2d);
        
        // Dibujar eje de tiempo
        drawTimeAxis(g2d);
        
        // Dibujar leyenda
        drawLegend(g2d);
    }

    private void drawTimeAxis(Graphics2D g2d) {
        int y = TOP_MARGIN + PROCESS_HEIGHT;
        if (!ioData.isEmpty()) {
            y += PROCESS_HEIGHT + 40;
        }
        y += 10;
        int endX = LEFT_MARGIN + maxTime * pixelsPerUnit;
        
        // Línea principal del eje
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(LEFT_MARGIN, y, Math.min(endX, getWidth() - 20), y);
        
        // Marcas de tiempo
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int time = 0; time <= maxTime; time++) {
            int x = LEFT_MARGIN + time * pixelsPerUnit;
            if (x > getWidth() - 30) break;
            
            // Marca vertical
            g2d.drawLine(x, y - 5, x, y + 5);
            
            // Número
            String timeStr = String.valueOf(time);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x - fm.stringWidth(timeStr) / 2;
            g2d.drawString(timeStr, textX, y + 20);
        }
        
        // Etiqueta del eje
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Tiempo (unidades)", LEFT_MARGIN, y + 40);
    }

    private void drawTrack(Graphics2D g2d, List<GanttEntry> data, int y, String label, boolean allowContextSwitch) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(label, 10, y + PROCESS_HEIGHT / 2 + 5);

        for (GanttEntry entry : data) {
            int startX = LEFT_MARGIN + entry.startTime * pixelsPerUnit;
            int width = (entry.endTime - entry.startTime) * pixelsPerUnit;

            if (startX > getWidth() - 30) {
                continue;
            }
            if (startX + width > getWidth() - 30) {
                width = getWidth() - 30 - startX;
            }

            Color color = entry.contextSwitch ? CONTEXT_SWITCH_COLOR : processColors.get(entry.pid);
            if (color == null) {
                color = CONTEXT_SWITCH_COLOR;
            }
            if (!allowContextSwitch && entry.contextSwitch) {
                color = CONTEXT_SWITCH_COLOR;
            }

            g2d.setColor(color);
            g2d.fillRect(startX, y, width, PROCESS_HEIGHT);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(startX, y, width, PROCESS_HEIGHT);

            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            String textLabel = entry.contextSwitch ? "CS" : entry.pid;
            int textX = startX + (width - fm.stringWidth(textLabel)) / 2;
            int textY = y + (PROCESS_HEIGHT + fm.getAscent()) / 2 - 2;

            g2d.setColor(Color.BLACK);
            g2d.drawString(textLabel, textX + 1, textY + 1);

            g2d.setColor(entry.contextSwitch ? Color.BLACK : Color.WHITE);
            g2d.drawString(textLabel, textX, textY);
        }
    }

    private void drawGanttBars(Graphics2D g2d) {
        int cpuY = TOP_MARGIN;
        drawTrack(g2d, cpuData, cpuY, "CPU", true);
    }

    private void drawIoBars(Graphics2D g2d) {
        if (ioData.isEmpty()) {
            return;
        }
        int ioY = TOP_MARGIN + PROCESS_HEIGHT + 40;
        drawTrack(g2d, ioData, ioY, "E/S", false);
    }

    private void drawLegend(Graphics2D g2d) {
        int legendY = TOP_MARGIN + PROCESS_HEIGHT + 70;
        if (!ioData.isEmpty()) {
            legendY += PROCESS_HEIGHT + 40;
        }
        int legendX = LEFT_MARGIN;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Procesos:", legendX, legendY);
        
        int x = legendX + 80;
        int spacing = 0;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        
        for (Map.Entry<String, Color> entry : processColors.entrySet()) {
            String pid = entry.getKey();
            Color color = entry.getValue();
            
            // Cuadro de color
            g2d.setColor(color);
            g2d.fillRect(x + spacing, legendY - 10, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + spacing, legendY - 10, 15, 15);
            
            // Etiqueta
            g2d.drawString(pid, x + spacing + 20, legendY);
            
            spacing += 60;
            
            // Salto de línea si es necesario
            if (x + spacing > getWidth() - 100) {
                x = legendX;
                spacing = 80;
                legendY += 20;
            }
        }

        boolean hasContextSwitch = cpuData.stream().anyMatch(e -> e.contextSwitch);
        if (hasContextSwitch) {
            g2d.setColor(CONTEXT_SWITCH_COLOR);
            g2d.fillRect(x + spacing, legendY - 10, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + spacing, legendY - 10, 15, 15);
            g2d.drawString("CS (Cambio de contexto)", x + spacing + 20, legendY);
        }
    }

    /**
     * Ajusta el zoom del diagrama
     */
    public void setPixelsPerUnit(int pixels) {
        this.pixelsPerUnit = Math.max(10, Math.min(pixels, 50));
        repaint();
    }

    /**
     * Obtiene información de la última ejecución
     */
    public String getExecutionSummary() {
        if (cpuData.isEmpty() && ioData.isEmpty()) {
            return "Sin datos de ejecución";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE EJECUCIÓN ===\n");
        sb.append(String.format("Tiempo total: %d unidades\n", maxTime));
        sb.append(String.format("Procesos ejecutados: %d\n", processColors.size()));
        int contextSwitches = (int) cpuData.stream().filter(entry -> entry.contextSwitch).count();
        sb.append(String.format("Cambios de contexto: %d\n", contextSwitches));
        sb.append("\nSecuencia de ejecución:\n");
        
        for (GanttEntry entry : cpuData) {
            String label = entry.contextSwitch ? "[CS]" : entry.pid;
            sb.append(String.format("  %s: [%d-%d] (%d unidades)\n",
                label, entry.startTime, entry.endTime,
                entry.endTime - entry.startTime));
        }

        if (!ioData.isEmpty()) {
            sb.append("\nRáfagas de E/S:\n");
            for (GanttEntry entry : ioData) {
                sb.append(String.format("  %s: [%d-%d] (%d unidades)\n",
                    entry.pid, entry.startTime, entry.endTime,
                    entry.endTime - entry.startTime));
            }
        }
        
        return sb.toString();
    }

    /**
     * Clase interna para representar una entrada del diagrama de Gantt
     */
    private static class GanttEntry {
        final String pid;
        final int startTime;
        final int endTime;
        final boolean contextSwitch;

        GanttEntry(String pid, int startTime, int endTime, boolean contextSwitch) {
            this.pid = pid;
            this.startTime = startTime;
            this.endTime = endTime;
            this.contextSwitch = contextSwitch;
        }
    }

    // Método main para prueba standalone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prueba Diagrama de Gantt");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 400);
            
            GanttPanel gantt = new GanttPanel();
            
            // Datos de prueba
            gantt.addEntry("P1", 0, 5);
            gantt.addEntry("P2", 5, 8);
            gantt.addEntry("P1", 8, 13);
            gantt.addEntry("P3", 13, 21);
            gantt.addEntry("P2", 21, 24);
            
            frame.add(new JScrollPane(gantt));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Imprimir resumen después de 1 segundo
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
                System.out.println(gantt.getExecutionSummary());
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}
