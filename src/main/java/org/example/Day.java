package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TimeZone;

public class Day implements DayInterface {
    private final static String timeZone = "GMT+05:00";
    private ArrayList<Task> tasks;

    public Day() {
        tasks = new ArrayList<Task>();
    }

    public Boolean tryAddTask(Task task) {
        for (Task item : this.getTasks()) {
            if (item.timeInterval.intersects(task.timeInterval))
                if (task.taskType != TaskType.overlapping || item.taskType != TaskType.overlapping)
                    return false;
            if (item.name.equals(task.name))
                return false;
        }
        this.tasks.add(task);
        return true;
    } 

    @Override
    public Boolean deleteTask(String name) {
        for (Task task : tasks)
            if (task.name.equals(name)) {
                tasks.remove(task);
                return true;
            }
        return false;
    }

    public Boolean completeTask(String name) {
        for (Task task : tasks)
            if (task.name.equals(name)) {
                YearsDataBase.completedTasks.add(new Object[] { task, getTodayDate() });
                return deleteTask(task.name);
            }
        return false;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public static DayInterface getToday() {
        var zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
        return getDay(LocalDate.now(zoneId));
    }

    public static DayInterface getDay(LocalDate date) {
        return getDay(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    public static DayInterface getDay(int day, int month, int year) {
        var yearsDateBase = YearsDataBase.getInstance();
        return yearsDateBase.getYear(year).getMonth(month).getDay(day);
    }

    public static String getTodayDate() {
        var zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
        var todayDate = LocalDate.now(zoneId);
        return todayDate.getDayOfMonth() + "." +
                todayDate.getMonthValue() + "." +
                todayDate.getYear();
    }
}
