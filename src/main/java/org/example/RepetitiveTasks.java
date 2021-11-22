package org.example;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class RepetitiveTasks {
    private static Map<DayOfWeek, ArrayList<Task>> repetitiveTasks = new HashMap<DayOfWeek, ArrayList<Task>>();

    public static ArrayList<Task> getTasksFor(DayOfWeek day){
        return repetitiveTasks.get(day);
    }

    public static Boolean tryAddRepetitiveTask(DayOfWeek day, Task task){
        var tasks = repetitiveTasks.get(day);
        if (tasks == null){
            tasks = new ArrayList<Task>();
            tasks.add(task);
            repetitiveTasks.put(day, tasks);
        }
        else{
            for (Task tsk : tasks) {
                if (tsk.name.equals(task.name))
                    return false;
                if (tsk.timeInterval.intersects(task.timeInterval))
                    if (task.taskType != TaskType.overlapping
                            || tsk.taskType != TaskType.overlapping)
                        return false;
            }
            tasks.add(task);
        }
        return true;
    }
}
