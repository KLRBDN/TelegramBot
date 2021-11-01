package org.example;

import java.util.Calendar;

public class Task {
    public Calendar start;
    public Calendar finish;
    public TaskType taskType;
    public String name;
    public String description;

    public Task(Calendar start, int hours, TaskType taskType, String name, String description) {
        super();
        this.start = start;
        this.finish.set(Calendar.MONTH, start.get(Calendar.MONTH));
        this.finish.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH));
        this.finish.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY) + hours);
        this.taskType = taskType;
        this.name = name;
        this.description = description;
    }

    public boolean TryAddTask(Task task) {
        // Примерная будущая реализация
        // for (Task item : DateToTasksDict[task.date]) {
        //     if ((task.start > item.start && task.start < item.finish) || (task.finish > item.start && task.finish < item.finish))
        //         if (task.taskType != TaskType.overlapping || item.taskType != TaskType.overlapping)
        //             return false;
        // }
        // return true;
        return false;
    
    }

    public void AddTask(Task task) {
        // Примерная будущая реализация
        // if (TryAddTask(task))
        //     DateToTasksDict[task.date].add(task);
    }

}
