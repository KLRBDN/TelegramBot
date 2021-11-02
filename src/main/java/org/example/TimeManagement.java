package org.example;

public class TimeManagement implements BotCommand {

    @Override
    public String getDescription() {
        return "Оптимально распределяет ваше время на нужные задачи";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String exec() {
        return null;
    }
}
