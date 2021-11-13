package org.example;

import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Update;

public class StandartAnswerHandler implements AnswerHandler{
    private String lastBotMessage;

    public StandartAnswerHandler(String lastBotMessage) {
        super();
        this.lastBotMessage = lastBotMessage;
    }

    @Override
    public String getLastBotMessage() {
        return lastBotMessage;
    }

    @Override
    public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands) {
        return botCommands.containsKey(answer.getMessage().getText())
            ? botCommands.get(answer.getMessage().getText()).exec()
            : new StandartAnswerHandler("wrong command");
    }
}
