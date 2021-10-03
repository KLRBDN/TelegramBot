package org.example;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class LifeSchedulerBot extends TelegramLongPollingBot {
    private static LifeSchedulerBot instance;
    private final String BOT_USERNAME;
    private final String BOT_TOKEN;
    private final Map<String, BotCommand> BOT_COMMANDS;

    private LifeSchedulerBot(String botUserName, String botToken) {
        super();
        this.BOT_USERNAME = botUserName;
        this.BOT_TOKEN = botToken;
        this.BOT_COMMANDS = new HashMap<String, BotCommand>();
        fillBotCommandsDictionary();
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
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            if (BOT_COMMANDS.containsKey(update.getMessage().getText()))
            {
                try {
                    message.setText(BOT_COMMANDS.get(update.getMessage().getText()).exec());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                message.setText("Сам такой");
            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public void fillBotCommandsDictionary()
    {
        About about = new About();
        TimeManagement timeManagement = new TimeManagement();
        Help help = new Help(BOT_COMMANDS);
        BOT_COMMANDS.put(about.getName(), about);
        BOT_COMMANDS.put(help.getName(), help);
        BOT_COMMANDS.put(timeManagement.getName(), timeManagement);
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}

