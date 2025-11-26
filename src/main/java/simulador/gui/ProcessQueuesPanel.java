package simulador.gui;

import simulador.process.Process;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel para mostrar las colas de procesos
 */
public class ProcessQueuesPanel extends JPanel {
    
    private List<Process> readyQueue = new ArrayList<>();
    private List<Process> blockedQueue = new ArrayList<>();
    private Process runningProcess = null;
    private String schedulerName = "";
    
    public ProcessQueuesPanel() {
        setPreferredSize(new Dimension(350, 500));
        setBackground(Color.WHITE);
    }
    
    public void updateQueues(List<Process> ready, List<Process> blocked, Process running, String scheduler) {
        this.readyQueue = new ArrayList<>(ready);
        this.blockedQueue = new ArrayList<>(blocked);
        this.runningProcess = running;
        this.schedulerName = scheduler;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int y = 20;
        int x = 20;
        
        // Título
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Estado de Procesos", x, y);
        y += 25;
        
        // Algoritmo activo
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Algoritmo: " + schedulerName, x, y);
        y += 30;
        
        // Proceso en ejecución
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(0, 128, 0));
        g2d.drawString("▶ EJECUTANDO", x, y);
        y += 5;
        
        g2d.setColor(new Color(200, 255, 200));
        g2d.fillRect(x, y, 310, 50);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, 310, 50);
        
        if (runningProcess != null) {
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            g2d.drawString(runningProcess.getPid(), x + 10, y + 25);
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.drawString("Prioridad: " + runningProcess.getPriority(), x + 80, y + 25);
            g2d.drawString("Páginas: " + runningProcess.getPages(), x + 180, y + 25);
        } else {
            g2d.setFont(new Font("Arial", Font.ITALIC, 12));
            g2d.setColor(Color.GRAY);
            g2d.drawString("(vacío)", x + 130, y + 30);
        }
        
        y += 70;
        
        // Cola de listos
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(0, 0, 128));
        g2d.drawString("⏸ LISTOS (" + readyQueue.size() + ")", x, y);
        y += 5;
        
        if (readyQueue.isEmpty()) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(x, y, 310, 40);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, 310, 40);
            g2d.setFont(new Font("Arial", Font.ITALIC, 11));
            g2d.setColor(Color.GRAY);
            g2d.drawString("(cola vacía)", x + 120, y + 25);
            y += 45;
        } else {
            for (Process p : readyQueue) {
                g2d.setColor(new Color(200, 220, 255));
                g2d.fillRect(x, y, 310, 35);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, 310, 35);
                
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString(p.getPid(), x + 10, y + 22);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString("Prioridad: " + p.getPriority(), x + 80, y + 22);
                g2d.drawString("Páginas: " + p.getPages(), x + 180, y + 22);
                
                y += 40;
            }
        }
        
        y += 15;
        
        // Cola de bloqueados
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(128, 0, 0));
        g2d.drawString("⏹ BLOQUEADOS (" + blockedQueue.size() + ")", x, y);
        y += 5;
        
        if (blockedQueue.isEmpty()) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(x, y, 310, 40);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, 310, 40);
            g2d.setFont(new Font("Arial", Font.ITALIC, 11));
            g2d.setColor(Color.GRAY);
            g2d.drawString("(cola vacía)", x + 120, y + 25);
        } else {
            for (Process p : blockedQueue) {
                g2d.setColor(new Color(255, 220, 220));
                g2d.fillRect(x, y, 310, 35);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, 310, 35);
                
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString(p.getPid(), x + 10, y + 22);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                
                String reason = "";
                if (p.getState() == Process.State.BLOCKED_MEM) {
                    reason = "Memoria";
                } else if (p.getState() == Process.State.BLOCKED_IO) {
                    reason = "E/S";
                }
                g2d.drawString("Razón: " + reason, x + 80, y + 22);
                
                y += 40;
            }
        }
    }
}
