package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class GetTasks implements BotCommand {

    @Override
    public String getDescription() {
        return "Return all active tasks of some date";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public AnswerHandler exec() {
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "Write the date in which you want to view tasks in format '10.10.2021'" +
                        " or 'today' if you want to view tasks for today";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return processAnswer(answer);
            }
        };
    }

    private AnswerHandler processAnswer(Update answer){
        var line = answer.getMessage().getText();
        var tasks = processDateAndGetTasks(line);
        if (tasks != null) {
            var strBuilder = new StringBuilder();
            for (Task task : tasks)
                strBuilder.append(task.name)
                          .append(": ")
                          .append(task.timeInterval.toString())
                          .append("\n");
            if (strBuilder.length() == 0)
                return new StandardAnswerHandler("No tasks for this date");
            return new StandardAnswerHandler(strBuilder.toString());
        }
        return new AnswerHandler() {
            public String getLastBotMessage(){
                return "Error: Wrong date, please try again and write the date" +
                        " of day where you want to view tasks in format: '10.10.2021'";
            }

            public AnswerHandler handle(Update answer, Map<String, BotCommand> botCommands){
                return processAnswer(answer);
            }
        };
    }

    private ArrayList<Task> processDateAndGetTasks(String date) {
        int day, month, year;
        var splitted = date.split("\\.");
        if (splitted.length != 3 && splitted.length != 1)
            return null;
        if (splitted[0].equalsIgnoreCase("today"))
            return getTodayTasks();
        else if (splitted.length == 3) {
            day = Integer.parseInt(splitted[0]);
            month = Integer.parseInt(splitted[1]);
            year = Integer.parseInt(splitted[2]);
            try {
                return getDayTasks(day, month, year);
            } catch (NullPointerException e) {
                return null;
            }
        }
        return null;
    }

    public static ArrayList<Task> getTodayTasks() {
        return Day.getToday().getTasks();
    }

    public static ArrayList<Task> getDayTasks(LocalDate date) {
        return Day.getDay(date).getTasks();
    }

    public static ArrayList<Task> getDayTasks(int day, int month, int year) {
        var date = Day.getDay(day, month, year);
        if (date != null)
            return date.getTasks();
        return null;
    }
}
