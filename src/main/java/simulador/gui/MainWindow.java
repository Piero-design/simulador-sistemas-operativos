package simulador.gui;

import simulador.core.Simulator;
import simulador.memory.*;
import simulador.metrics.MetricsCollector;
import simulador.process.Process;
import simulador.scheduler.*;
import simulador.utils.FileParser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame implements Simulator.SimulationListener {

    private Simulator simulator;
    private GanttPanel ganttPanel;  // ¡Panel del diagrama de Gantt!
    private JTextArea logArea;
    private JTextArea metricsArea;
    private JTable processTable;
    private DefaultTableModel processTableModel;
    private JTable memoryTable;
    private DefaultTableModel memoryTableModel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel timeLabel;
    
    // Configuración
    private JComboBox<String> schedulerCombo;
    private JComboBox<String> memoryAlgoCombo;
    private JSpinner framesSpinner;
    private JSpinner quantumSpinner;
    private JSpinner contextSwitchSpinner;
    private JTextField filePathField;
    
    private JButton loadButton;
    private JButton startButton;
    private JButton stopButton;
    private JButton resetButton;
    
    // Variables para tracking del Gantt
    private String lastRunningProcess = null;
    private int lastExecutionTime = 0;
    private final Map<String, Integer> ioActiveBursts = new HashMap<>();

    public MainWindow() {
        setTitle("Simulador de Sistema Operativo - Planificación y Memoria Virtual");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        
        setVisible(true);
    }

    private void initComponents() {
        // Panel de configuración
        schedulerCombo = new JComboBox<>(new String[]{"FCFS", "SJF", "Round Robin"});
        memoryAlgoCombo = new JComboBox<>(new String[]{"FIFO", "LRU", "Optimal"});
        framesSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        quantumSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        contextSwitchSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        filePathField = new JTextField("procesos.txt");
        
        // Botones
        loadButton = new JButton("Cargar Procesos");
        startButton = new JButton("Iniciar Simulación");
        stopButton = new JButton("Detener");
        resetButton = new JButton("Reiniciar");
        
        loadButton.addActionListener(e -> loadProcesses());
        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());
        resetButton.addActionListener(e -> resetSimulation());
        
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        
        // ¡PANEL DE GANTT!
        ganttPanel = new GanttPanel();
        
        // Área de log
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // Área de métricas
        metricsArea = new JTextArea(10, 30);
        metricsArea.setEditable(false);
        metricsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // Tabla de procesos
        String[] processColumns = {"PID", "Llegada", "Estado", "Prioridad", "Páginas"};
        processTableModel = new DefaultTableModel(processColumns, 0);
        processTable = new JTable(processTableModel);
        
        // Tabla de memoria
        String[] memoryColumns = {"Marco", "Ocupado", "PID", "Página"};
        memoryTableModel = new DefaultTableModel(memoryColumns, 0);
        memoryTable = new JTable(memoryTableModel);
        
        // Barra de progreso y estado
        progressBar = new JProgressBar();
        statusLabel = new JLabel("Listo para cargar procesos");
        timeLabel = new JLabel("Tiempo: 0");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior - Configuración
        JPanel configPanel = createConfigPanel();
        add(configPanel, BorderLayout.NORTH);
        
        // Panel central - Visualización con Gantt arriba
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // ¡GANTT EN LA PARTE SUPERIOR!
        JPanel ganttContainer = new JPanel(new BorderLayout());
        ganttContainer.add(new JScrollPane(ganttPanel), BorderLayout.CENTER);
        centerPanel.add(ganttContainer, BorderLayout.NORTH);
        
        // Grid con el resto de los paneles
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        
        // Panel de procesos
        JPanel processPanel = new JPanel(new BorderLayout());
        processPanel.setBorder(new TitledBorder("Cola de Procesos"));
        processPanel.add(new JScrollPane(processTable), BorderLayout.CENTER);
        gridPanel.add(processPanel);
        
        // Panel de memoria
        JPanel memoryPanel = new JPanel(new BorderLayout());
        memoryPanel.setBorder(new TitledBorder("Estado de Memoria"));
        memoryPanel.add(new JScrollPane(memoryTable), BorderLayout.CENTER);
        gridPanel.add(memoryPanel);
        
        // Panel de log
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new TitledBorder("Log de Eventos"));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        gridPanel.add(logPanel);
        
        // Panel de métricas
        JPanel metricsPanel = new JPanel(new BorderLayout());
        metricsPanel.setBorder(new TitledBorder("Métricas de Desempeño"));
        metricsPanel.add(new JScrollPane(metricsArea), BorderLayout.CENTER);
        gridPanel.add(metricsPanel);
        
        centerPanel.add(gridPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior - Estado
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(timeLabel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Configuración del Simulador"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fila 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Archivo de Procesos:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(filePathField, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton browseButton = new JButton("...");
        browseButton.addActionListener(e -> browseFile());
        panel.add(browseButton, gbc);
        gbc.gridx = 3;
        panel.add(loadButton, gbc);
        
        // Fila 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Algoritmo de Planificación:"), gbc);
        gbc.gridx = 1;
        panel.add(schedulerCombo, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Quantum:"), gbc);
        gbc.gridx = 3;
        panel.add(quantumSpinner, gbc);
        
        // Fila 3
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Algoritmo de Memoria:"), gbc);
        gbc.gridx = 1;
        panel.add(memoryAlgoCombo, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Marcos de Memoria:"), gbc);
        gbc.gridx = 3;
        panel.add(framesSpinner, gbc);
        
        // Fila 4 - Costo de cambio de contexto
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Costo cambio de contexto:"), gbc);
        gbc.gridx = 1;
        panel.add(contextSwitchSpinner, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(unidades)"), gbc);

        // Fila 4 - Botones de control
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }

    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void loadProcesses() {
        try {
            String filePath = filePathField.getText();
            
            // Crear scheduler
            CPUScheduler scheduler = createScheduler();
            
            // Crear algoritmo de reemplazo de páginas
            PageReplacement pageAlgo = createPageReplacementAlgorithm();
            
            // Crear gestor de memoria
            int frames = (Integer) framesSpinner.getValue();
            MemoryManager memoryManager = new MemoryManager(frames, pageAlgo);
            int contextSwitchCost = (Integer) contextSwitchSpinner.getValue();
            
            // Crear simulador
            simulator = new Simulator(scheduler, memoryManager, contextSwitchCost);
            simulator.addListener(this);
            
            // Cargar procesos
            simulator.loadProcessesFromFile(filePath);

            ganttPanel.clear();
            ioActiveBursts.clear();
            lastRunningProcess = null;
            lastExecutionTime = 0;
            metricsArea.setText("");
            progressBar.setValue(0);
            timeLabel.setText("Tiempo: 0");
            
            log("Procesos cargados exitosamente desde: " + filePath);
            log("Configuración: " + scheduler.getName() + " + " + pageAlgo.getName());
            log("Marcos de memoria: " + frames);
            log("Costo de cambio de contexto: " + contextSwitchCost + " unidades");
            
            startButton.setEnabled(true);
            loadButton.setEnabled(false);
            statusLabel.setText("Procesos cargados - Listo para iniciar");
            
            updateProcessTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar procesos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private CPUScheduler createScheduler() {
        String selected = (String) schedulerCombo.getSelectedItem();
        switch (selected) {
            case "SJF":
                return new SJF();
            case "Round Robin":
                int quantum = (Integer) quantumSpinner.getValue();
                return new RoundRobin(quantum);
            case "FCFS":
            default:
                return new FCFS();
        }
    }

    private PageReplacement createPageReplacementAlgorithm() {
        String selected = (String) memoryAlgoCombo.getSelectedItem();
        switch (selected) {
            case "LRU":
                return new LRU();
            case "Optimal":
                return new Optimal();
            case "FIFO":
            default:
                return new FIFO();
        }
    }

    private void startSimulation() {
        if (simulator != null) {
            simulator.start();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            loadButton.setEnabled(false);
            progressBar.setIndeterminate(true);
            log("=== SIMULACIÓN INICIADA ===");
        }
    }

    private void stopSimulation() {
        if (simulator != null) {
            simulator.stop();
            stopButton.setEnabled(false);
            resetButton.setEnabled(true);
            progressBar.setIndeterminate(false);
            log("=== SIMULACIÓN DETENIDA ===");
        }
    }

    private void resetSimulation() {
        simulator = null;
        processTableModel.setRowCount(0);
        memoryTableModel.setRowCount(0);
        logArea.setText("");
        metricsArea.setText("");
        ganttPanel.clear();  // ¡Limpiar el Gantt!
        progressBar.setValue(0);
        timeLabel.setText("Tiempo: 0");
        statusLabel.setText("Listo para cargar procesos");
        
        // Resetear tracking del Gantt
        lastRunningProcess = null;
        lastExecutionTime = 0;
        ioActiveBursts.clear();
        
        loadButton.setEnabled(true);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        resetButton.setEnabled(false);
        
        log("Sistema reiniciado");
    }

    private void updateProcessTable() {
        processTableModel.setRowCount(0);
        if (simulator != null) {
            for (Process p : simulator.getProcesses()) {
                processTableModel.addRow(new Object[]{
                        p.getPid(),
                        p.getArrivalTime(),
                        p.getState(),
                        p.getPriority(),
                        p.getPages()
                });
            }
        }
    }

    private void updateMemoryTable() {
        memoryTableModel.setRowCount(0);
        if (simulator != null && simulator.getMemoryManager() != null) {
            boolean[] frames = simulator.getMemoryManager().getFrameOccupied();
            Map<Integer, MemoryManager.FrameAllocation> allocations = simulator.getMemoryManager().getFrameAllocations();
            for (int i = 0; i < frames.length; i++) {
                MemoryManager.FrameAllocation allocation = allocations.get(i);
                memoryTableModel.addRow(new Object[]{
                        i,
                        frames[i] ? "Sí" : "No",
                        allocation != null ? allocation.getPid() : (frames[i] ? "?" : "-"),
                        allocation != null ? allocation.getPageNumber() : (frames[i] ? "?" : "-")
                });
            }
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // Implementación de SimulationListener
    @Override
    public void onSimulationStarted() {
        log("Simulación iniciada");
        statusLabel.setText("Simulación en curso...");
    }

    @Override
    public void onSimulationFinished(MetricsCollector metrics) {
        log("=== SIMULACIÓN FINALIZADA ===");
        statusLabel.setText("Simulación completada");
        progressBar.setIndeterminate(false);
        progressBar.setValue(100);
        
        // Cerrar última entrada del Gantt si quedó abierta
        if (lastRunningProcess != null && simulator != null) {
            int currentTime = simulator.getCurrentTime();
            ganttPanel.addEntry(lastRunningProcess, lastExecutionTime, currentTime);
            lastRunningProcess = null;
        }

        if (!ioActiveBursts.isEmpty() && simulator != null) {
            int currentTime = simulator.getCurrentTime();
            Map<String, Integer> snapshot = new HashMap<>(ioActiveBursts);
            ioActiveBursts.clear();
            for (Map.Entry<String, Integer> entry : snapshot.entrySet()) {
                ganttPanel.addIoEntry(entry.getKey(), entry.getValue(), currentTime);
            }
        }
        
        // Mostrar métricas
        String report = metrics.generateReport();
        metricsArea.setText(report);
        log("\n" + report);
        log("\n" + ganttPanel.getExecutionSummary());  // ¡Agregar resumen del Gantt!
        
        SwingUtilities.invokeLater(() -> {
            updateProcessTable();
            updateMemoryTable();
        });

        stopButton.setEnabled(false);
        resetButton.setEnabled(true);
    }

    @Override
    public void onProcessesLoaded(int count) {
        log("Cargados " + count + " procesos");
    }

    @Override
    public void onProcessArrived(Process process) {
        log("Proceso " + process.getPid() + " llegó al sistema");
        SwingUtilities.invokeLater(this::updateProcessTable);
    }

    @Override
    public void onProcessStarted(Process process) {
        log("Proceso " + process.getPid() + " iniciado");
        SwingUtilities.invokeLater(this::updateProcessTable);
    }

    @Override
    public void onProcessExecStart(Process process, int time) {
        // Start a new Gantt entry for this process at simulator time `time`
        SwingUtilities.invokeLater(() -> {
            // Close previous if somehow left open
            if (lastRunningProcess != null && !lastRunningProcess.equals(process.getPid())) {
                ganttPanel.addEntry(lastRunningProcess, lastExecutionTime, time);
            }
            lastRunningProcess = process.getPid();
            lastExecutionTime = time;
        });
    }

    @Override
    public void onProcessExecEnd(Process process, int time) {
        SwingUtilities.invokeLater(() -> {
            if (lastRunningProcess != null && lastRunningProcess.equals(process.getPid())) {
                ganttPanel.addEntry(lastRunningProcess, lastExecutionTime, time);
                lastRunningProcess = null;
            }
        });
    }

    @Override
    public void onContextSwitch(int startTime, int endTime) {
        SwingUtilities.invokeLater(() -> ganttPanel.addContextSwitch(startTime, endTime));
    }

    @Override
    public void onIOCompleted(Process process) {
        log("Proceso " + process.getPid() + " completó E/S");
    }

    @Override
    public void onIOStarted(Process process, int startTime, int duration) {
        log("Proceso " + process.getPid() + " inició E/S (" + duration + " unidades)");
        SwingUtilities.invokeLater(() -> {
            ioActiveBursts.put(process.getPid(), startTime);
            updateProcessTable();
        });
    }

    @Override
    public void onIOCompleted(Process process, int time) {
        SwingUtilities.invokeLater(() -> {
            Integer start = ioActiveBursts.remove(process.getPid());
            if (start != null) {
                ganttPanel.addIoEntry(process.getPid(), start, time);
            }
            updateProcessTable();
            updateMemoryTable();
        });
    }

    @Override
    public void onTimeAdvanced(int time) {
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText("Tiempo: " + time);
            updateProcessTable();
            updateMemoryTable();
            // Gantt updated via explicit exec start/end notifications
        });
    }
    
    /**
     * Actualiza el diagrama de Gantt detectando cambios en el proceso en ejecución
     */
    // Gantt ahora se actualiza mediante notificaciones explícitas onProcessExecStart/onProcessExecEnd
    @SuppressWarnings("unused")
    private void updateGanttChart(int currentTime) {
        // kept for backward-compatibility but no-op because Gantt entries are driven by Simulator events
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
