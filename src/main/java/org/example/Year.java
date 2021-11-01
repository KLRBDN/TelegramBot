package org.example;

public class Year {
    private Month[] months;
    
    public Year(Boolean isLeap) {
        months = new Month[12];
    }

    public Year() {
        months = new Month[12];
    }

    public Month[] getMonths(){
        return months;
    }
}
