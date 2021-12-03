package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
        return "Добавляет повторяющуюся задачу на выбранный день недели";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BotRequest exec(Update answer) {
        return new BotRequest(
                "Write day of week to add repetitive task (M, T1, W, T2, F, S1, S2)",
                this::askTimeInterval);
    }

    private BotRequest askTimeInterval(Update answer){
        var dayOfWeekAsInt = mapOfDaysOfWeek.get(answer.getMessage().getText());
        if (dayOfWeekAsInt == null)
            return new BotRequest(
                    "Write day of week to add repetitive task (M, T1, W, T2, F, S1, S2)",
                    this::askTimeInterval);
        this.dayOfWeek = DayOfWeek.of(dayOfWeekAsInt);
        return new BotRequest("Write time interval of your task in format: 9:00 - 10:00", this::askTaskName);
    }

    private BotRequest askTaskName(Update time){
        if (!tryProcessDateTime(time))
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
        var splTime = dateTime
                .getMessage()
                .getText()
                .split(" - ");
        if (splTime.length != 2)
            return false;

        var interval = makeTimeInterval(splTime[0], splTime[1]);
        if (interval == null)
            return false;

        this.timeInterval = interval;

        return true;
    }
}
