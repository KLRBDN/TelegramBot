package org.example;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Map;

public final class RepetitiveTasks {
    private static Map<DayOfWeek, ArrayList<Task>> repetitiveTasks;

    public static ArrayList<Task> getTasksFor(DayOfWeek day){
        return repetitiveTasks.get(day);
    }

    public static Boolean AddRepetitiveTask(DayOfWeek day){
        
    }
}
