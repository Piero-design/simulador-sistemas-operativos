package simulador.gui;

import javax.swing.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Simulador de Sistema Operativo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Simulador iniciado. Aquí irá la interfaz gráfica.", SwingConstants.CENTER);
        add(label);

        setVisible(true);
    }
}
