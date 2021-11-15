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
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write name for your task";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return askTaskDescriprion(answer, dateTime);
            }
        };
    }

    private AnswerHandler askTaskDescriprion(Update name, Update dateTime){
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write description for your task";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return askTaskType(answer, name, dateTime);
            }
        };
    }

    private AnswerHandler askTaskType(Update description, Update name, Update dateTime){
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return processAnswer(answer, description, name, dateTime);
            }
        };
    }

    private AnswerHandler processAnswer(Update taskType, Update description, Update name, Update dateTime){
        var interval = dateTime.getMessage().getText();
        var splitted = interval.split(" - ");
        if (splitted.length != 2)
            return exec();
        var dateAndStartTime = splitted[0];
        var endTime = splitted[1];
        
        TaskType tskType = null;
        switch (Integer.parseInt(taskType.getMessage().getText())) {
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
                return askTaskType(description, name, dateTime);
        };
        var descriptionAsStr = description.getMessage().getText();
        var nameAsStr = name.getMessage().getText();
        if (addTask(tskType, descriptionAsStr, nameAsStr, dateAndStartTime, endTime)){
            return new StandartAnswerHandler("task was added");
        }
        return exec();
    }

    private Boolean addTask(TaskType taskType, String description, 
                            String name, String dateAndStartTime, String endTime) {
        var splitted = dateAndStartTime.split("[. :]");
        if (splitted.length != 5)
            return false;
        var day = Integer.parseInt(splitted[0]);
        var month = Integer.parseInt(splitted[1]);
        var year = Integer.parseInt(splitted[2]);
        var hoursStart = Integer.parseInt(splitted[3]);
        var minutesStart = Integer.parseInt(splitted[4]);

        var splittedEndTime = endTime.split(":");
        if (splittedEndTime.length != 2)
            return false;
        var hoursEnd = Integer.parseInt(splittedEndTime[0]);
        var minutesEnd = Integer.parseInt(splittedEndTime[1]);

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