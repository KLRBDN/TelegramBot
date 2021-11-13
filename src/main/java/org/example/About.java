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
    public String exec() {
        return "Создатели: Михаил Яскевич, Павел Овчинников";
    }
}
