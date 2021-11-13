package org.example;

import java.util.HashMap;
import java.util.Map;

public class YearsDateBase {
    public Map<String, Year> years;

    public YearsDateBase() {
        this.years = new HashMap<String, Year>();
    }

    public Map<String, Year> getYears(){
        return this.years;
    }
}
