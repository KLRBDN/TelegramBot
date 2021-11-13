package org.example;

public class Month {
    public DayInterface[] days;
    
    public int mountNumber;
    
    public Month(int mountNumber, int daysCount) {
        this.mountNumber = mountNumber;
        this.days = new DayInterface[daysCount];
    }
}
