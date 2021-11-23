package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class BasicAnswerHandler {
    private String lastBotMessage;
    private AnswerHandler handler;

    public BasicAnswerHandler(String lastBotMessage, AnswerHandler handler){
        this.lastBotMessage = lastBotMessage;
        this.handler = handler;
    }

    public String getLastBotMessage() {
        return lastBotMessage;
    }

    public BasicAnswerHandler handle(Update answer, Map<String, BotCommand> botCommands) {
        return this.handler.handle(answer);
    }
}
