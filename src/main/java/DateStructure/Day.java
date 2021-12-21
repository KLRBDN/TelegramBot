package DateStructure;

import TaskConfiguration.RepetitiveTasks;
import TaskConfiguration.Task;
import TaskConfiguration.TaskType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TimeZone;

public class Day {
    private final static String timeZone = "GMT+05:00";
    private final ArrayList<Task> tasks;
    private final LocalDate date;
    private int importantTasksCount = 0;
    private final ArrayList<Task> deletedRepetitiveTasks;

    public Day(LocalDate date) {
        tasks = new ArrayList<>();
        deletedRepetitiveTasks = new ArrayList<>();
        this.date = date;
    }

    public LocalDate getDate(){
        return date;
    }

    public ArrayList<Task> getDeletedRepetitiveTasks(){
        return deletedRepetitiveTasks;
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
        if (task.taskType == TaskType.important)
            this.importantTasksCount++;
        return true;
    }

    public Boolean deleteTask(Task task) {
        var deletedSuccessfully = tasks.remove(task) || deletedRepetitiveTasks.add(task);
        if (deletedSuccessfully && task.taskType == TaskType.important)
            this.importantTasksCount--;
        return deletedSuccessfully;
    }

    public Boolean deleteTask(String name) {
        var task = getTask(name);
        if (task == null)
            return false;
        return deleteTask(task);
    }

    public Task getTask(String name){
        for (Task task : getTasks())
            if (task.name.equals(name)) {
                return task;
            }
        return null;
    }

    public Boolean completeTask(String name) {
        var task = getTask(name);
        return task != null && deleteTask(task)
                && YearsDataBase.completedTasks.add(new Object[]{task, getTodayDate()});
    }

    public ArrayList<Task> getTasks() {
        return merge(RepetitiveTasks.getTasksFor(this), tasks);
    }

    public Boolean hasImportantTasks() {
        return this.importantTasksCount > 0;
    }

    private ArrayList<Task> merge(ArrayList<Task> first, ArrayList<Task> second){
        var mergeResultList = new ArrayList<Task>(
                first.size() + second.size());
        mergeResultList.addAll(first);
        mergeResultList.addAll(second);
        return mergeResultList;
    }

    public static Day getToday() {
        var zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
        return getDay(LocalDate.now(zoneId));
    }

    public static Day getDay(String date) {
        var splitted = date.split("\\.");
        var day = Integer.parseInt(splitted[0]);
        var month = Integer.parseInt(splitted[1]);
        var year = Integer.parseInt(splitted[2]);
        return getDay(day, month, year);
    }

    public static Day getDay(LocalDate date) {
        return getDay(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    public static Day getDay(int day, int month, int year) {
        var yearsDateBase = YearsDataBase.getInstance();
        var yearObject = yearsDateBase.getYear(year);
        if (yearObject == null)
            return null;
        var monthObject = yearObject.getMonth(month);
        if (monthObject == null)
            return null;
        return monthObject.getDay(day);
    }

    public String toString(){
        return date.getDayOfMonth() + "." +
                date.getMonthValue() + "." +
                date.getYear();
    }

    public static String getTodayDate() {
        var day = getToday();
        return day.toString();
    }
}
