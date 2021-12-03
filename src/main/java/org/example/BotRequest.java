package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class BotRequest {
    private AnswerHandler handler;
    private SendMessage requestMessage;

    public BotRequest(SendMessage botRequest, AnswerHandler handler){
        this.handler = handler;
        this.requestMessage = botRequest;
    }

    public BotRequest(String request, AnswerHandler handler){
        var requestMessage = new SendMessage();
        requestMessage.setText(request);
        this.requestMessage = requestMessage;
        this.handler = handler;
    }

    protected BotRequest(String request){
        var requestMessage = new SendMessage();
        requestMessage.setText(request);
        this.requestMessage = requestMessage;
        this.handler = null;
    }

    public void setChatId(Update update){
        if (update.hasMessage() || update.hasCallbackQuery()){
            requestMessage.setChatId(
                    Long.toString(update.hasMessage()
                            ? update.getMessage().getChatId()
                            : update.getCallbackQuery().getMessage().getChatId()));
        }
    }

    public SendMessage getRequestMessage() {
        return this.requestMessage;
    }

    public BotRequest handle(Update answer, Map<String, BotCommand> botCommands) {
        return this.handler.handle(answer);
    }
}
