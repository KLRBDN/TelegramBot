package DialogueHandling;

import BotCommands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class BotRequest {
    private AnswerHandler handler;
    private BotApiMethod requestMessage;

    public BotRequest(SendMessage botRequest, AnswerHandler handler) {
        this.requestMessage = botRequest;
        this.handler = handler;
    }

    public BotRequest(EditMessageReplyMarkup botRequest, AnswerHandler handler) {
        this.requestMessage = botRequest;
        this.handler = handler;
    }

    public BotRequest(String request, AnswerHandler handler, Integer messageId) {
        var requestMessage = new EditMessageText();
        requestMessage.setText(request);
        requestMessage.setMessageId(messageId);
        this.requestMessage = requestMessage;
        this.handler = handler;
    }

    public BotRequest(String request, AnswerHandler handler) {
        var requestMessage = new SendMessage();
        requestMessage.setText(request);
        this.requestMessage = requestMessage;
        this.handler = handler;
    }

//    public BotRequest(EditMessageText requestMessage, AnswerHandler answerHandler) {
//        this.requestMessage = requestMessage;
//        this.handler = answerHandler;
//    }

    protected BotRequest(String request) {
        var requestMessage = new SendMessage();
        requestMessage.setText(request);
        this.requestMessage = requestMessage;
        this.handler = null;
    }

    public void setChatId(Update update){
        String chatId = Long.toString(update.hasMessage()
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId());
        if (update.hasMessage() || update.hasCallbackQuery()) {
            if (requestMessage instanceof SendMessage)
                ((SendMessage)requestMessage).setChatId(chatId);
            else if (requestMessage instanceof EditMessageReplyMarkup)
                ((EditMessageReplyMarkup)requestMessage).setChatId(chatId);
            else if (requestMessage instanceof EditMessageText)
                ((EditMessageText)requestMessage).setChatId(chatId);
        }
    }

    public BotApiMethod getRequestMessage() {
        return this.requestMessage;
    }

    public BotRequest handle(Update answer, Map<String, BotCommand> botCommands) {
        return this.handler.handle(answer);
    }
}
