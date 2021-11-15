package org.example;

import javax.management.InvalidAttributeValueException;

public final class Time{
    private int hours;
    private int minutes;

    public Time(int hours, int minutes) throws InvalidAttributeValueException {
        if (hours < 0 || hours > 24 || minutes < 0 || minutes > 60)
            throw new InvalidAttributeValueException(hours + ":" + minutes + " - wrong time");
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public Boolean gt(Time time){
        return this.hours > time.getHours() 
            || this.hours == time.getHours() && this.minutes > time.getMinutes();
    }

    public Boolean lt(Time time){
        return this.hours < time.getHours() 
            || this.hours == time.getHours() && this.minutes < time.getMinutes();
    }

    public Boolean eq(Time time){
        return this.hours == time.getHours() && this.minutes == time.getMinutes();
    }
}
