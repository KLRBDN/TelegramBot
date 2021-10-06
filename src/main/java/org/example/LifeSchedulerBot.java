package org.example;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class LifeSchedulerBot extends TelegramLongPollingBot {
    private static LifeSchedulerBot instance;
    private final String botUsername;
    private final String botToken;
    private final Map<String, BotCommand> botCommands;

    private LifeSchedulerBot(String botUsername, String botToken) {
        super();
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.botCommands = new HashMap<String, BotCommand>();
        BotHelper.fillBotCommandsDictionary(botCommands);
    }

    public static LifeSchedulerBot getInstance(){
        if (instance == null){
            instance = new LifeSchedulerBot("LifeScheduler_Bot", System.getenv("LifeSchedulerBotToken"));
        }
        return instance;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
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

