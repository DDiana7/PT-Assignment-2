package org.example.Model;

import org.example.BusinessLogic.SimulationManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private volatile Task currentTask;

    public Server() {
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task task) {
        try {
            tasks.put(task);
            waitingPeriod.addAndGet(task.getServiceTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Task addition interrupted: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Ia un task
                currentTask = tasks.take();

                // Verifica sa fim pe timpul corect
                while (SimulationManager.getCurrentSimulationTime() < currentTask.getTimeAddedToQueue()) {
                    Thread.sleep(10);
                }

                // Se marcheaza start time
                int now = SimulationManager.getCurrentSimulationTime();
                currentTask.setStartTime(now);

                // Contorizam waiting time si service time
                int waitingTime = now - currentTask.getTimeAddedToQueue();
                SimulationManager.recordWaitingAndServiceTime(waitingTime, currentTask.getServiceTime());

                // Procesam task-ul
                int serviceLeft = currentTask.getServiceTime();
                int lastTick = now;
                while (serviceLeft > 0) {

                    // verificam daca SimulationManager a trecut la urmatorul tick
                    int currentTick = SimulationManager.getCurrentSimulationTime();
                    if (currentTick > lastTick) {
                        serviceLeft--;
                        waitingPeriod.decrementAndGet();
                        lastTick = currentTick;
                    } else {
                        Thread.sleep(10);
                    }
                }

                // Task-ul e gata
                currentTask = null;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public Task getCurrentTask() {
        return currentTask;
    }
}
