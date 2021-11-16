package org.example;

import java.time.LocalDate;
import java.util.ArrayList;

public class GetTasks implements BotCommand {

    @Override
    public String getDescription() {
        return "Возвращает все активные задачи";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public AnswerHandler exec() {
        var strBuilder = new StringBuilder();
        for (Task task : getTodayTasks())
            strBuilder.append(task.name + ": " + task.timeInterval.toString() + "\n");
        return new StandartAnswerHandler(strBuilder.toString());    
    }

    public static ArrayList<Task> getTodayTasks() {
        return Day.getToday().getTasks();
    }

    public static ArrayList<Task> getDayTasks(LocalDate date) {
        return Day.getDay(date).getTasks();
    }

    public static ArrayList<Task> getDayTasks(int day, int month, int year) {
        return Day.getDay(day, month, year).getTasks();
    }
}
