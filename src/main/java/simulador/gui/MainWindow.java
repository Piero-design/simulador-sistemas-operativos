package simulador.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del simulador
 * Integra todos los paneles de visualización
 */
public class MainWindow extends JFrame {
    
    private GanttPanel ganttPanel;
    private MemoryPanel memoryPanel;
    private ProcessQueuesPanel queuesPanel;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JButton loadButton;
    private JButton startButton;
    private JButton pauseButton;
    private JButton clearButton;
    private Runnable onStartCallback;
    private Runnable onPauseCallback;
    private boolean simulationStarted = false;
    
    public MainWindow() {
        setTitle("Simulador de Sistema Operativo - Gestión de Procesos y Memoria");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        // Panel superior - Gantt
        ganttPanel = new GanttPanel();
        JScrollPane ganttScroll = new JScrollPane(ganttPanel);
        ganttScroll.setBorder(BorderFactory.createTitledBorder("Planificación de CPU"));
        ganttScroll.setPreferredSize(new Dimension(800, 200));
        
        // Panel central - Dividido en dos
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Izquierda - Colas de procesos
        queuesPanel = new ProcessQueuesPanel();
        JScrollPane queuesScroll = new JScrollPane(queuesPanel);
        queuesScroll.setBorder(BorderFactory.createTitledBorder("Colas de Procesos"));
        centerSplit.setLeftComponent(queuesScroll);
        
        // Derecha - Memoria
        memoryPanel = new MemoryPanel();
        JScrollPane memoryScroll = new JScrollPane(memoryPanel);
        memoryScroll.setBorder(BorderFactory.createTitledBorder("Gestión de Memoria"));
        centerSplit.setRightComponent(memoryScroll);
        
        centerSplit.setDividerLocation(400);
        
        // Panel inferior - Log
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Registro de Eventos"));
        logScroll.setPreferredSize(new Dimension(800, 200));
        
        // Panel de estado
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Simulador iniciado. Cargue un archivo de procesos para comenzar.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Botones de control
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loadButton = new JButton("Cargar Procesos");
        startButton = new JButton("Iniciar Simulación");
        pauseButton = new JButton("Pausar");
        clearButton = new JButton("Limpiar");
        
        // Configurar estado inicial
        pauseButton.setEnabled(false);
        
        // Conectar eventos
        loadButton.addActionListener(e -> onLoadProcesses());
        startButton.addActionListener(e -> onStartSimulation());
        pauseButton.addActionListener(e -> onPauseSimulation());
        clearButton.addActionListener(e -> onClearAll());
        
        buttonPanel.add(loadButton);
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(clearButton);
        statusPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Layout principal
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(ganttScroll, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerSplit, BorderLayout.CENTER);
        add(logScroll, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    // ===== Métodos públicos para actualizar la UI =====
    
    public void addGanttEntry(String pid, int startTime, int duration) {
        ganttPanel.addEntry(pid, startTime, duration);
    }
    
    public void updateMemory(simulador.memory.MemoryManager manager) {
        memoryPanel.setMemoryManager(manager);
    }
    
    public void updateQueues(java.util.List<simulador.process.Process> ready, 
                             java.util.List<simulador.process.Process> blocked, 
                             simulador.process.Process running,
                             String schedulerName) {
        queuesPanel.updateQueues(ready, blocked, running, schedulerName);
    }
    
    public void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    public void clearAll() {
        ganttPanel.clear();
        logArea.setText("");
    }
    
    // ===== Métodos de control =====
    
    private void onLoadProcesses() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("Seleccionar archivo de procesos");
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getName();
            appendLog("Archivo seleccionado: " + filename);
            setStatus("Archivo cargado: " + filename + ". Presione 'Iniciar Simulación' para comenzar.");
        }
    }
    
    private void onStartSimulation() {
        if (!simulationStarted) {
            simulationStarted = true;
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            loadButton.setEnabled(false);
            
            if (onStartCallback != null) {
                onStartCallback.run();
            }
            
            setStatus("Simulación en ejecución...");
        }
    }
    
    private void onPauseSimulation() {
        if (onPauseCallback != null) {
            onPauseCallback.run();
        }
        setStatus("Simulación pausada");
    }
    
    private void onClearAll() {
        clearAll();
        simulationStarted = false;
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        loadButton.setEnabled(true);
        setStatus("Simulador reiniciado. Cargue un archivo de procesos para comenzar.");
    }
    
    public void setOnStartCallback(Runnable callback) {
        this.onStartCallback = callback;
    }
    
    public void setOnPauseCallback(Runnable callback) {
        this.onPauseCallback = callback;
    }
    
    public void simulationCompleted() {
        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        loadButton.setEnabled(true);
        clearButton.setEnabled(true);
        setStatus("Simulación completada. Ver log para métricas detalladas.");
    }
    
    public GanttPanel getGanttPanel() { return ganttPanel; }
    public MemoryPanel getMemoryPanel() { return memoryPanel; }
    public ProcessQueuesPanel getQueuesPanel() { return queuesPanel; }
}
