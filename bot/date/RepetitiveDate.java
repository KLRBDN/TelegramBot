package date;

import commands.AddRepetitiveTask;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;

public class RepetitiveDate {
    private final LocalDate startDay;
    private final Boolean[] pickedDaysOfWeek;
    private final Integer repeatPeriod;
    private final TimeUnit timeUnit;
    private final HashMap<TimeUnit, MatchFinder> matchFinders;
    private final Boolean[] pickedDaysInMonthAndYearFormat;

    public RepetitiveDate(AddRepetitiveTask addRepetitiveTaskCmd){
        this.pickedDaysOfWeek = addRepetitiveTaskCmd.getPickedDaysOfWeek();
        this.pickedDaysInMonthAndYearFormat = addRepetitiveTaskCmd.getPickedDaysInMonthAndYearFormat();
        this.startDay = addRepetitiveTaskCmd.getStartDay();
        this.repeatPeriod = addRepetitiveTaskCmd.getRepeatPeriod();
        this.timeUnit = addRepetitiveTaskCmd.getTimeUnit();
        matchFinders = new HashMap<>();
        matchFinders.put(TimeUnit.day, this::repetitiveDayMatch);
        matchFinders.put(TimeUnit.week, this::repetitiveWeekMatch);
        matchFinders.put(TimeUnit.month, this::repetitiveMonthMatch);
        matchFinders.put(TimeUnit.year, this::repetitiveYearMatch);
    }

    public RepetitiveDate(Boolean[] pickedDaysOfWeek, Boolean[] pickedDaysInMonthAndYearFormat,
                          LocalDate startDay, Integer repeatPeriod, TimeUnit timeUnit){
        this.pickedDaysOfWeek = pickedDaysOfWeek;
        this.pickedDaysInMonthAndYearFormat = pickedDaysInMonthAndYearFormat;
        this.startDay = startDay;
        this.repeatPeriod = repeatPeriod-1;
        this.timeUnit = timeUnit;
        matchFinders = new HashMap<>();
        matchFinders.put(TimeUnit.day, this::repetitiveDayMatch);
        matchFinders.put(TimeUnit.week, this::repetitiveWeekMatch);
        matchFinders.put(TimeUnit.month, this::repetitiveMonthMatch);
        matchFinders.put(TimeUnit.year, this::repetitiveYearMatch);
    }

    @FunctionalInterface
    private interface MatchFinder{
        Boolean match(LocalDate date);
    }

    private Boolean repetitiveDayMatch(LocalDate date){
        var daysPassedSinceStart = Period.between(startDay, date).getDays();
        return daysPassedSinceStart % (repeatPeriod+1) == 0;
    }

    private Boolean repetitiveWeekMatch(LocalDate date){
        var daysPassedSinceStart = Period.between(startDay, date).getDays();
        var weeksPassedSinceStart = (daysPassedSinceStart + startDay.getDayOfWeek().getValue()-1)/7;
        return weeksPassedSinceStart % (repeatPeriod+1) == 0
                && pickedDaysOfWeek[date.getDayOfWeek().getValue()-1];
    }

    private Boolean repetitiveMonthMatch(LocalDate date){
        var monthsPassedSinceStart = Period.between(startDay, date).getMonths();
        return monthsPassedSinceStart % (repeatPeriod+1) == 0
                && oneOfTwoFormatsMatch(date, pickedDaysInMonthAndYearFormat[0], pickedDaysInMonthAndYearFormat[1]);
    }

    private Boolean repetitiveYearMatch(LocalDate date){
        var yearsPassedSinceStart = Period.between(startDay, date).getYears();
        return yearsPassedSinceStart % (repeatPeriod+1) == 0
                && startDay.getMonth() == date.getMonth()
                && oneOfTwoFormatsMatch(date, pickedDaysInMonthAndYearFormat[2], pickedDaysInMonthAndYearFormat[3]);
    }

    private Boolean oneOfTwoFormatsMatch(LocalDate date, Boolean dayOfMonthPicked, Boolean dayOfWeekPicked) {
        return dayOfMonthPicked && startDay.getDayOfMonth() == date.getDayOfMonth()
                || dayOfWeekPicked
                    && startDay.getDayOfWeek() == date.getDayOfWeek()
                    && getWeekNumber(startDay) == getWeekNumber(date);
    }

    public static int getWeekNumber(LocalDate date){
        var countOfPreviousMonthDaysInFirstWeek = LocalDate
                .of(date.getYear(), date.getMonthValue(), 1)
                .getDayOfWeek()
                .getValue() - 1;
        return ((countOfPreviousMonthDaysInFirstWeek  + date.getDayOfMonth() - 1)/7) + 1;
    }

    public Boolean match(LocalDate date){
        return matchFinders.get(timeUnit).match(date);
    }
}
