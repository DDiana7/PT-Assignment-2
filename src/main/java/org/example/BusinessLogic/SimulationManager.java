package org.example.BusinessLogic;

import org.example.GUI.SimulationFrame;
import org.example.Model.Server;
import org.example.Model.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulationManager implements Runnable {
    public static int totalWaitingTime = 0;
    public static int totalServiceTime = 0;
    public static int totalServedTasks = 0;
    public static int currentSimulationTime = 0;

    private final int timeLimit;
    private final int maxProcessingTime;
    private final int minProcessingTime;
    private final int maxArrivalTime;
    private final int minArrivalTime;
    private final int numberOfServers;
    private final int numberOfClients;
    private final int maxTasksPerServer = 1000;
    private SelectionPolicy selectionPolicy;

    //Map pentru calculat peak time
    private final Map<Integer, Integer> tasksInQueuesAtTime = new HashMap<>();

    private FileWriter logWriter;
    private final Scheduler scheduler;
    private final SimulationFrame frame;
    private List<Task> generatedTasks;

    //Constructor
    public SimulationManager(int timeLimit, int minProcessingTime, int maxProcessingTime,
                             int minArrivalTime, int maxArrivalTime,
                             int numberOfServers, int numberOfClients, SimulationFrame frame, SelectionPolicy selectionPolicy){
        this.timeLimit = timeLimit;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.frame = frame;
        this.selectionPolicy = selectionPolicy;

        try {
            logWriter = new FileWriter("log.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduler = new Scheduler(numberOfServers, maxTasksPerServer);
        scheduler.changeStrategy(selectionPolicy);
        generatedTasks = new ArrayList<>();
        generateNRandomTasks();
    }

    //Functie pentru generarea task-urilor
    private void generateNRandomTasks() {
        Random random = new Random();
        List<Task> tempTasks = new ArrayList<>();

        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = random.nextInt((maxArrivalTime - minArrivalTime) + 1) + minArrivalTime;
            int processingTime = random.nextInt((maxProcessingTime - minProcessingTime) + 1) + minProcessingTime;
            tempTasks.add(new Task(0, arrivalTime, processingTime));
        }

        // Sortam in functie de arrival time
        tempTasks.sort(Comparator.comparingInt(Task::getArrivalTime));

        // Redistribuim id-urile
        for (int i = 0; i < tempTasks.size(); i++) {
            Task t = tempTasks.get(i);
            tempTasks.set(i, new Task(i + 1, t.getArrivalTime(), t.getServiceTime()));
        }

        generatedTasks = tempTasks;
    }

    //Functie pentru pornirea simularii
    @Override
    public void run() {
        currentSimulationTime = 0;

        while (currentSimulationTime < timeLimit) {

            // Pun in queues task-urile care se pot adauga
            List<Task> dispatchNow = new ArrayList<>();
            for (Task t : generatedTasks) {
                if (t.getArrivalTime() <= currentSimulationTime) {
                    dispatchNow.add(t);
                }
            }
            //Si le scot din lista de task-uri generate
            generatedTasks.removeAll(dispatchNow);

            //Le setez timpul de adaugare si le pun in queue folosindu-ma de scheduler
            for (Task t : dispatchNow) {
                t.setTimeAddedToQueue(currentSimulationTime);
                scheduler.dispatchTask(t);
            }

            // Thread sleep ca sa mi se sincronizeze cu timpul simularii
            // Am avut multe probleme cu sincronizatul, dar asta pare sa functioneze
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Afisez datele
            afisareStatus(currentSimulationTime);

            // Verific daca am ajuns sa servesc toti clientii, daca da, afisez statisticile de final
            // Si inchei simularea
            if (allClientsServed()) {
                logFinalStatistics();
                return;
            }

            // Iar dau sleep, apoi trec mai departe
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            currentSimulationTime++;
        }

        // La final, chiar daca nu au fost serviti toti clientii, tot afisez statisticile
        logFinalStatistics();
    }

    //Functia folosita pentru a vedea waiting clients, queues, clientii din queues
    private void afisareStatus(int currentTime) {
        try {
            StringBuilder logLine = new StringBuilder();
            //Timpul curent
            logLine.append("Time ").append(currentTime).append("\n");

            //Clientii care nu au fost inca asignati la queue
            logLine.append("Waiting clients: ");
            List<Task> sortedWaiting = new ArrayList<>(generatedTasks);
            sortedWaiting.sort(Comparator.comparingInt(Task::getID));
            for (Task w : sortedWaiting) {
                if (w.getStartTime() < 0) {
                    logLine.append("(").append(w.getID()).append(",")
                            .append(w.getArrivalTime()).append(",")
                            .append(w.getServiceTime()).append("); ");
                } else {
                    int leftover = w.getServiceTime() - (currentTime - w.getStartTime());
                    if (leftover > 0) {
                        logLine.append("(").append(w.getID()).append(",")
                                .append(w.getArrivalTime()).append(",")
                                .append(leftover).append("); ");
                    }
                }
            }
            logLine.append("\n");

            //Cozile
            int i = 1;
            for (Server s : scheduler.getServers()) {
                logLine.append("Queue ").append(i).append(": ");
                Task current = s.getCurrentTask();
                Task[] pending = s.getTasks();

                //Daca serverul nu are task-uri de facut si nici nu lucreaza la unul in prezent, atunci queue-ul e closed
                if (current == null && pending.length == 0) {
                    logLine.append("closed\n");
                    i++;
                    continue;
                }

                //Daca lucreaza acum
                if (current != null) {
                    //Pentru a afisa cat timp mai are de lucru
                    int leftover = current.getServiceTime() - (currentTime - current.getStartTime());
                    //Daca nu e >0, nu se afiseaza, e gata
                    if (leftover > 0) {
                        logLine.append("(").append(current.getID()).append(",")
                                .append(current.getArrivalTime()).append(",")
                                .append(leftover).append("); ");
                    }
                }

                //Afisez restul task-urilor pe care urmeaza sa le faca serverul
                for (Task p : pending) {
                    //Sare peste task-ul la care lucreaza
                    if (current != null && p.getID() == current.getID()) {
                        continue;
                    }
                    if (p.getStartTime() < 0) {
                        logLine.append("(").append(p.getID()).append(",")
                                .append(p.getArrivalTime()).append(",")
                                .append(p.getServiceTime()).append("); ");
                    } else {
                        int leftover = p.getServiceTime() - (currentTime - p.getStartTime());
                        if (leftover > 0) {
                            logLine.append("(").append(p.getID()).append(",")
                                    .append(p.getArrivalTime()).append(",")
                                    .append(leftover).append("); ");
                        }
                    }
                }
                logLine.append("\n");
                i++;
            }

            logLine.append("\n");
            logWriter.write(logLine.toString());
            logWriter.flush();

            frame.appendText(logLine.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        int count = 0;
        for (Server s : scheduler.getServers()) {
            if (s.getCurrentTask() != null) count++;
            count += s.getTasks().length;
        }
        tasksInQueuesAtTime.put(currentTime, count);
    }

    //Functie pentru a vedea daca am servit toti clientii
    private boolean allClientsServed() {
        if (!generatedTasks.isEmpty()) {
            return false;
        }
        for (Server s : scheduler.getServers()) {
            if (s.getCurrentTask() != null || s.getTasks().length > 0) {
                return false;
            }
        }
        return true;
    }

    //Functie pentru calcularea statisticilor de la final
    private void logFinalStatistics() {
        int peakTime = 0;
        int maxTasks = 0;
        for (Map.Entry<Integer, Integer> entry : tasksInQueuesAtTime.entrySet()) {
            if (entry.getValue() > maxTasks) {
                peakTime = entry.getKey();
                maxTasks = entry.getValue();
            }
        }

        double avgWait;
        if (totalServedTasks > 0) {
            avgWait = (double) totalWaitingTime / totalServedTasks;
        } else {
            avgWait = 0;
        }

        double avgService;
        if (totalServedTasks > 0) {
            avgService = (double) totalServiceTime / totalServedTasks;
        } else {
            avgService = 0;
        }

        try {
            String stats = String.format(
                    "\n--- Simulation Results ---\n" +
                            "Average waiting time: %.2f seconds\n" +
                            "Average service time: %.2f seconds\n" +
                            "Peak hour: %d (with %d tasks in queues)\n",
                    avgWait, avgService, peakTime, maxTasks
            );
            logWriter.write(stats);
            logWriter.close();
            frame.appendText(stats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Functie pentru calculele statisticilor
    public static synchronized void recordWaitingAndServiceTime(int waitingTime, int serviceTime) {
        totalWaitingTime += waitingTime;
        totalServiceTime += serviceTime;
        totalServedTasks++;
    }

    //Functie pentru timpul curent
    public static int getCurrentSimulationTime() {
        return currentSimulationTime;
    }
}
