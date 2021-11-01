package org.example;

public final class TimeInterval {
    private Time start;
    private Time end;

    public TimeInterval(Time start, Time end) {
        this.start = start;
        this.end = end;
    }

    public Time getStart(){
        return start;
    }

    public Time getEnd(){
        return end;
    }
}
