package org.example;

import java.util.Optional;

public class Year {
    public class Month {
        private DayInterface[] days;
        private int monthNumber;
        
        public Month(int monthNumber, int daysCount) {
            this.monthNumber = monthNumber;
            this.days = new DayInterface[daysCount];
        }

        public DayInterface[] getAllDays(){
            return days;
        }

        public int getMonthNumber(){
            return monthNumber;
        }

        public DayInterface getDay(int dayNumber){
            return dayNumber < 1 || dayNumber > days.length 
                    ? null 
                    : Optional
                        .ofNullable(days[dayNumber-1])
                        .orElse(createDay(dayNumber));
        }

        private DayInterface createDay(int dayNumber){
            days[dayNumber-1] = new Day();
            return days[dayNumber-1];
        }
    }
    
    private int number;
    private Boolean isLeap;
    private Month[] months;
    private int[] daysInMonths;

    public Year(int number) {
        this.number = number;
        this.isLeap = number % 4 == 0;
        this.daysInMonths = new int[] { 
            31, 
            isLeap ? 29 : 28,
            31, 30, 31, 30, 31, 31, 30, 31, 30, 31 
        };
        this.months = new Month[12];
    }
    

    public Month[] getAllMonths(){
        return months;
    }

    public Month getMonth(int monthNumber){
        return monthNumber < 1 || monthNumber > 12
                    ? null 
                    : Optional
                        .ofNullable(months[monthNumber-1])
                        .orElse(createMonth(monthNumber));
    }

    public int getNumber(){
        return this.number;
    }

    public Boolean isLeap(){
        return this.isLeap;
    }

    public Month createMonth(int monthNumber){
        months[monthNumber-1] = new Month(monthNumber, daysInMonths[monthNumber-1]);
        return months[monthNumber-1];
    }
}
