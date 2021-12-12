package org.example;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class GetClosestTasksTest {
    @Test
    public void noClosestTasksTest(){
        var userAnswer = RepetitiveTasksTest.makeUserAnswer("/closesttask");
        var botRequestWithClosestTasks =
                new GetClosestTasks(YearsDataBase.getInstance()).exec(userAnswer);
        assert botRequestWithClosestTasks instanceof StandardBotRequest;
        Assertions.assertEquals("No tasks for next week",
                ((SendMessage)botRequestWithClosestTasks.getRequestMessage()).getText());
    }

    @Test
    public void haveClosestTasksTest(){
        var tasksAdder = new AddTaskCommandTest();
        tasksAdder.addTaskCommandTest(String.format("%s 9:00 - 10:00", Day.getTodayDate()), "1", true);
        tasksAdder.addTaskCommandTest(String.format("%s 10:00 - 11:00", Day.getTodayDate()), "2", true);
        tasksAdder.addTaskCommandTest(String.format("%s 11:00 - 12:00", Day.getTodayDate()), "3", true);
        var userAnswer = RepetitiveTasksTest.makeUserAnswer("/closesttask");
        var botRequestWithClosestTasks =
                new GetClosestTasks(YearsDataBase.getInstance()).exec(userAnswer);
        assert botRequestWithClosestTasks instanceof StandardBotRequest;
        Assertions.assertEquals("‼️1: 12.12.2021 09:00-10:00\n" +
                        "‼️2: 12.12.2021 10:00-11:00\n" +
                        "‼️3: 12.12.2021 11:00-12:00\n",
                ((SendMessage)botRequestWithClosestTasks.getRequestMessage()).getText());
    }
}
