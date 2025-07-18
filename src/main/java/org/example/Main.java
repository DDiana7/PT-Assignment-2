package org.example;

import org.example.BusinessLogic.SelectionPolicy;
import org.example.BusinessLogic.SimulationManager;
import org.example.GUI.SimulationFrame;

import javax.swing.*;

public class Main {
    private static Thread simulationThread = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulationFrame frame = new SimulationFrame();

            frame.getStartButton().addActionListener(e -> {
                frame.clearDisplay();

                if (simulationThread != null && simulationThread.isAlive()) {
                    frame.showError("A simulation is already running. Please wait until it finishes.");
                    return;
                }

                try {
                    int numClients = frame.getNumClients();
                    int numQueues = frame.getNumQueues();
                    int timeLimit = frame.getTimeLimit();
                    int arrivalMin = frame.getArrivalMin();
                    int arrivalMax = frame.getArrivalMax();
                    int serviceMin = frame.getServiceMin();
                    int serviceMax = frame.getServiceMax();

                    SelectionPolicy selectedPolicy = frame.getSelectedStrategy();
                    //Validare date de input
                    if (arrivalMin > arrivalMax || serviceMin > serviceMax || numClients <= 0 || numQueues <= 0 || timeLimit <= 0) {
                        frame.showError("Invalid input values. Make sure:"
                                + "\n - Arrival Min ≤ Arrival Max"
                                + "\n - Service Min ≤ Service Max"
                                + "\n - Clients, Queues, and Time Limit are > 0");
                        return;
                    }

                    SimulationManager manager = new SimulationManager(
                            timeLimit,
                            serviceMin,
                            serviceMax,
                            arrivalMin,
                            arrivalMax,
                            numQueues,
                            numClients,
                            frame,
                            selectedPolicy
                    );

                    simulationThread = new Thread(manager);
                    simulationThread.start();

                } catch (NumberFormatException ex) {
                    frame.showError(ex.getMessage());//Daca utilizatorul nu pune date tip int
                }
            });
        });
    }
}
