package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

public class About implements BotCommand {
    @Override
    public String getDescription() {
        return "Возвращает имена создателей бота";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BasicAnswerHandler exec(Update answer) {
        return new StandardAnswerHandler("Создатели: Михаил Яскевич, Павел Овчинников");
    }
}
