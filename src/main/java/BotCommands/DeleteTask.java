package BotCommands;

import DialogueHandling.BotRequest;
import DateStructure.Day;
import DialogueHandling.KeyboardConfiguration;
import DialogueHandling.StandardBotRequest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DeleteTask implements BotCommand {
    protected String taskName;
    protected String date;

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
            message = KeyboardConfiguration.createMessageWithCalendarKeyboard(answer.getMessage().getChatId());
        else
            message = KeyboardConfiguration.createMessageWithCalendarKeyboard(answer.getCallbackQuery().getMessage().getChatId());
        return new BotRequest(message, this::askTaskName);
    }

    protected BotRequest askTaskName(Update answer){
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

    protected BotRequest setTaskName(Update answer){
        taskName = answer.getMessage().getText();
        if (deleteTask(date, taskName))
            return new StandardBotRequest("Task was successfully deleted!");
        return new StandardBotRequest("There is no such task");
    }

    protected Boolean deleteTask(String date, String name) {
        return Day.getDay(date).deleteTask(name);
    }
}
