package org.example;

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
    public BasicAnswerHandler exec() {
        return new BasicAnswerHandler(
                "write date of completed task and its name in format: 10.10.2021 taskname",
                this::processAnswer);
    }

    private BasicAnswerHandler processAnswer(Update answer){
        var line = answer.getMessage().getText();
        var splitted = line.split(" ");
        if (splitted.length == 2) {
            var date = splitted[0];
            var name = splitted[1];
            if (processDate(date, name)) {
                return new StandardAnswerHandler("task was completed");
            }
        }
        return new BasicAnswerHandler(
                "Error: Wrong date or task name, please try again and write the date" +
                " of completed task and its name in format: 10.10.2021 name_of_task",
                this::processAnswer);
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
