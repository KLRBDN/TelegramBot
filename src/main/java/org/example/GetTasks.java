package org.example;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.TimeZone;

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
        var yearsDateBase = YearsDateBase.getInstance();
        var zoneId = TimeZone.getTimeZone("GMT+05:00").toZoneId();
        // var year = YearMonth.now().getYear();
        // var month = YearMonth.now().getMonth().getValue();
        var year = LocalDate.now(zoneId).getYear();
        var month = LocalDate.now(zoneId).getMonthValue();
        var day = LocalDate.now(zoneId).getDayOfMonth();
        for (Task task : yearsDateBase.getYear(year).getMonth(month).getDay(day).getTasks())
            strBuilder.append(task.name + ": " + task.timeInterval.toString() + "\n");
        return new StandartAnswerHandler(strBuilder.toString());    
    }
}
