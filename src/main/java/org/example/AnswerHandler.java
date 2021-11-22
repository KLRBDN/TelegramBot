package org.example;

import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface AnswerHandler {
    String getLastBotMessage();

    AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands);
}
