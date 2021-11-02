package org.example;

public class AddTask implements BotCommand {

    @Override
    public String getDescription() {
        return "Добавляет задание в выбранный день в расписание";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }
    
    // Не сделано
    @Override
    public String exec() {
        return "";
    }
    
}
