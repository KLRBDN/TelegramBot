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

    @Test
    public void addRepetitiveTaskCommandTest(){
        addRepetitiveTaskCommandTest("T1 9:00 - 10:00", true);
        addRepetitiveTaskCommandTest("M 9:00 - 10:00", true);
        addRepetitiveTaskCommandTest("S1 9:00 - 10:00", true);

        addRepetitiveTaskCommandTest("M 9:70 - 10:00", false);
        addRepetitiveTaskCommandTest("M -1:00 - 10:00", false);
        addRepetitiveTaskCommandTest("M 11:00 - 10:00", false);
        addRepetitiveTaskCommandTest("M 9:00 : 10:00", false);
        addRepetitiveTaskCommandTest("M 9:00-10:00", false);
    }

    public void addTaskCommandTest(String dateTime, Boolean correctFormat){
        var userAnswer = makeUserAnswer(dateTime);
        var firstRequestText = "Write date and time in format: 10.10.2021 9:00 - 10:00";
        var firstRequestErrorMessage = "Error: Wrong date, please try again" +
                " and write date and time of your task in format: 10.10.2021 9:00 - 10:00";
        checkBotRequests(userAnswer, new AddTask(),
                firstRequestText, firstRequestErrorMessage, correctFormat);
    }

    public void addRepetitiveTaskCommandTest(String dayOfWeekAndTime, Boolean correctFormat){
        var firstRequestText = "Write day of week to add repetitive task (M, T1, W, T2, F, S1, S2) " +
                "and time in format 9:00 - 10:00. Example 'T1 9:00 - 10:00'";
        var userAnswer = makeUserAnswer(dayOfWeekAndTime);
        checkBotRequests(userAnswer, new AddRepetitiveTask(),
                firstRequestText, firstRequestText, correctFormat);
    }

    private void checkBotRequests(Update userAnswer, BotCommand addTaskCommand,
                                  String firstRequestText, String firstRequestErrorMessage, Boolean correctFormat){
        var botRequestAboutTaskInfo = addTaskCommand.exec(userAnswer);

        assertEquals(firstRequestText,
                botRequestAboutTaskInfo.getRequestMessage().getText());

        botRequestAboutTaskInfo = botRequestAboutTaskInfo.handle(userAnswer, null);

        assert(!(botRequestAboutTaskInfo instanceof StandardBotRequest));
        if (!correctFormat){
            assertEquals(firstRequestErrorMessage,
                    botRequestAboutTaskInfo.getRequestMessage().getText());
        }
        else {
            checkBotRequestsFromNameToType(botRequestAboutTaskInfo, userAnswer);
        }
    }

    private Update makeUserAnswer(String botRequestText){
        var currentChat = new Chat();
        currentChat.setId(1L);

        var messageForUserAnswer = new Message();
        messageForUserAnswer.setText(botRequestText);
        messageForUserAnswer.setChat(currentChat);

        var userAnswer = new Update();
        userAnswer.setMessage(messageForUserAnswer);
        return userAnswer;
    }

    private void checkBotRequestsFromNameToType(BotRequest botRequestAboutTaskInfo, Update userAnswer){
        assertEquals("Write name for your task", botRequestAboutTaskInfo.getRequestMessage().getText());

        userAnswer.getMessage().setText("name");
        botRequestAboutTaskInfo = botRequestAboutTaskInfo.handle(userAnswer, null);
        assertEquals("Write description for your task", botRequestAboutTaskInfo.getRequestMessage().getText());

        userAnswer.getMessage().setText("description");
        botRequestAboutTaskInfo = botRequestAboutTaskInfo.handle(userAnswer, null);
        assertEquals("Write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                botRequestAboutTaskInfo.getRequestMessage().getText());

        userAnswer.getMessage().setText("wrong answer");
        botRequestAboutTaskInfo = botRequestAboutTaskInfo.handle(userAnswer, null);
        assert(!(botRequestAboutTaskInfo instanceof StandardBotRequest));
        assertEquals("Error: Wrong value for task type. Please try again and write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                botRequestAboutTaskInfo.getRequestMessage().getText());

        userAnswer.getMessage().setText("3");
        botRequestAboutTaskInfo = botRequestAboutTaskInfo.handle(userAnswer, null);
        assert(botRequestAboutTaskInfo instanceof StandardBotRequest);
        assertEquals("Task was added", botRequestAboutTaskInfo.getRequestMessage().getText());
    }
}
