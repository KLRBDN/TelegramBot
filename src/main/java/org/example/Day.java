package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TimeZone;

public class Day implements DayInterface {
    private final static String timeZone = "GMT+05:00";
    private final ArrayList<Task> tasks;
    private final LocalDate date;

    public Day(LocalDate date) {
        tasks = new ArrayList<>();
        this.date = date;
    }

    public LocalDate getDate(){
        return date;
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
    public Boolean deleteTask(Task task) {
        return tasks.remove(task)
                || RepetitiveTasks.tryDeleteTask(date.getDayOfWeek(), task);
    }

    @Override
    }

    public Boolean completeTask(String name) {
        for (Task task : getTasks())
            if (task.name.equals(name)) {
                YearsDataBase.completedTasks.add(new Object[] { task, getTodayDate() });
                return deleteTask(task);
            }
        return false;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return merge(RepetitiveTasks.getTasksFor(date.getDayOfWeek()), tasks);
    }

    private ArrayList<Task> merge(ArrayList<Task> first, ArrayList<Task> second){
        var mergeResultList = new ArrayList<Task>(
                first.size() + second.size());
        mergeResultList.addAll(first);
        mergeResultList.addAll(second);
        return mergeResultList;
    }

    public static DayInterface getToday() {
        var zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
        return getDay(LocalDate.now(zoneId));
    }

    public static DayInterface getDay(String date) {
        var splitted = date.split("\\.");
        var day = Integer.parseInt(splitted[0]);
        var month = Integer.parseInt(splitted[1]);
        var year = Integer.parseInt(splitted[2]);
        return getDay(day, month, year);
    }

    public static DayInterface getDay(LocalDate date) {
        return getDay(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    public static DayInterface getDay(int day, int month, int year) {
        var yearsDateBase = YearsDataBase.getInstance();
        var yearObject = yearsDateBase.getYear(year);
        if (yearObject == null)
            return null;
        var monthObject = yearObject.getMonth(month);
        if (monthObject == null)
            return null;
        return monthObject.getDay(day);
    }

    public String convertToString(){
        return date.getDayOfMonth() + "." +
                date.getMonthValue() + "." +
                date.getYear();
    }

    public static String getTodayDate() {
        var day = (Day)getToday();
        return day.convertToString();
    }
}
