package org.example;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class RepetitiveTasks {
    private static final Map<DayOfWeek, ArrayList<Task>> repetitiveTasks = new HashMap<>();

    public static ArrayList<Task> getTasksFor(DayOfWeek day){
        var tasks = repetitiveTasks.get(day);
        return tasks == null ? new ArrayList<>() : tasks;
    }

    public static Boolean tryAddTask(DayOfWeek day, Task task){
        var tasks = repetitiveTasks.computeIfAbsent(day, k -> new ArrayList<>());
        for (Task tsk : tasks) {
            if (tsk.name.equals(task.name))
                return false;
            if (tsk.timeInterval.intersects(task.timeInterval))
                if (task.taskType != TaskType.overlapping
                        || tsk.taskType != TaskType.overlapping)
                    return false;
        }
        tasks.add(task);
        return true;
    }

    public static Boolean tryDeleteTask(DayOfWeek day, Task task){
        var tasks = repetitiveTasks.get(day);
        if (tasks == null)
            return false;
        return tasks.remove(task);
    }
}
