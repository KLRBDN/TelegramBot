package org.example;

import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Update;

public class CompleteTask implements BotCommand {
    private final YearsDataBase yearsDataBase;

    public CompleteTask(YearsDataBase yearsDataBase) {
        super();
        this.yearsDataBase = yearsDataBase;
    }

    @Override
    public String getDescription() {
        return "Completes task and deletes it from active tasks";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public AnswerHandler exec() {
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "write date of completed task and its name in format: 10.10.2021 taskname";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return processAnswer(answer);
            }
        };
    }

    private AnswerHandler processAnswer(Update answer){
        var line = answer.getMessage().getText();
        if (line == "stop" || line == " ")
            return null;
        var splitted = line.split(" ");
        if (splitted.length != 2)
            return exec();
        var date = splitted[0];
        var name = splitted[1];
        if (processDate(date, name)){
            return new StandartAnswerHandler("task was completed");
        }
        return exec();
    }

    private Boolean processDate(String date, String name) {
        var splitted = date.split("\\.");
        if (splitted.length != 3)
            return false;
        var day = Integer.parseInt(splitted[0]);
        var month = Integer.parseInt(splitted[1]);
        var year = Integer.parseInt(splitted[2]);

        try {
            return yearsDataBase.
                    getYear(year)
                    .getMonth(month)
                    .getDay(day)
                    .completeTask(name);
        } catch (NullPointerException e) {
            return false;
        }
    }
  
}
