package org.example;

import java.util.ArrayList;

public class Day implements DayInterface {
    private ArrayList<Task> tasks;
    private ArrayList<Task> completedTasks;

    public Day() {
        tasks = new ArrayList<Task>();
    }

    public Boolean tryAddTask(Task task) {
        for (Task item : this.getTasks()) {
            if (item.timeInterval.intersects(task.timeInterval))
                if (task.taskType != TaskType.overlapping || item.taskType != TaskType.overlapping)
                    return false;
            if (item.name == task.name)
                return false;
        }
        this.tasks.add(task);
        return true;
    } 

    @Override
    public Boolean deleteTask(String name) {
        for (Task task : tasks)
            if (task.name == name) {
                tasks.remove(task);
                return true;
            }
        return false;
    }

    public Boolean completeTask(String name) {
        for (Task task : tasks)
            if (task.name == name) {
                completedTasks.add(task);
                return deleteTask(task.name);
            }
        return false;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
