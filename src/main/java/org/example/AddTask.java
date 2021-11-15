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
                return processAnswer(answer);
            }
        };
    }

    private AnswerHandler processAnswer(Update answer){
        var interval = answer.getMessage().getText();
        var splitted = interval.split(" - ");
        if (splitted.length != 2)
            return exec();
        var dateAndStartTime = splitted[0];
        var endTime = splitted[1];
        if (processDate(dateAndStartTime, endTime)){
            return new StandartAnswerHandler("task was added");
        }
        return exec();
    }

    private Boolean processDate(String dateAndStartTime, String endTime) {
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
                    TaskType.overlapping, 
                    "debug name", 
                    "debug description")
            );
        } catch (InvalidAttributeValueException | NullPointerException e) {
            return false;
        }

        return true;
    }
}