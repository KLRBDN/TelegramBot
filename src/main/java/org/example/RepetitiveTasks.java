package org.example;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class RepetitiveTasks {
    private static final Map<RepetitiveDate, ArrayList<Task>> repetitiveDatesAndTasks = new HashMap<>();

    public static ArrayList<Task> getTasksFor(RepetitiveDate date){
        var tasks = repetitiveDatesAndTasks.get(date);
        return tasks == null ? new ArrayList<>() : tasks;
    }

    public static ArrayList<Task> getTasksFor(LocalDate date){
        var allTasks = new ArrayList<Task>();
        for (var entry: repetitiveDatesAndTasks.entrySet())
            if (entry.getKey().match(date))
                allTasks.addAll(entry.getValue());
        return allTasks;
    }

    public static Boolean tryAddTask(RepetitiveDate date, Task task){
        if (date == null)
            return false;
        var tasks = repetitiveDatesAndTasks.computeIfAbsent(date, k -> new ArrayList<>());
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

    public static Boolean tryDeleteTask(RepetitiveDate date, Task task){
        var tasks = repetitiveDatesAndTasks.get(date);
        if (tasks == null)
            return false;
        return tasks.remove(task);
    }
}
