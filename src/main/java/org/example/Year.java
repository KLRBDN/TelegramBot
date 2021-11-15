package org.example;

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
            if (dayNumber < 1 || dayNumber > days.length)
                return null;
            return days[dayNumber-1] != null 
                ? days[dayNumber-1] 
                : createDay(dayNumber);
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
        if (monthNumber < 1 || monthNumber > 12)
            return null;
        return months[monthNumber-1] == null 
            ? createMonth(monthNumber) 
            : months[monthNumber-1];
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
