package org.example;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTaskCommandTest {
    @Test
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
    public void addTaskCommandTest(String dateTime, Boolean correctFormat){
        var currentChat = new Chat();
        currentChat.setId(1L);
        var messageForUserAnswer = new Message();
        messageForUserAnswer.setText(dateTime);
        messageForUserAnswer.setChat(currentChat);
        var userAnswer = new Update();
        userAnswer.setMessage(messageForUserAnswer);

        var taskCommand = new AddTask(YearsDataBase.getInstance());
        var answerHandler = taskCommand.exec();

        assertEquals("write date and time in format: 10.10.2021 9:00 - 10:00",
                answerHandler.getLastBotMessage());

        answerHandler = answerHandler.handle(userAnswer, null);

        assert(!(answerHandler instanceof StandartAnswerHandler));
        if (!correctFormat){
            assertEquals("write date and time in format: 10.10.2021 9:00 - 10:00",
                    answerHandler.getLastBotMessage());
        }
        else {
            assertEquals("write name for your task", answerHandler.getLastBotMessage());

            userAnswer.getMessage().setText("name");
            answerHandler = answerHandler.handle(userAnswer, null);
            assertEquals("write description for your task", answerHandler.getLastBotMessage());

            userAnswer.getMessage().setText("description");
            answerHandler = answerHandler.handle(userAnswer, null);
            assertEquals("write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                    answerHandler.getLastBotMessage());

            userAnswer.getMessage().setText("wrong answer");
            answerHandler = answerHandler.handle(userAnswer, null);
            assert(!(answerHandler instanceof StandartAnswerHandler));
            assertEquals("write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                    answerHandler.getLastBotMessage());

            userAnswer.getMessage().setText("3");
            answerHandler = answerHandler.handle(userAnswer, null);
            assert(answerHandler instanceof StandartAnswerHandler);
            assertEquals("Task was added", answerHandler.getLastBotMessage());
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
