package org.example;

import java.text.DateFormatSymbols;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.w3c.dom.Text;

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
