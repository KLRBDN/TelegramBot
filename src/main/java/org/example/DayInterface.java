package org.example;

import java.util.ArrayList;

public interface DayInterface {

    Boolean tryAddTask(Task task);

    Boolean deleteTask(Task task);

    Boolean completeTask(String name);

    ArrayList<Task> getTasks();
}
