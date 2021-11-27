package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class BasicAnswerHandler {
    private AnswerHandler handler;
    private SendMessage botRequest;

    public BasicAnswerHandler(SendMessage botRequest, AnswerHandler handler){
        this.handler = handler;
        this.botRequest = botRequest;
    }

    public BasicAnswerHandler(String request, AnswerHandler handler){
        var botRequest = new SendMessage();
        botRequest.setText(request);
        this.botRequest = botRequest;
        this.handler = handler;
    }

    protected BasicAnswerHandler(String request){
        var botRequest = new SendMessage();
        botRequest.setText(request);
        this.botRequest = botRequest;
        this.handler = null;
    }

    public SendMessage getBotRequest() {
        return this.botRequest;
    }

    public BasicAnswerHandler handle(Update answer, Map<String, BotCommand> botCommands) {
        return this.handler.handle(answer);
    }
}
