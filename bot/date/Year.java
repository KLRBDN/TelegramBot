package date;

import java.time.LocalDate;

public class Year {
    public class Month {
        private final Day[] days;
        private final int year;
        private final int monthNumber;
        
        public Month(int year, int monthNumber, int daysCount) {
            this.year = year;
            this.monthNumber = monthNumber;
            this.days = new Day[daysCount];
        }

        public Day[] getAllDays(){
            return days;
        }

        public int getMonthNumber(){
            return monthNumber;
        }

        public Day getDay(int dayNumber){
            if (dayNumber < 1 || dayNumber > days.length)
                return null;
            return days[dayNumber-1] != null 
                ? days[dayNumber-1] 
                : createDay(dayNumber);
        }

        private Day createDay(int dayNumber){
            days[dayNumber-1] = new Day(LocalDate.of(year, monthNumber, dayNumber));
            return days[dayNumber-1];
        }
    }
    
    private final int number;
    private final Boolean isLeap;
    private final Month[] months;
    private final int[] daysInMonths;

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
        months[monthNumber-1] = new Month(
                number, monthNumber, daysInMonths[monthNumber-1]);
        return months[monthNumber-1];
    }
}
