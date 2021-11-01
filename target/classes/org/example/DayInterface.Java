package org.example;

import java.util.Map;

public interface DayInterface {
    public Boolean AddTask(TimeInterval timeInterval, Task task);
    public Boolean DeleteTask(TimeInterval time);
    public Map<TimeInterval, Task> getTasks();
}
