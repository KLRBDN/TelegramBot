package org.example;

import java.time.LocalDate;
import java.time.Period;

public class RepetitiveDate {
    private String pushedButtonText;
    private final LocalDate startDay;
    private final Boolean[] pickedDaysOfWeek;
    private final Integer repeatPeriod;
    private final Integer timeUnitIndex;
    private final Integer dayOfMonth;
    private final Integer weekNumber;
    private final MatchFinder[] matchFinders;

    public RepetitiveDate(String pushedButtonText, LocalDate startDay, Boolean[] pickedDaysOfWeek,
                          Integer repeatPeriod, Integer timeUnitIndex, Integer dayOfMonth, Integer weekNumber){
        this.pushedButtonText = pushedButtonText;
        this.startDay = startDay;
        this.pickedDaysOfWeek = pickedDaysOfWeek;
        this.repeatPeriod = repeatPeriod;
        this.timeUnitIndex = timeUnitIndex;
        this.dayOfMonth = dayOfMonth;
        this.weekNumber = weekNumber;
        this.matchFinders = new MatchFinder[] {
                this::repetitiveDayMatch,
                this::repetitiveWeekMatch,
                this::repetitiveMonthMatch,
                this::repetitiveYearMatch
        };
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
                && oneOfTwoFormatsMatch(date);
    }

    private Boolean repetitiveYearMatch(LocalDate date){
        var yearsPassedSinceStart = Period.between(startDay, date).getYears();
        return yearsPassedSinceStart % (repeatPeriod+1) == 0
                && startDay.getMonth() == date.getMonth()
                && oneOfTwoFormatsMatch(date);
    }

    private Boolean oneOfTwoFormatsMatch(LocalDate date){
        if (dayOfMonth != null)
            return dayOfMonth == date.getDayOfMonth();
        return weekNumber != null
                && startDay.getDayOfWeek() == date.getDayOfWeek()
                && weekNumber == getWeekNumber(date);
    }

    public static int getWeekNumber(LocalDate date){
        var countOfPreviousMonthDaysInFirstWeek = LocalDate
                .of(date.getYear(), date.getMonthValue(), 1)
                .getDayOfWeek()
                .getValue() - 1;
        return ((countOfPreviousMonthDaysInFirstWeek  + date.getDayOfMonth() - 1)/7) + 1;
    }

    public Boolean match(LocalDate date){
        return matchFinders[timeUnitIndex].match(date);
    }
}
