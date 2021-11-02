package org.example;

public class Task {
    public TimeInterval timeInterval;
    public TaskType taskType;
    public String name;
    public String description;

    public Task(Time start, Time end, TaskType taskType, String name, String description) {
        super();
        this.timeInterval = new TimeInterval(start, end);
        this.taskType = taskType;
        this.name = name;
        this.description = description;
    }
}
