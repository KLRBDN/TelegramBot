package org.example;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
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
}
