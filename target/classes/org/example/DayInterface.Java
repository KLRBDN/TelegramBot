package org.example;

import java.util.ArrayList;

public interface DayInterface {

    public Boolean TryAddTask(TimeInterval timeInterval, Task task);

    public Boolean DeleteTask(String name);

    public ArrayList<Task> getTasks();
}
