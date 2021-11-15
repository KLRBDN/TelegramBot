package org.example;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

final class BotHelper {
    private static AnswerHandler answerHandler;

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
        if (update.getMessage().getText().startsWith("/") 
            || answerHandler == null)
            answerHandler = new StandartAnswerHandler(null);
        answerHandler = answerHandler.handle(update, botCommands);

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(answerHandler.getLastBotMessage());
        
        return message;
    }
}
