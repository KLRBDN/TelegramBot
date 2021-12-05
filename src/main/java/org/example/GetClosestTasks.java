package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

public class GetClosestTasks implements BotCommand {
    private final YearsDataBase yearsDataBase;

    public GetClosestTasks(YearsDataBase yearsDataBase) {
        this.yearsDataBase = yearsDataBase;
    }

    @Override
    public String getDescription() {
        return "Возвращает все ближайшие активные задачи на неделю вперед";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BotRequest exec(Update answer) {
        var strBuilder = new StringBuilder();
        var todayDateSplitted = Day.getTodayDate().split("\\.");
        var day = Integer.parseInt(todayDateSplitted[0]);
        var month = Integer.parseInt(todayDateSplitted[1]);
        var year = Integer.parseInt(todayDateSplitted[2]);
        int monthDayCount = yearsDataBase.getYear(year).getMonth(month).getAllDays().length;
        for (int i = 0; i < 8; i++) {
            if (day > monthDayCount) {
                month++;
                day = 1;
                if (month > 12) {
                    year++;
                    month = 1;
                }
                monthDayCount = yearsDataBase.getYear(year).getMonth(month).getAllDays().length;
            } else if (i != 0)
                day++;
            var tasks = GetTasks.getDayTasks(day, month, year);
            if (tasks != null) {
                var date = day + "." + month + "." + year;
                for (var task : tasks) {
                    if (task.taskType == TaskType.important)
                        strBuilder.append("‼️");
                    strBuilder.append(task.name)
                            .append(": ")
                            .append(date)
                            .append(" ")
                            .append(task.timeInterval)
                            .append("\n");
                }
            }
        }
        if (strBuilder.length() == 0)
            return new StandardBotRequest("No tasks for next week");
        return new StandardBotRequest(strBuilder.toString());
    }
}
