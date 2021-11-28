package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CompleteTask implements BotCommand {
    private String taskName;
    private String date;

    public CompleteTask(YearsDataBase yearsDataBase) {
        super();
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
    public BotRequest exec(Update answer) {
        var message = BotHelper.sendInlineKeyBoardMessage(answer.getMessage().getChatId());
        return new BotRequest(message, this::askTaskName);
    }

    private BotRequest askTaskName(Update answer){
        date = answer.getCallbackQuery().getData();
        var botRequest = new SendMessage();
        botRequest.setText("Write name for your task");
        botRequest.setChatId(Long.toString(answer.getCallbackQuery().getMessage().getChatId()));
        return new BotRequest(botRequest, this::setTaskName);
    }

    private BotRequest setTaskName(Update answer){
        taskName = answer.getMessage().getText();
        if (completeTask(date, taskName))
            return new StandardBotRequest("Task was successfully completed!");
        return new StandardBotRequest("There is no such task");
    }

    private Boolean completeTask(String date, String name) {
        try {
            return Day
                    .getDay(date)
                    .completeTask(name);
        } catch (NullPointerException e) {
            return false;
        }
    }
  
}
