package org.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class LifeSchedulerBot extends TelegramLongPollingBot {
    private static LifeSchedulerBot instance;
    private final String botUsername;
    private final String botToken;
    private final KeyboardConfiguration keyboardConfig;
    private final YearsDataBase yearsDataBase;
    private final Map<String, BotCommand> botCommands;

    private LifeSchedulerBot(String botUsername, String botToken) {
        super();
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.botCommands = new HashMap<String, BotCommand>();
        this.yearsDataBase = YearsDataBase.getInstance();
        this.keyboardConfig = new KeyboardConfiguration();
        BotHelper.fillBotCommandsDictionary(botCommands, Arrays.asList(
                new About(),
                new AddTask(),
                new Help(botCommands),
                new GetCompletedTasks(),
                new GetTasks(),
                new CompleteTask(),
                new AddRepetitiveTask(),
                new DeleteTask()
        ));
    }

    public static LifeSchedulerBot getInstance(){
        if (instance == null){
            instance = new LifeSchedulerBot("LifeScheduler_Bot", System.getenv("LifeSchedulerBotToken"));
        }
        return instance;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if ((update.hasMessage() && update.getMessage().hasText()) || (update.hasCallbackQuery() &&
                !update.getCallbackQuery().getData().equals("null"))) {
            try {
                var callData = update.hasCallbackQuery() ? update.getCallbackQuery().getData() : null;
                if (callData != null && (callData.equals("Next") || callData.equals("Previous"))) {
                    if (keyboardConfig.trySwitchMonth(callData)) {
                        EditMessageReplyMarkup editedMessage = new EditMessageReplyMarkup();
                        var message = KeyboardConfiguration.sendInlineKeyBoardMessage(
                                update.getCallbackQuery().getMessage().getChatId()
                        );
                        editedMessage.setReplyMarkup((InlineKeyboardMarkup)message.getReplyMarkup());
                        editedMessage.setChatId(message.getChatId());
                        editedMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                        execute(editedMessage);
                    }
                    else {
                        var errorMessage = new SendMessage();
                        errorMessage.setText("Error: Can not proceed to a past date");
                        errorMessage.setChatId(Long.toString(update.getCallbackQuery().getMessage().getChatId()));
                        execute(errorMessage);
                    }
                }
                else
                    execute(BotHelper.FormMessage(update, botCommands));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}

