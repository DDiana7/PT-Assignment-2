package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    //Constructor
    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        servers = new ArrayList<>();

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server();
            Thread thread = new Thread(server);
            thread.start();
            servers.add(server);
        }
    }

    //Functie pentru schimbarea strategiei in functie de input (ca utilizatorul sa poata alege)
    public void changeStrategy(SelectionPolicy policy) {
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ShortestQueueStrategy();
        }

        if(policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new TimeStrategy();
        }
    }

    //Functie pentru adaugarea task-ului in queue
    public void dispatchTask(Task task) {
        if(strategy != null){
            strategy.addTask(servers, task);
        }
        else {
            System.out.println("Strategia nu poate fi null. Alegeti o strategie");
        }
    }

    //Functie care returneaza chelnerii
    public List<Server> getServers() {
        return servers;
    }
}
