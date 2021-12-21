package DateStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YearsDataBase {
    private static YearsDataBase instance;
    private final Map<Integer, Year> years;
    public static ArrayList<Object[]> completedTasks = new ArrayList<>();

    private YearsDataBase() {
        this.years = new HashMap<>();
    }

    public static YearsDataBase getInstance(){
        if (instance == null){
            instance = new YearsDataBase();
        }
        return instance;
    }

    public Map<Integer, Year> getAllYears(){
        return this.years;
    }

    public Year getYear(Integer yearNumber){
        if (yearNumber < 100)
            yearNumber = 2000 + yearNumber;
        if (!years.containsKey(yearNumber)){
            if (!tryAddYear(yearNumber))
                return null;
        }
        return years.get(yearNumber);
    }

    private Boolean tryAddYear(Integer yearNumber){
        if (yearNumber < 2021 || years.containsKey(yearNumber))
            return false;
        years.put(yearNumber, new Year(yearNumber));
        return true;
    }

}
