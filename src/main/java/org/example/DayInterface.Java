package org.example;

import java.util.ArrayList;

public interface DayInterface {

    public Boolean tryAddTask(Task task);

    public Boolean deleteTask(String name);

    public ArrayList<Task> getTasks();
}
