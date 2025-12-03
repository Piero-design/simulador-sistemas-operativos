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
    
    private List<GanttEntry> ganttData;
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
        ganttData = new ArrayList<>();
        processColors = new HashMap<>();
        maxTime = 0;
        
        setPreferredSize(new Dimension(800, 300));
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
        addEntry(pid, startTime, endTime, false);
    }

    private void addEntry(String pid, int startTime, int endTime, boolean contextSwitch) {
        if (endTime <= startTime) {
            return;
        }

        GanttEntry entry = new GanttEntry(pid, startTime, endTime, contextSwitch);
        ganttData.add(entry);

        if (!contextSwitch && !processColors.containsKey(pid)) {
            processColors.put(pid, COLORS[colorIndex % COLORS.length]);
            colorIndex++;
        }

        maxTime = Math.max(maxTime, endTime);
        repaint();
    }

    public void addContextSwitch(int startTime, int endTime) {
        addEntry("CS", startTime, endTime, true);
    }

    /**
     * Limpia el diagrama
     */
    public void clear() {
        ganttData.clear();
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

        if (ganttData.isEmpty()) {
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

        // Dibujar eje de tiempo
        drawTimeAxis(g2d);
        
        // Dibujar barras del diagrama
        drawGanttBars(g2d);
        
        // Dibujar leyenda
        drawLegend(g2d);
    }

    private void drawTimeAxis(Graphics2D g2d) {
        int y = TOP_MARGIN + PROCESS_HEIGHT + 10;
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

    private void drawGanttBars(Graphics2D g2d) {
        int y = TOP_MARGIN;
        
        // Etiqueta "Procesos"
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("CPU", 10, y + PROCESS_HEIGHT / 2 + 5);
        
        // Dibujar cada entrada
        for (GanttEntry entry : ganttData) {
            int startX = LEFT_MARGIN + entry.startTime * pixelsPerUnit;
            int width = (entry.endTime - entry.startTime) * pixelsPerUnit;
            
            if (startX > getWidth() - 30) break;
            if (startX + width > getWidth() - 30) {
                width = getWidth() - 30 - startX;
            }
            
            // Rectángulo de proceso
            Color color = entry.contextSwitch ? CONTEXT_SWITCH_COLOR : processColors.get(entry.pid);
            if (color == null) {
                color = CONTEXT_SWITCH_COLOR;
            }
            g2d.setColor(color);
            g2d.fillRect(startX, y, width, PROCESS_HEIGHT);
            
            // Borde
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(startX, y, width, PROCESS_HEIGHT);
            
            // Texto del PID
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            String label = entry.contextSwitch ? "CS" : entry.pid;
            int textX = startX + (width - fm.stringWidth(label)) / 2;
            int textY = y + (PROCESS_HEIGHT + fm.getAscent()) / 2 - 2;
            
            // Sombra del texto
            g2d.setColor(Color.BLACK);
            g2d.drawString(label, textX + 1, textY + 1);

            // Texto principal
            g2d.setColor(entry.contextSwitch ? Color.BLACK : Color.WHITE);
            g2d.drawString(label, textX, textY);
        }
    }

    private void drawLegend(Graphics2D g2d) {
        int legendY = TOP_MARGIN + PROCESS_HEIGHT + 70;
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

        boolean hasContextSwitch = ganttData.stream().anyMatch(e -> e.contextSwitch);
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
        if (ganttData.isEmpty()) {
            return "Sin datos de ejecución";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE EJECUCIÓN ===\n");
        sb.append(String.format("Tiempo total: %d unidades\n", maxTime));
        sb.append(String.format("Procesos ejecutados: %d\n", processColors.size()));
        int contextSwitches = (int) ganttData.stream().filter(entry -> entry.contextSwitch).count();
        sb.append(String.format("Cambios de contexto: %d\n", contextSwitches));
        sb.append("\nSecuencia de ejecución:\n");
        
        for (GanttEntry entry : ganttData) {
            String label = entry.contextSwitch ? "[CS]" : entry.pid;
            sb.append(String.format("  %s: [%d-%d] (%d unidades)\n",
                label, entry.startTime, entry.endTime,
                entry.endTime - entry.startTime));
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
