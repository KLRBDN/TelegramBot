package org.example;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

final class BotHelper {
    private static BotRequest answerHandler;

    public static void fillBotCommandsDictionary(Map<String, BotCommand> mapToFill, List<BotCommand> commandsToPut)
    {    
        Consumer<BotCommand> putter = (command) -> { 
            if (command != null && !mapToFill.containsKey(command.getName()))
                mapToFill.put(command.getName(), command);
        };
        for (var command : commandsToPut){
            putter.accept(command);
        }
    }

    public static SendMessage FormMessage(Update update, Map<String, BotCommand> botCommands)
    {
        if (!update.hasCallbackQuery()) {
            if (update.getMessage().getText().startsWith("/")
                    || answerHandler == null)
                answerHandler = new StandardBotRequest("");
        }
        answerHandler = answerHandler.handle(update, botCommands);

        answerHandler.setChatId(update);
        return answerHandler.getRequestMessage();
    }
}
