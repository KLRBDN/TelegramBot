package org.example;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTaskCommandTest {
    @Test
    public void addTaskCommandTest(){
        addTaskCommandTest("10.10.2021 9:00 - 10:00", true);
        addTaskCommandTest("1.1.2021 9:0 - 10:0", true);
        addTaskCommandTest("29.02.2024 9:00 - 10:00", true);
        addTaskCommandTest("29.02.2021 9:00 - 10:00", false);
        addTaskCommandTest("10.10.2020 9:00 - 10:00", false);

        addTaskCommandTest("10.10.2021 10:00 - 9:00", false);
        addTaskCommandTest("10.10.2021 -1:00 - 10:00", false);
        addTaskCommandTest("10.10.2021 9:70 - 10:00", false);
        addTaskCommandTest("10.10.2021 9:00 - 25:00", false);
        addTaskCommandTest("10.10.2021 9:00", false);

        addTaskCommandTest("10.10.2021 9:00-10:00", false);
        addTaskCommandTest("10.10.2021 9:00- 10:00", false);
        addTaskCommandTest("10.10.2021 9:00 : 10:00", false);
        addTaskCommandTest("10.2021 9:00 - 10:00", false);
        addTaskCommandTest("-10.10.2021 9:00 - 10:00", false);

        addTaskCommandTest("10.20.2021 9:00 - 10:00", false);
        addTaskCommandTest("10.20.2021 9:00 - 10:00", false);
    }

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
}
