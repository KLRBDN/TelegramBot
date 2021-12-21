package TaskConfiguration;

import DateStructure.Day;
import DateStructure.RepetitiveDate;
import TaskConfiguration.Task;
import TaskConfiguration.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class RepetitiveTasks {
    private static final Map<RepetitiveDate, ArrayList<Task>> repetitiveDatesAndTasks = new HashMap<>();

    public static ArrayList<Task> getTasksFor(RepetitiveDate date){
        var tasks = repetitiveDatesAndTasks.get(date);
        return tasks == null ? new ArrayList<>() : tasks;
    }

    public static ArrayList<Task> getTasksFor(Day day){
        var allTasks = new ArrayList<Task>();
        for (var entry: repetitiveDatesAndTasks.entrySet())
            if (entry.getKey().match(day.getDate()))
                for (var task: entry.getValue())
                    if (!day.getDeletedRepetitiveTasks().contains(task))
                        allTasks.add(task);
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
        return tasks.add(task);
    }

    public static Boolean tryDeleteTask(RepetitiveDate date, Task task){
        var tasks = repetitiveDatesAndTasks.get(date);
        if (tasks == null)
            return false;
        return tasks.remove(task);
    }

    public static Boolean tryDeleteTask(String taskName) {
        for (Map.Entry<RepetitiveDate, ArrayList<Task>> entry: repetitiveDatesAndTasks.entrySet()) {
            var tasks = entry.getValue();
            for (Task task: tasks) {
                if (task.name.equals(taskName)){
                    if (tasks.size() == 1){
                        repetitiveDatesAndTasks.remove(entry.getKey());
                        return true;
                    }
                    return tasks.remove(task);
                }
            }
        }
        return false;
    }

    public static void clearAll(){
        repetitiveDatesAndTasks.clear();
    }
}
