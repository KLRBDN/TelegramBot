package org.example;

public final class Time{
    private int hours;
    private int minutes;

    public Time(int hours, int minutes) {
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

    public String toString() {
        String timeMinutes;
        String timeHours;
        if (minutes < 10)
            timeMinutes = "0" + minutes;
        else 
            timeMinutes = Integer.toString(minutes);
        if (hours < 10)
            timeHours = "0" + hours;
        else 
            timeHours = Integer.toString(hours);
        return timeHours + ":" + timeMinutes;
    }
}
