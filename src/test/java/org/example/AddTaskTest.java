package org.example;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;

public class AddTaskTest {
    @Test
    public void addingTwoTasksWithSameNameWontWork(){
        try{
            var day = new Day(LocalDate.now());
            day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test", "this is test task"));
            day.tryAddTask(new Task(new Time(2, 0), new Time(3, 0), TaskType.overlapping, "test", "this is second test task"));
            Assertions.assertEquals(day.getTasks().size(), 1);
            Assertions.assertEquals(day.getTasks().get(0).description, "this is test task");
        }
        catch (InvalidAttributeValueException e){
            assert(Boolean.FALSE);
        }
    }

    @Test
    public void addingTwoNonOverlappingTasksInSameTimeWontWork() throws Exception {
        var day = new Day(LocalDate.now());
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.nonOverlapping, "first test", "test"));
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.nonOverlapping, "second test", "test"));
        assertEquals(day.getTasks().size(), 1);
        assertEquals(day.getTasks().get(0).name, "first test");
    }

    @Test
    public void addingTwoOverlappingTasksInSameTimeWorks() throws Exception {
        var day = new Day(LocalDate.now());
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "first test", "test"));
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "second test", "test"));
        assertEquals(day.getTasks().size(), 2);
        assertEquals(day.getTasks().get(0).name, "first test");
        assertEquals(day.getTasks().get(1).name, "second test");
    }
}
