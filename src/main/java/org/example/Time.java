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
        if (minutes == 0)
            timeMinutes = "0" + minutes;
        else if (minutes < 10)
            timeMinutes = minutes + "0";
        else 
            timeMinutes = Integer.toString(minutes);
        if (hours == 0)
            timeHours = "0" + hours;
        else if (hours < 10)
            timeHours = hours + "0";
        else 
            timeHours = Integer.toString(hours);
        return timeHours + ":" + timeMinutes;
    }
}
