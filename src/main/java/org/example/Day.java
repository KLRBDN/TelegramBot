package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Day implements DayInterface {
    private ArrayList<Task> tasks;
    private ArrayList<Task> completedTasks;

    public Day() {
        tasks = new ArrayList<Task>();
    }

    public Boolean TryAddTask(TimeInterval timeInterval, Task task) {
        for (Task item : this.getTasks()) {
            if ((task.time.getStart() > item.time.getStart() && task.time.getStart() < item.time.getEnd()) ||
                (task.time.getEnd() > item.time.getStart() && task.time.getEnd() < item.time.getEnd()))
                if (task.taskType != TaskType.overlapping || item.taskType != TaskType.overlapping)
                    return false;
            if (item.name == task.name)
                return false;
        }
        this.tasks.add(task);
        return true;
    } 

    @Override
    public Boolean DeleteTask(String name) {
        for (Task task : tasks)
            if (task.name == name) {
                tasks.remove(task);
                return true;
            }
        return false;
    }

    public Boolean CompleteTask(String name) {
        for (Task task : tasks)
            if (task.name == name) {
                completedTasks.add(task);
                return DeleteTask(task.name);
            }
        return false;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
