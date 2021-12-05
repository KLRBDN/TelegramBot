package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

public class GetCompletedTasks implements BotCommand{

    @Override
    public String getDescription() {
        return "Возвращает все выполненные задачи за всё время";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BotRequest exec(Update answer) {
        var strBuilder = new StringBuilder();
        for (var pair : YearsDataBase.completedTasks) {
            var task = (Task) pair[0];
            var date = (String) pair[1];
            strBuilder.append(task.name)
                    .append(": ")
                    .append("completed on ")
                    .append(date)
                    .append("\n");
        }
        if (strBuilder.length() == 0)
            return new StandardBotRequest("No tasks completed");
        return new StandardBotRequest(strBuilder.toString());
    }
}
