package commands;

import dialogue.BotRequest;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommand {

    String getDescription();

    String getName();

    BotRequest exec(Update answer);
}
