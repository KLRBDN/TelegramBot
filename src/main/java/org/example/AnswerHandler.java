package org.example;

import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface AnswerHandler {
    public String getLastBotMessage();
    public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands);
    
}
