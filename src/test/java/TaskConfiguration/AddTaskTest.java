package TaskConfiguration;

import static org.junit.Assert.assertEquals;

import DateStructure.Day;
import TaskConfiguration.RepetitiveTasks;
import TaskConfiguration.Task;
import TaskConfiguration.TaskType;
import TaskConfiguration.Time;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;

public class AddTaskTest {
    public AddTaskTest(){
        RepetitiveTasks.clearAll();
    }

    @Test
    public void addingTwoTasksWithSameNameWontWork(){
        try{
            var day = new Day(LocalDate.now());
            new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test", "this is test task");
            day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test", "this is test task"));
            day.tryAddTask(new Task(new Time(2, 0), new Time(3, 0), TaskType.overlapping, "test", "this is second test task"));
            Assertions.assertEquals(1, day.getTasks().size());
            Assertions.assertEquals("this is test task", day.getTasks().get(0).description);
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
        assertEquals(1, day.getTasks().size());
        assertEquals("first test", day.getTasks().get(0).name);
    }

    @Test
    public void addingTwoOverlappingTasksInSameTimeWorks() throws Exception {
        var day = new Day(LocalDate.now());
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "first test", "test"));
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "second test", "test"));
        assertEquals(2, day.getTasks().size());
        assertEquals("first test", day.getTasks().get(0).name);
        assertEquals("second test", day.getTasks().get(1).name);
    }
}
