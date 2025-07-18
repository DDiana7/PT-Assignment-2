package org.example.Model;

public class Task implements Comparable<Task> {
    private int ID;
    private int arrivalTime;
    private int serviceTime;
    private int startTime = -1;
    private int timeAddedToQueue;

    public Task(int ID, int arrivalTime, int serviceTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getID() {
        return ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setTimeAddedToQueue(int time) {
        this.timeAddedToQueue = time;
    }

    public int getTimeAddedToQueue() {
        return timeAddedToQueue;
    }

    @Override
    public int compareTo(Task o) {
        return this.arrivalTime - o.arrivalTime;
    }
}
