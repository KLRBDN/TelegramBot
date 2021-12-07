package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DeleteTask implements BotCommand {
    private String taskName;
    private String date;

    public DeleteTask() {
        super();
    }

    @Override
    public String getDescription() {
        return "Удаляет задачу из списка активных задач";
    }

    @Override
    public String getName() {
        return "/delete";
    }

    @Override
    public BotRequest exec(Update answer) {
        SendMessage message;
        if (answer.hasMessage())
            message = KeyboardConfiguration.createCalendarKeyboard(answer.getMessage().getChatId());
        else
            message = KeyboardConfiguration.createCalendarKeyboard(answer.getCallbackQuery().getMessage().getChatId());
        return new BotRequest(message, this::askTaskName);
    }

    private BotRequest askTaskName(Update answer){
        date = answer.getCallbackQuery().getData();
        // Зачем это здесь было?
//        if (date.equals("Next") || date.equals("Previous")) {
//            keyboardConfig.trySwitchMonth(date);
//            return exec(answer);
//        }
        var botRequest = new SendMessage();
        botRequest.setText("Write name for your task");
        botRequest.setChatId(Long.toString(answer.getCallbackQuery().getMessage().getChatId()));
        return new BotRequest(botRequest, this::setTaskName);
    }

    private BotRequest setTaskName(Update answer){
        taskName = answer.getMessage().getText();
        if (deleteTask(date, taskName))
            return new StandardBotRequest("Task was successfully deleted!");
        return new StandardBotRequest("There is no such task");
    }

    private Boolean deleteTask(String date, String name) {
        try {
            return Day
                    .getDay(date)
                    .deleteTask(name);
        } catch (NullPointerException e) {
            return false;
        }
    }

}
