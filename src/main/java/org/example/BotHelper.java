package org.example;

import java.util.Map;
import java.util.function.Consumer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

final class BotHelper {

    public static void fillBotCommandsDictionary(Map<String, BotCommand> botCommands)
    {    
        Consumer<BotCommand> putter = (command) -> { 
            if (command != null && !botCommands.containsKey(command.getName()))
                botCommands.put(command.getName(), command);
        };        
        putter.accept(new About());
        putter.accept(new TimeManagement());
        putter.accept(new Help(botCommands));
    }

    public static SendMessage FormMessage(Update update, Map<String, BotCommand> botCommands)
    {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        if (botCommands.containsKey(update.getMessage().getText()))
        {
            message.setText(botCommands.get(update.getMessage().getText()).exec());
        }
        else
        {
            message.setText("Сам такой");
        }
        return message;
    }
}
