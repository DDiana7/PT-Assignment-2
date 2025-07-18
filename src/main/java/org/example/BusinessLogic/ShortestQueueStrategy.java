package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task t) {
        Server chelnerAles = servers.get(0);
        int minLoad = chelnerAles.getTasks().length + (chelnerAles.getCurrentTask() == null ? 0 : 1);

        for (Server s : servers) {
            int load = s.getTasks().length + (s.getCurrentTask() == null ? 0 : 1);
            if (load < minLoad) {
                chelnerAles = s;
                minLoad = load;
            }
        }
        chelnerAles.addTask(t);
    }

}
