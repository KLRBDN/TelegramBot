package org.example;

import java.util.ArrayList;

public interface DayInterface {

    Boolean tryAddTask(Task task);

    Boolean deleteTask(String name);

    Boolean completeTask(String name);

    ArrayList<Task> getTasks();
}
