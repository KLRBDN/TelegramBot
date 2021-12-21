package dialogue;

import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface AnswerHandler {
    BotRequest handle(Update answer);
}
