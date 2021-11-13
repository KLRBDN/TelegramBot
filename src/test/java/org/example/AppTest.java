package org.example;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AppTest 
{
    @Test
    public void fillBotCommandsDictionaryTest()
    {
        var expected = new HashMap<String, BotCommand>();
        var about = new About();
        var help = new Help(expected);
        var commandsToPut = Arrays.asList(
            about,
            help
        );

        BotHelper.fillBotCommandsDictionary(expected, commandsToPut);

        var actual = new HashMap<String, BotCommand>();
        actual.put(about.getName(), about);
        actual.put(help.getName(), help);
        assertEquals(expected, actual);
    }

    @Test
    public void addingSameCommandsWontWorkAndWontCrashProgram()
    {
        var expected = new HashMap<String, BotCommand>();
        var about = new About();
        var commandsToPut = Arrays.asList(
            (BotCommand)about,
            (BotCommand)about
        );
        var actual = new HashMap<String, BotCommand>();
        actual.put(about.getName(), about);

        BotHelper.fillBotCommandsDictionary(expected, commandsToPut);

        assertEquals(expected, actual);
    }

    @Test
    public void adddingNullCommandWontCrashProgram()
    {
        var expected = new HashMap<String, BotCommand>();
        var commandsToPut = Arrays.asList(
            (BotCommand)null
        );

        BotHelper.fillBotCommandsDictionary(expected, commandsToPut);
        
        assertEquals(expected, new HashMap<String, BotCommand>());
    }

    @Test
    public void formMessageTest()
    {
        var chat = new Chat();
        chat.setId(1L);
        var myMessage = new Message();
        myMessage.setText("/about");
        myMessage.setChat(chat);
        var update = new Update();
        update.setMessage(myMessage);

        var botCommands = new HashMap<String, BotCommand>();
        var about = new About();
        botCommands.put(about.getName(), about);

        var actual = BotHelper.FormMessage(update, botCommands);

        var expected = new SendMessage();
        expected.setText(about.exec());
        expected.setChatId(chat.getId().toString());

        assertEquals(expected, actual);
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
