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
    public BasicAnswerHandler exec(Update answer) {
        var message = BotHelper.sendInlineKeyBoardMessage(answer.getMessage().getChatId());
        return new BasicAnswerHandler(
                "", this::processAnswer, message);
    }

    private BasicAnswerHandler processAnswer(Update answer){
        date = answer.getCallbackQuery().getData();
        var message = new SendMessage();
        message.setText("Write name for your task");
        message.setChatId(Long.toString(answer.getCallbackQuery().getMessage().getChatId()));
        return new BasicAnswerHandler("Write name for your task",
                this::askTaskName, message);
    }

    private BasicAnswerHandler askTaskName(Update answer){
        taskName = answer.getMessage().getText();
        if (processDate(date, taskName))
            return new StandardAnswerHandler("Task was successfully completed!");
        return new StandardAnswerHandler("There is no such task");
    }

    private Boolean processDate(String date, String name) {
        try {
            return Day
                    .getDay(date)
                    .completeTask(name);
        } catch (NullPointerException e) {
            return false;
        }
    }
  
}
