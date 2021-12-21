package DialogueHandling;

import java.util.Map;

import BotCommands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StandardBotRequest extends BotRequest {

    public StandardBotRequest(String request) {
        super(request);
    }

    @Override
    public BotRequest handle(Update answer, Map<String, BotCommand> botCommands) {
        return botCommands.containsKey(answer.getMessage().getText())
            ? botCommands.get(answer.getMessage().getText()).exec(answer)
            : new StandardBotRequest("Wrong command");
    }
}
