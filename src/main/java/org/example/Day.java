package org.example;

import java.util.HashMap;
import java.util.Map;

public class Day implements DayInterface {
    private Map<TimeInterval, Task> tasks;

    public Day() {
        tasks = new HashMap<TimeInterval, Task>();
    }

    @Override
    public Boolean AddTask(TimeInterval timeInterval, Task task) {
        // Примерная будущая реализация
        // if (TryAddTask(task))
        //     DateToTasksDict[task.date].add(task);
        // Примерная будущая реализация
        // for (Task item : DateToTasksDict[task.date]) {
        //     if ((task.start > item.start && task.start < item.finish) || (task.finish > item.start && task.finish < item.finish))
        //         if (task.taskType != TaskType.overlapping || item.taskType != TaskType.overlapping)
        //             return false;
        // }
        // return true;
        return false;
    }

    @Override
    public Boolean DeleteTask(TimeInterval time) {
        if (tasks.containsKey(time)){
            tasks.remove(time);
            return true;
        }
        return false;
    }

    @Override
    public Map<TimeInterval, Task> getTasks() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
