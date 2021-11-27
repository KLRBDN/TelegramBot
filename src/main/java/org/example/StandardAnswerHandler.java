package org.example;

import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Update;

public class StandardAnswerHandler extends BasicAnswerHandler{

    public StandardAnswerHandler(String request) {
        super(request);
    }

    @Override
    public BasicAnswerHandler handle(Update answer, Map<String, BotCommand> botCommands) {
        return botCommands.containsKey(answer.getMessage().getText())
            ? botCommands.get(answer.getMessage().getText()).exec(answer)
            : new StandardAnswerHandler("Wrong command");
    }
}
