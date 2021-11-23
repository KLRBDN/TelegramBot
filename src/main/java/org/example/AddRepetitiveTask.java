package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

import javax.management.InvalidAttributeValueException;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class AddRepetitiveTask extends AddTask {
    private final Map<String, Integer> mapOfDaysOfWeek;

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
        return "add repetitive task for a day of week";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BasicAnswerHandler exec() {
        return new BasicAnswerHandler(
                "write day of week to add repetitive task (M, T1, W, T2, F, S1, S2) " +
                        "and time in format 9:00 - 10:00. Example 'T1 9:00 - 10:00'",
                this::askTaskName);
    }

    private BasicAnswerHandler askTaskName(Update dateTime){
        dayAndInterval = processDayAndInterval(dateTime);
        if (dayAndInterval == null)
            return exec();
        return new BasicAnswerHandler("write name for your task", this::askTaskDescription);
    }

    @Override
    protected Boolean addTask(TaskType taskType, String description,
                              String name, Object[] dayAndInterval) {
        try {
            var dayOfWeek = (DayOfWeek)dayAndInterval[0];
            var timeInterval = (TimeInterval)dayAndInterval[1];
            return RepetitiveTasks.tryAddRepetitiveTask(
                    dayOfWeek,
                    new Task(
                            timeInterval.getStart(),
                            timeInterval.getEnd(),
                            taskType, name, description));
        } catch (InvalidAttributeValueException | NullPointerException e) {
            return false;
        }
    }

    private Object[] processDayAndInterval(Update dateTime){
        var splDateTime = dateTime
                .getMessage()
                .getText()
                .split(" - ");
        if (splDateTime.length != 2){
            return null;
        }
        var dayAndStart  = splDateTime[0].split(" ");
        if (dayAndStart.length != 2)
            return null;
        var dayOfWeek = mapOfDaysOfWeek.get(dayAndStart[0]);
        if (dayOfWeek == null)
            return null;

        var start = dayAndStart[1];
        var splStart = start.split(":");
        if (splStart.length != 2)
            return  null;
        var end = splDateTime[1];
        var splEnd = end.split(":");
        if (splEnd.length != 2)
            return  null;
        TimeInterval interval;
        try{
            interval = new TimeInterval(
                    new Time(Integer.parseInt(splStart[0]), Integer.parseInt(splStart[1])),
                    new Time(Integer.parseInt(splEnd[0]), Integer.parseInt(splEnd[1]))
            );
        }
        catch (InvalidAttributeValueException e){
            return null;
        }

        var dayAndInterval = new Object[2];
        dayAndInterval[0] = DayOfWeek.of(dayOfWeek);
        dayAndInterval[1] = interval;

        return dayAndInterval;
    }
}
