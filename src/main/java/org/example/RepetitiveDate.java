package org.example;

import java.time.LocalDate;

public class RepetitiveDate {
    private String pushedButtonText;
    private LocalDate startDay;
    private Integer dayOfWeek;
    private Integer repeatPeriod;
    private Integer timeUnitIndex;

    public RepetitiveDate(String pushedButtonText, LocalDate startDay, Integer dayOfWeek, Integer repeatPeriod, Integer timeUnitIndex){
        this.pushedButtonText = pushedButtonText;
        this.startDay = startDay;
        this.dayOfWeek = dayOfWeek;
        this.repeatPeriod = repeatPeriod;
        this.timeUnitIndex = timeUnitIndex;
    }

    public Boolean match(Day day){
        return false;
    }
}
