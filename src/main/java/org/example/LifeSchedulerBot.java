package org.example;

import java.io.Serializable;
import java.util.ArrayList;
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
    private final YearsDataBase yearsDataBase;
    private final KeyboardConfiguration keyboardConfig;
    private final Map<String, BotCommand> botCommands;
    public static Integer messageId;

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
                new DeleteTask(),
                new GetClosestTasks(yearsDataBase)
        ));
    }

    public ArrayList<BotCommand> getBotCommands() {
        var arrayList = new ArrayList<BotCommand>();
        arrayList.addAll(botCommands.values());
        return arrayList;
    }

    public static LifeSchedulerBot getInstance() {
        if (instance == null) {
            instance = new LifeSchedulerBot("LifeScheduler_Bot", System.getenv("LifeSchedulerBotToken"));
        }
        return instance;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message;
        if ((update.hasMessage() && update.getMessage().hasText()) || (update.hasCallbackQuery() &&
                !update.getCallbackQuery().getData().equals("No data"))) {
            try {
                var callData = update.hasCallbackQuery() ? update.getCallbackQuery().getData() : null;
                if (callData != null && (callData.equals("Next") || callData.equals("Previous") || callData.equals("Past"))) {
                    if (callData.equals("Next") || callData.equals("Previous")) {
                        if (KeyboardConfiguration.trySwitchMonth(callData, false)) {
                            EditMessageReplyMarkup editedMessage = new EditMessageReplyMarkup();
                            var sendMessage = KeyboardConfiguration.createCalendarKeyboard(
                                    update.getCallbackQuery().getMessage().getChatId()
                            );
                            editedMessage.setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup());
                            editedMessage.setChatId(sendMessage.getChatId());
                            editedMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                            execute(editedMessage);
                        } else {
                            var errorMessage = new SendMessage();
                            errorMessage.setText("Error: Can not proceed to a past date");
                            errorMessage.setChatId(Long.toString(update.getCallbackQuery().getMessage().getChatId()));
                            execute(errorMessage);
                        }
                    } else {
                        var errorMessage = new SendMessage();
                        errorMessage.setText("Error: Can not choose past date");
                        errorMessage.setChatId(Long.toString(update.getCallbackQuery().getMessage().getChatId()));
                        execute(errorMessage);
                    }
                } else {
                    execute(KeyboardConfiguration.createCommandKeyboard(update.getMessage().getChatId()));
                    message = (Message)(execute(BotHelper.FormMessage(update, botCommands)));
                    messageId = message.getMessageId();
                }
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

