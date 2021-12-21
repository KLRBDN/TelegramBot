package DateStructure;

import DateStructure.RepetitiveDate;
import DateStructure.TimeUnit;
import org.junit.Test;

import java.time.LocalDate;

public class RepetitiveDateTest {
    @Test
    public void everyDayTest(){
        var repetitiveDate = new RepetitiveDate(
                new Boolean[] {false, false, false, false, false, false, false},
                new Boolean[] {false, false, false, false},
                LocalDate.of(2021, 12, 10),
                1, TimeUnit.day);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        for (var i = 1; i < 20; i++)
            assert repetitiveDate.match(LocalDate.of(2022, 1, i));
    }

    @Test
    public void everyMondayTest(){
        var repetitiveDate = new RepetitiveDate(
                new Boolean[] {true, false, false, false, false, false, false},
                new Boolean[] {false, false, false, false},
                LocalDate.of(2021, 12, 10),
                1, TimeUnit.week);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 13));
        assert repetitiveDate.match(LocalDate.of(2022, 1, 3));

        assert !repetitiveDate.match(LocalDate.of(2021, 12, 14));
        assert !repetitiveDate.match(LocalDate.of(2022, 1, 2));
    }

    @Test
    public void everyMondayAndFridayTest(){
        var repetitiveDate = new RepetitiveDate(
                new Boolean[] {/*monday*/true, false, false, false, /*friday*/true, false, false},
                new Boolean[] {false, false, false, false},
                LocalDate.of(2021, 12, 10),
                1, TimeUnit.week);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 13));
        assert repetitiveDate.match(LocalDate.of(2021, 12, 17));
        assert repetitiveDate.match(LocalDate.of(2022, 1, 3));
        assert repetitiveDate.match(LocalDate.of(2022, 1, 7));

        assert !repetitiveDate.match(LocalDate.of(2021, 12, 15));
        assert !repetitiveDate.match(LocalDate.of(2022, 1, 9));
    }

    @Test
    public void everyMonthTest(){
        var repetitiveDate = makeRepetitiveDate(new Boolean[] {true, false, false, false}, TimeUnit.month);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 1, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 2, 10));

        assert !repetitiveDate.match(LocalDate.of(2021, 12, 11));
        assert !repetitiveDate.match(LocalDate.of(2022, 1, 9));


        repetitiveDate = makeRepetitiveDate(new Boolean[] {false, true, false, false}, TimeUnit.month);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 1, 7));
        assert repetitiveDate.match(LocalDate.of(2022, 2, 11));

        assert !repetitiveDate.match(LocalDate.of(2022, 1, 10));
        assert !repetitiveDate.match(LocalDate.of(2022, 2, 10));


        repetitiveDate = makeRepetitiveDate(new Boolean[] {true, true, false, false}, TimeUnit.month);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 1, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 2, 10));
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 1, 7));
        assert repetitiveDate.match(LocalDate.of(2022, 2, 11));

        assert !repetitiveDate.match(LocalDate.of(2021, 12, 11));
        assert !repetitiveDate.match(LocalDate.of(2022, 1, 9));
    }

    @Test
    public void everyYearTest(){
        var repetitiveDate = makeRepetitiveDate(new Boolean[] {false, false, true, false}, TimeUnit.year);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2023, 12, 10));

        assert !repetitiveDate.match(LocalDate.of(2021, 12, 11));
        assert !repetitiveDate.match(LocalDate.of(2022, 1, 10));


        repetitiveDate = makeRepetitiveDate(new Boolean[] {false, false, false, true}, TimeUnit.year);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 12, 9));
        assert repetitiveDate.match(LocalDate.of(2023, 12, 8));

        assert !repetitiveDate.match(LocalDate.of(2022, 12, 10));
        assert !repetitiveDate.match(LocalDate.of(2023, 12, 10));


        repetitiveDate = makeRepetitiveDate(new Boolean[] {false, false, true, true}, TimeUnit.year);
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2023, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2021, 12, 10));
        assert repetitiveDate.match(LocalDate.of(2022, 12, 9));
        assert repetitiveDate.match(LocalDate.of(2023, 12, 8));

        assert !repetitiveDate.match(LocalDate.of(2021, 12, 11));
        assert !repetitiveDate.match(LocalDate.of(2022, 1, 10));
    }

    private RepetitiveDate makeRepetitiveDate(Boolean[] pickedDaysInMonthAndYearFormat, TimeUnit timeUnit){
        return new RepetitiveDate(
                new Boolean[] {false, false, false, false, false, false, false},
                pickedDaysInMonthAndYearFormat,
                LocalDate.of(2021, 12, 10),
                1, timeUnit);
    }
}
