package org.example;

import java.util.HashMap;
import java.util.Map;

public class YearsDateBase {
    private static YearsDateBase instance;
    private Map<Integer, Year> years;

    private YearsDateBase() {
        this.years = new HashMap<Integer, Year>();
    }

    public static YearsDateBase getInstance(){
        if (instance == null){
            instance = new YearsDateBase();
        }
        return instance;
    }

    public Map<Integer, Year> getAllYears(){
        return this.years;
    }

    public Year getYear(Integer yearNumber){
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
