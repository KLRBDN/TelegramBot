package org.example;

import javax.management.InvalidAttributeValueException;

import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTask implements BotCommand {
    protected Object[] dayAndInterval;
    protected Update name;
    protected Update description;

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String getDescription() {
        return "Добавляет задание";
    }
    
    @Override
    public BasicAnswerHandler exec() {
        return new BasicAnswerHandler(
                "write date and time in format: 10.10.2021 9:00 - 10:00",
                this::askTaskName);
    }

    private BasicAnswerHandler askTaskName(Update dateTime){
        dayAndInterval = processDateTime(dateTime);
        if (dayAndInterval == null)
            return exec();
        return new BasicAnswerHandler(
                "write name for your task",
                this::askTaskDescription);
    }

    protected BasicAnswerHandler askTaskDescription(Update name){
        this.name = name;
        return new BasicAnswerHandler(
                "write description for your task",
                this::askTaskType);
    }

    protected BasicAnswerHandler askTaskType(Update description){
        this.description = description;
        return new BasicAnswerHandler(
                "write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::processAnswer);
    }

    private Object[] processDateTime(Update dateTimeMessage){
        var splitted = dateTimeMessage
                .getMessage()
                .getText()
                .split(" - ");
        if (splitted.length != 2)
            return null;
        var dateAndStartTime = splitted[0];
        var endTime = splitted[1];

        var splDateAndStartTime = dateAndStartTime.split("[. :]");
        if (splDateAndStartTime.length != 5)
            return null;

        var splEndTime = endTime.split(":");
        if (splEndTime.length != 2)
            return null;

        var dateTimeIntArray = new int[7];
        DayInterface day;
        TimeInterval interval;
        try{
            for (var i = 0; i < dateTimeIntArray.length; i++){
                dateTimeIntArray[i] = Integer.parseInt(
                        i < 5 ? splDateAndStartTime[i] : splEndTime[i-5]);
            }
            day = Day.getDay(dateTimeIntArray[0], dateTimeIntArray[1], dateTimeIntArray[2]);
            if (day == null) return null;
            interval = new TimeInterval(
                    new Time(dateTimeIntArray[3], dateTimeIntArray[4]),
                    new Time(dateTimeIntArray[5], dateTimeIntArray[6])
            );
        }
        catch (NumberFormatException | InvalidAttributeValueException e){
            return null;
        }
        var dayAndInterval = new Object[2];
        dayAndInterval[0] = day;
        dayAndInterval[1] = interval;
        return dayAndInterval;
    }

    protected BasicAnswerHandler processAnswer(Update taskType){
        TaskType tskType;
        var typeAsInt = -1;
        try {
            typeAsInt = Integer.parseInt(taskType.getMessage().getText());
        } catch (Exception e) {
            return askTaskType(this.description);
        }
        switch (typeAsInt) {
            case 1:
                tskType = TaskType.overlapping;
                break;
            case 2:
                tskType = TaskType.nonOverlapping;
                break;
            case 3:
                tskType = TaskType.important;
                break;
            default:
                return askTaskType(this.description);
        }
        var descriptionAsStr = description.getMessage().getText();
        var nameAsStr = name.getMessage().getText();
        if (addTask(tskType, descriptionAsStr, nameAsStr, dayAndInterval)){
            return new StandardAnswerHandler("Task was added");
        }
        return exec();
    }

    protected Boolean addTask(TaskType taskType, String description,
                            String name, Object[] dayAndInterval) {
        try {
            var day = (DayInterface)dayAndInterval[0];
            var timeInterval = (TimeInterval)dayAndInterval[1];
            return day.tryAddTask(
                    new Task(
                            timeInterval.getStart(),
                            timeInterval.getEnd(),
                            taskType, name, description));
        } catch (ClassCastException | InvalidAttributeValueException | NullPointerException e) {
            return false;
        }
    }
}