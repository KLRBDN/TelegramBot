package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface AnswerHandler {
    BasicAnswerHandler handle(Update answer);
}
