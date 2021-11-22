package org.example;

import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTask implements BotCommand {

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String getDescription() {
        return "Добавляет задание";
    }
    
    @Override
    public AnswerHandler exec() {
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write date and time in format: 10.10.2021 9:00 - 10:00";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return askTaskName(answer);
            }
        };
    }

    private AnswerHandler askTaskName(Update dateTime){
        var dayAndInterval = processDateTime(dateTime);
        if (dayAndInterval == null)
            return exec();
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write name for your task";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return askTaskDescriprion(answer, dayAndInterval);
            }
        };
    }

    private AnswerHandler askTaskDescriprion(Update name, Object[] dayAndInterval){
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write description for your task";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return askTaskType(answer, name, dayAndInterval);
            }
        };
    }

    private AnswerHandler askTaskType(Update description, Update name, Object[] dayAndInterval){
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return processAnswer(answer, description, name, dayAndInterval);
            }
        };
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

    private AnswerHandler processAnswer(Update taskType, Update description, Update name, Object[] dayAndInterval){
        TaskType tskType;
        var typeAsInt = -1;
        try {
            typeAsInt = Integer.parseInt(taskType.getMessage().getText());
        } catch (Exception e) {
            return askTaskType(description, name, dayAndInterval);
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
                return askTaskType(description, name, dayAndInterval);
        }
        var descriptionAsStr = description.getMessage().getText();
        var nameAsStr = name.getMessage().getText();
        if (addTask(tskType, descriptionAsStr, nameAsStr, dayAndInterval)){
            return new StandardAnswerHandler("Task was added");
        }
        return exec();
    }

    private Boolean addTask(TaskType taskType, String description, 
                            String name, Object[] dayAndInterval) {
        try {
            var day = (DayInterface)dayAndInterval[0];
            var timeInterval = (TimeInterval)dayAndInterval[1];
            day.tryAddTask(
                new Task(
                        timeInterval.getStart(),
                        timeInterval.getEnd(),
                        taskType, name, description)
            );
            return true;
        } catch (InvalidAttributeValueException | NullPointerException e) {
            return false;
        }
    }
}