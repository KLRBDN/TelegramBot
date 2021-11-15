package org.example;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTaskCommandTest {
    @ParameterizedTest
    @CsvSource(delimiter='|', value= {
        "'10.10.2021 9:00 - 10:00'|true",
        "'1.1.2021 9:0 - 10:0'|true",
        "'29.02.2024 9:00 - 10:00'|true",
        "'29.02.2021 9:00 - 10:00'|false",
        "'10.10.2020 9:00 - 10:00'|false",
        "'10.10.2021 10:00 - 9:00'|false",
        "'10.10.2021 -1:00 - 10:00'|false",
        "'10.10.2021 9:70 - 10:00'|false",
        "'10.10.2021 9:00 - 25:00'|false",
        "'10.10.2021 9:00'|false",
        "'10.10.2021 9:00-10:00'|false",
        "'10.10.2021 9:00- 10:00'|false",
        "'10.10.2021 9:00 : 10:00'|false",
        "'10.2021 9:00 - 10:00'|false",
        "'-10.10.2021 9:00 - 10:00'|false",
        "'10.20.2021 9:00 - 10:00'|false",
        "'10.20.2021 9:00 - 10:00'|false",
    })
    public void addTaskCommandTest(String dateTime, Boolean ok){
        var chat = new Chat();
        chat.setId(1L);
        var myMessage = new Message();
        myMessage.setText(dateTime);
        myMessage.setChat(chat);
        var answer = new Update();
        answer.setMessage(myMessage);

        var taskCmd = new AddTask(YearsDataBase.getInstance());
        var handler1 = taskCmd.exec();

        assertEquals("write date and time in format: 10.10.2021 9:00 - 10:00", handler1.getLastBotMessage());

        var handler2 = handler1.handle(answer, null);

        if (ok) {
            assert(handler2 instanceof StandartAnswerHandler);
            assertEquals("task was added", handler2.getLastBotMessage());
        }
        else {
            assert(!(handler2 instanceof StandartAnswerHandler));
            assertEquals("write date and time in format: 10.10.2021 9:00 - 10:00", handler2.getLastBotMessage());
        }
        
    }

    @Test
    public void addingTwoTasksWithSameNameWontWork() throws Exception {
        var day = new Day();
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test", "this is test task"));
        day.tryAddTask(new Task(new Time(2, 0), new Time(3, 0), TaskType.overlapping, "test", "this is second test task"));
        assertEquals(day.getTasks().size(), 1);
        assertEquals(day.getTasks().get(0).description, "this is test task");
    }

    @Test
    public void addingTwoNonOverlappingTasksInSameTimeWontWork() throws Exception {
        var day = new Day();
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.nonOverlapping, "first test", "test"));
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.nonOverlapping, "second test", "test"));
        assertEquals(day.getTasks().size(), 1);
        assertEquals(day.getTasks().get(0).name, "first test");
    }

    @Test
    public void addingTwoOverlappingTasksInSameTimeWorks() throws Exception {
        var day = new Day();
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "first test", "test"));
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "second test", "test"));
        assertEquals(day.getTasks().size(), 2);
        assertEquals(day.getTasks().get(0).name, "first test");
        assertEquals(day.getTasks().get(1).name, "second test");
    }
}
