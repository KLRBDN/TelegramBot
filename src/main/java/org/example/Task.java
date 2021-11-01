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
}
