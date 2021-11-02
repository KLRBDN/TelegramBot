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
        var timeManagement = new TimeManagement();
        var help = new Help(expected);
        var commandsToPut = Arrays.asList(
            about,
            timeManagement,
            help
        );

        BotHelper.fillBotCommandsDictionary(expected, commandsToPut);

        var actual = new HashMap<String, BotCommand>();
        actual.put(about.getName(), about);
        actual.put(timeManagement.getName(), timeManagement);
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

    // Все три ниже пока что не работают
    @Test
    public void addingTwoTasksWithSameNameWontWork() {
        var day = new Day();
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test", "this is test task"));
        day.tryAddTask(new Task(new Time(2, 0), new Time(3, 0), TaskType.overlapping, "test", "this is second test task"));
        var expected = Arrays.asList(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test", "this is test task"));
        assertEquals(expected, day.getTasks());
    }

    @Test
    public void addingTwoNonOverlappingTasksInSameTimeWontWork() {
        var day = new Day();
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.nonOverlapping, "test1", "test"));
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.nonOverlapping, "test2", "test"));
        var expected = Arrays.asList(new Task(new Time(0, 0), new Time(1, 0), TaskType.nonOverlapping, "test1", "test"),
                                     new Task(new Time(0, 0), new Time(1, 0), TaskType.nonOverlapping, "test2", "test"));
        assertEquals(expected, day.getTasks());
    }

    @Test
    public void addingTwoOverlappingTasksInSameTimeWorks() {
        var day = new Day();
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test1", "test"));
        day.tryAddTask(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test2", "test"));
        var expected = Arrays.asList(new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test1", "test"),
                                     new Task(new Time(0, 0), new Time(1, 0), TaskType.overlapping, "test2", "test"));
        assertEquals(expected, day.getTasks());
    }
}
