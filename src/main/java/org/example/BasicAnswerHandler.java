package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class BasicAnswerHandler {
    private String lastBotMessage;
    private AnswerHandler handler;
    private SendMessage message;

    public BasicAnswerHandler(String lastBotMessage, AnswerHandler handler, SendMessage message){
        this.lastBotMessage = lastBotMessage;
        this.handler = handler;
        this.message = message;
    }

    public String getLastBotMessage() {
        return lastBotMessage;
    }

    public SendMessage getMessage() {
        return this.message;
    }

    public BasicAnswerHandler handle(Update answer, Map<String, BotCommand> botCommands) {
        return this.handler.handle(answer);
    }
}
