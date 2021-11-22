package org.example;

import javax.management.InvalidAttributeValueException;

public final class TimeInterval {
    private Time start;
    private Time end;

    public TimeInterval(Time start, Time end) throws InvalidAttributeValueException {
        if (start.gt(end) || start.eq(end))
            throw new InvalidAttributeValueException(
                    start + "shouldn't be bigger or equal" + end);
        this.start = start;
        this.end = end;
    }

    public Time getStart(){
        return start;
    }

    public Time getEnd(){
        return end;
    }

    public Boolean intersects(TimeInterval interval) {
        return (this.start.gt(interval.start) || this.start.eq(interval.start))
            && this.start.lt(interval.end)
            || this.end.gt(interval.start)
            && (this.end.lt(interval.end) || this.end.eq(interval.end));
    }

    public String toString() {
        return start.toString() + "-" + end.toString();
    }
}
