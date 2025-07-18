package org.example.GUI;

import org.example.BusinessLogic.SelectionPolicy;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {
    private JTextField clientsField;
    private JTextField queuesField;
    private JTextField timeLimitField;
    private JTextField arrivalMinField;
    private JTextField arrivalMaxField;
    private JTextField serviceMinField;
    private JTextField serviceMaxField;
    private JButton startButton;
    private JTextArea displayArea;
    JComboBox<SelectionPolicy> strategyCombo = new JComboBox<>(SelectionPolicy.values());

    //Constructor
    public SimulationFrame() {
        setTitle("Queue Simulation");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Culori
        Color panelColor      = new Color(204, 227, 222);
        Color textColor       = new Color(47, 79, 79);
        Color buttonColor     = new Color(164, 195, 178);

        //Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        inputPanel.setBackground(panelColor);

        //Labels
        JLabel clientsLabel     = new JLabel("Number of Clients:");
        clientsLabel.setForeground(textColor);

        JLabel queuesLabel      = new JLabel("Number of Queues:");
        queuesLabel.setForeground(textColor);

        JLabel timeLimitLabel   = new JLabel("Simulation Time Limit:");
        timeLimitLabel.setForeground(textColor);

        JLabel arrivalMinLabel  = new JLabel("Arrival Time Min:");
        arrivalMinLabel.setForeground(textColor);

        JLabel arrivalMaxLabel  = new JLabel("Arrival Time Max:");
        arrivalMaxLabel.setForeground(textColor);

        JLabel serviceMinLabel  = new JLabel("Service Time Min:");
        serviceMinLabel.setForeground(textColor);

        JLabel serviceMaxLabel  = new JLabel("Service Time Max:");
        serviceMaxLabel.setForeground(textColor);

        JLabel strategyLabel = new JLabel("Strategy:");
        strategyLabel.setForeground(textColor);

        //Casete de input + formatate cu culori
        clientsField = new JTextField();
        clientsField.setBackground(Color.WHITE);
        clientsField.setForeground(textColor);

        queuesField = new JTextField();
        queuesField.setBackground(Color.WHITE);
        queuesField.setForeground(textColor);

        timeLimitField = new JTextField();
        timeLimitField.setBackground(Color.WHITE);
        timeLimitField.setForeground(textColor);

        arrivalMinField = new JTextField();
        arrivalMinField.setBackground(Color.WHITE);
        arrivalMinField.setForeground(textColor);

        arrivalMaxField = new JTextField();
        arrivalMaxField.setBackground(Color.WHITE);
        arrivalMaxField.setForeground(textColor);

        serviceMinField = new JTextField();
        serviceMinField.setBackground(Color.WHITE);
        serviceMinField.setForeground(textColor);

        serviceMaxField = new JTextField();
        serviceMaxField.setBackground(Color.WHITE);
        serviceMaxField.setForeground(textColor);

        strategyCombo.setBackground(Color.WHITE);
        strategyCombo.setForeground(textColor);

        //Adaug toate label-urile si casetele de input
        inputPanel.add(clientsLabel);
        inputPanel.add(clientsField);
        inputPanel.add(queuesLabel);
        inputPanel.add(queuesField);
        inputPanel.add(timeLimitLabel);
        inputPanel.add(timeLimitField);
        inputPanel.add(arrivalMinLabel);
        inputPanel.add(arrivalMinField);
        inputPanel.add(arrivalMaxLabel);
        inputPanel.add(arrivalMaxField);
        inputPanel.add(serviceMinLabel);
        inputPanel.add(serviceMinField);
        inputPanel.add(serviceMaxLabel);
        inputPanel.add(serviceMaxField);
        inputPanel.add(strategyLabel);
        inputPanel.add(strategyCombo);

        //Buton de start
        startButton = new JButton("Start Simulation");
        startButton.setBackground(buttonColor);
        startButton.setForeground(textColor);
        inputPanel.add(startButton);

        add(inputPanel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setBackground(Color.WHITE);
        displayArea.setForeground(textColor);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    //Functii pentru a putea lua datele din casetele de input
    public JButton getStartButton() {
        return startButton;
    }

    public int getNumClients() throws NumberFormatException {
        return parseIntWithValidation(clientsField);
    }

    public int getNumQueues() throws NumberFormatException {
        return parseIntWithValidation(queuesField);
    }

    public int getTimeLimit() throws NumberFormatException {
        return parseIntWithValidation(timeLimitField);
    }

    public int getArrivalMin() throws NumberFormatException {
        return parseIntWithValidation(arrivalMinField);
    }

    public int getArrivalMax() throws NumberFormatException {
        return parseIntWithValidation(arrivalMaxField);
    }

    public int getServiceMin() throws NumberFormatException {
        return parseIntWithValidation(serviceMinField);
    }

    public int getServiceMax() throws NumberFormatException {
        return parseIntWithValidation(serviceMaxField);
    }

    public SelectionPolicy getSelectedStrategy() {
        return (SelectionPolicy) strategyCombo.getSelectedItem();
    }

    //Validare a corectitudinii datelor din punct de vedere al formatului
    private int parseIntWithValidation(JTextField field) throws NumberFormatException {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Every value you introduce must be an integer");
        }
    }

    //Functie care face posibil update-ul display-ului pentru a vedea in timp real rezultatul simularii
    public void appendText(String text) {
        SwingUtilities.invokeLater(() -> displayArea.append(text + "\n"));
    }

    //Functie pentru a curata display-ul cand incercam sa pornim o simulare noua
    public void clearDisplay() {
        SwingUtilities.invokeLater(() -> displayArea.setText(""));
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
