package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

final class BotHelper {
    private static BasicAnswerHandler answerHandler;

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
            answerHandler = new StandardAnswerHandler(null);
        answerHandler = answerHandler.handle(update, botCommands);

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(answerHandler.getLastBotMessage());
        
        return message;
    }

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        var yearsDataBase = YearsDataBase.getInstance();
        var date = Day.getTodayDate();
        var dateSplitted = date.split("\\.");
        var days = yearsDataBase
                .getYear(Integer.parseInt(dateSplitted[2]))
                .getMonth(Integer.parseInt(dateSplitted[1]))
                .getAllDays();
        List<InlineKeyboardButton> buttonRow = new ArrayList<>(7);
        List<List<InlineKeyboardButton>> buttonRowList = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            var managerButton
//        }
        for (int i = 1; i <= days.length; i++) {
            var button = new InlineKeyboardButton();
            button.setText(Integer.toString(i));
            button.setCallbackData(i + "." + dateSplitted[1] + "." + dateSplitted[2]);
            buttonRow.add(button);
            if (i % 7 == 0) {
                buttonRowList.add(buttonRow);
                buttonRow = new ArrayList<>(7);
            }
        }
        if (!buttonRow.isEmpty()) {
            for (int i = buttonRow.size(); i < 7; i++) {
                var emptyButton = new InlineKeyboardButton();
                emptyButton.setText(" ");
                emptyButton.setCallbackData("null");
                buttonRow.add(emptyButton);
            }
            buttonRowList.add(buttonRow);
        }
        inlineKeyboardMarkup.setKeyboard(buttonRowList);
        var message = new SendMessage();
        message.setChatId(Long.toString(chatId));
        message.setText("Choose the date of task to complete");
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
}
