package org.example;

import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTask implements BotCommand {
    private final YearsDataBase yearsDataBase;

    public AddTask(YearsDataBase yearsDataBase) {
        super();
        this.yearsDataBase = yearsDataBase;
    }

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
        var splDateTime = processDateTime(dateTime);
        if (splDateTime == null)
            return exec();
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write name for your task";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return askTaskDescriprion(answer, splDateTime);
            }
        };
    }

    private AnswerHandler askTaskDescriprion(Update name, String[] splDateTime){
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write description for your task";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return askTaskType(answer, name, splDateTime);
            }
        };
    }

    private AnswerHandler askTaskType(Update description, Update name, String[] splDateTime){
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return processAnswer(answer, description, name, splDateTime);
            }
        };
    }

    private String[] processDateTime(Update dateTime){
        var interval = dateTime.getMessage().getText();
        var splitted = interval.split(" - ");
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
        
        var splDateTime = new String[7];
        for (var i = 0; i < splDateTime.length; i++){
            if (i < 5)
                splDateTime[i] = splDateAndStartTime[i];
            else
                splDateTime[i] = splEndTime[i-5];
        }

        return splDateTime;
    }

    private AnswerHandler processAnswer(Update taskType, Update description, Update name, String[] splDateTime){
        TaskType tskType = null;
        var typeAsInt = -1;
        try {
            typeAsInt = Integer.parseInt(taskType.getMessage().getText());
        } catch (Exception e) {
            return askTaskType(description, name, splDateTime);
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
                return askTaskType(description, name, splDateTime);
        };
        var descriptionAsStr = description.getMessage().getText();
        var nameAsStr = name.getMessage().getText();
        if (addTask(tskType, descriptionAsStr, nameAsStr, splDateTime)){
            return new StandartAnswerHandler("task was added");
        }
        return exec();
    }

    private Boolean addTask(TaskType taskType, String description, 
                            String name, String[] splDateTime) {
        if (splDateTime.length != 7)
            return false;
        var day = Integer.parseInt(splDateTime[0]);
        var month = Integer.parseInt(splDateTime[1]);
        var year = Integer.parseInt(splDateTime[2]);
        var hoursStart = Integer.parseInt(splDateTime[3]);
        var minutesStart = Integer.parseInt(splDateTime[4]);

        var hoursEnd = Integer.parseInt(splDateTime[5]);
        var minutesEnd = Integer.parseInt(splDateTime[6]);

        try {
            yearsDataBase
            .getYear(year)
            .getMonth(month)
            .getDay(day)
            .tryAddTask(
                new Task(
                    new Time(hoursStart, minutesStart), 
                    new Time(hoursEnd, minutesEnd), 
                    taskType, name, description)
            );
        } catch (InvalidAttributeValueException | NullPointerException e) {
            return false;
        }

        return true;
    }
}