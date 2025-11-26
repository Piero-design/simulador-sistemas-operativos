package simulador.gui;

import simulador.memory.MemoryManager;
import javax.swing.*;
import java.awt.*;

/**
 * Panel para mostrar el estado de la memoria
 */
public class MemoryPanel extends JPanel {
    
    private MemoryManager memoryManager;
    private final int FRAME_SIZE = 40;
    private final int COLS = 8;
    
    public MemoryPanel() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);
    }
    
    public void setMemoryManager(MemoryManager manager) {
        this.memoryManager = manager;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (memoryManager == null) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("Sin gestor de memoria", 20, 50);
            return;
        }
        
        // Título
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Estado de Memoria", 20, 25);
        
        // Estadísticas
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        int y = 50;
        g2d.drawString("Total de marcos: " + memoryManager.getTotalFrames(), 20, y);
        y += 20;
        g2d.drawString("Marcos libres: " + memoryManager.getFreeFrames(), 20, y);
        y += 20;
        g2d.drawString("Fallos de página: " + memoryManager.getPageFaults(), 20, y);
        y += 20;
        g2d.drawString("Reemplazos: " + memoryManager.getPageReplacements(), 20, y);
        y += 30;
        
        // Dibujar marcos
        MemoryManager.Frame[] frames = memoryManager.getFrames();
        int startY = y;
        
        for (int i = 0; i < frames.length; i++) {
            int row = i / COLS;
            int col = i % COLS;
            int x = 20 + col * (FRAME_SIZE + 5);
            y = startY + row * (FRAME_SIZE + 5);
            
            MemoryManager.Frame frame = frames[i];
            
            // Color según estado
            if (frame.isOccupied()) {
                g2d.setColor(new Color(255, 100, 100)); // Rojo claro
            } else {
                g2d.setColor(new Color(100, 255, 100)); // Verde claro
            }
            
            g2d.fillRect(x, y, FRAME_SIZE, FRAME_SIZE);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, FRAME_SIZE, FRAME_SIZE);
            
            // Texto
            g2d.setFont(new Font("Arial", Font.PLAIN, 9));
            String text = String.valueOf(i);
            if (frame.isOccupied()) {
                text = frame.getOwnerPid() + "\nP" + frame.getPageNumber();
            }
            
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (FRAME_SIZE - fm.stringWidth(String.valueOf(i))) / 2;
            int textY = y + (FRAME_SIZE + fm.getHeight()) / 2 - 2;
            
            g2d.drawString(String.valueOf(i), textX, textY);
            
            if (frame.isOccupied()) {
                textY += 12;
                String pid = frame.getOwnerPid();
                textX = x + (FRAME_SIZE - fm.stringWidth(pid)) / 2;
                g2d.drawString(pid, textX, textY);
            }
        }
        
        // Leyenda
        y = startY + ((frames.length / COLS) + 2) * (FRAME_SIZE + 5);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        
        g2d.setColor(new Color(100, 255, 100));
        g2d.fillRect(20, y, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(20, y, 20, 20);
        g2d.drawString("Libre", 45, y + 15);
        
        g2d.setColor(new Color(255, 100, 100));
        g2d.fillRect(120, y, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(120, y, 20, 20);
        g2d.drawString("Ocupado", 145, y + 15);
    }
}
