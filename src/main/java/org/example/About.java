package org.example;

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
    public BasicAnswerHandler exec() {
        return new StandardAnswerHandler("Создатели: Михаил Яскевич, Павел Овчинников");
    }
}
