package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class TimeStrategy implements Strategy{

    @Override
    public void addTask(List<Server> servers, Task t) {
        Server chelnerAles = servers.get(0);
        int minWaiting = chelnerAles.getWaitingPeriod();
        for (Server s : servers) {
            if (s.getWaitingPeriod() < minWaiting) {
                chelnerAles = s;
                minWaiting = s.getWaitingPeriod();
            }
        }
        chelnerAles.addTask(t);
    }

}
