package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

import javax.management.InvalidAttributeValueException;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class AddRepetitiveTask extends AddTask {
    private final Map<String, Integer> mapOfDaysOfWeek;
    private DayOfWeek dayOfWeek;

    public AddRepetitiveTask(){
        mapOfDaysOfWeek = new HashMap<String, Integer>() {{
            put("M", 1);
            put("T1", 2);
            put("W", 3);
            put("T2", 4);
            put("F", 5);
            put("S1", 6);
            put("S2", 7);
        }};
    }

    @Override
    public String getDescription() {
        return "Add repetitive task for a day of week";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BotRequest exec(Update answer) {
        return new BotRequest(
                "Write day of week to add repetitive task (M, T1, W, T2, F, S1, S2) " +
                "and time in format 9:00 - 10:00. Example 'T1 9:00 - 10:00'",
                this::askTaskName);
    }

    private BotRequest askTaskName(Update dateTime){
        if (!tryProcessDateTime(dateTime))
            return exec(null);
        return new BotRequest("Write name for your task", this::askTaskDescription);
    }

    @Override
    protected Boolean addTask(TaskType taskType) {
        try {
            return RepetitiveTasks.tryAddTask(
                    dayOfWeek,
                    new Task(
                            timeInterval.getStart(),
                            timeInterval.getEnd(),
                            taskType, name, description));
        } catch (InvalidAttributeValueException | NullPointerException e) {
            return false;
        }
    }

    private Boolean tryProcessDateTime(Update dateTime){
        var splDateTime = dateTime
                .getMessage()
                .getText()
                .split(" - ");
        if (splDateTime.length != 2){
            return false;
        }
        var dayAndStart  = splDateTime[0].split(" ");
        if (dayAndStart.length != 2)
            return false;

        var dayOfWeekAsInt = mapOfDaysOfWeek.get(dayAndStart[0]);
        if (dayOfWeekAsInt == null)
            return false;

        var start = dayAndStart[1];
        var end = splDateTime[1];
        var interval = makeTimeInterval(start, end);
        if (interval == null)
            return false;

        this.dayOfWeek = DayOfWeek.of(dayOfWeekAsInt);
        this.timeInterval = interval;

        return true;
    }

    private TimeInterval makeTimeInterval(String start, String end){
        var splStart = start.split(":");
        if (splStart.length != 2)
            return  null;
        var splEnd = end.split(":");
        if (splEnd.length != 2)
            return  null;
        try{
            return new TimeInterval(
                    new Time(Integer.parseInt(splStart[0]), Integer.parseInt(splStart[1])),
                    new Time(Integer.parseInt(splEnd[0]), Integer.parseInt(splEnd[1]))
            );
        }
        catch (InvalidAttributeValueException e){
            return null;
        }
    }
}
