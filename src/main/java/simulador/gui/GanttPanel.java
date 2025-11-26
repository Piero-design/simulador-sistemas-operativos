package simulador.gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel para mostrar el diagrama de Gantt
 */
public class GanttPanel extends JPanel {
    
    private List<GanttEntry> entries = new ArrayList<>();
    private int maxTime = 100;
    private final int CELL_WIDTH = 30;
    private final int ROW_HEIGHT = 40;
    private final Map<String, Color> processColors = new HashMap<>();
    private final Random random = new Random(42);
    
    public GanttPanel() {
        setPreferredSize(new Dimension(800, 300));
        setBackground(Color.WHITE);
    }
    
    public void addEntry(String pid, int startTime, int duration) {
        entries.add(new GanttEntry(pid, startTime, duration));
        if (startTime + duration > maxTime) {
            maxTime = startTime + duration + 10;
        }
        assignColorIfNeeded(pid);
        repaint();
    }
    
    public void clear() {
        entries.clear();
        maxTime = 100;
        repaint();
    }
    
    private void assignColorIfNeeded(String pid) {
        if (!processColors.containsKey(pid)) {
            Color color = new Color(
                random.nextInt(200) + 55,
                random.nextInt(200) + 55,
                random.nextInt(200) + 55
            );
            processColors.put(pid, color);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int marginTop = 50;
        int marginLeft = 80;
        
        // Título
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Diagrama de Gantt", marginLeft, 25);
        
        // Dibujar línea de tiempo
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int t = 0; t <= maxTime; t += 5) {
            int x = marginLeft + t * CELL_WIDTH / 5;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawLine(x, marginTop - 10, x, marginTop + entries.size() * ROW_HEIGHT);
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(t), x - 5, marginTop - 15);
        }
        
        // Dibujar entradas
        for (GanttEntry entry : entries) {
            int x = marginLeft + entry.startTime * CELL_WIDTH / 5;
            int y = marginTop;
            int width = entry.duration * CELL_WIDTH / 5;
            
            Color color = processColors.get(entry.pid);
            g2d.setColor(color);
            g2d.fillRect(x, y, width, ROW_HEIGHT - 5);
            
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, ROW_HEIGHT - 5);
            
            // Texto del proceso
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String text = entry.pid;
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (width - fm.stringWidth(text)) / 2;
            int textY = y + (ROW_HEIGHT - 5 + fm.getHeight()) / 2 - 3;
            g2d.drawString(text, textX, textY);
        }
    }
    
    private static class GanttEntry {
        String pid;
        int startTime;
        int duration;
        
        GanttEntry(String pid, int startTime, int duration) {
            this.pid = pid;
            this.startTime = startTime;
            this.duration = duration;
        }
    }
}
