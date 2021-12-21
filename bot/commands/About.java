package commands;

import dialogue.BotRequest;
import dialogue.StandardBotRequest;
import org.telegram.telegrambots.meta.api.objects.Update;

public class About implements BotCommand {
    @Override
    public String getDescription() {
        return "Возвращает имена создателей бота";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BotRequest exec(Update answer) {
        return new StandardBotRequest("Создатели: Михаил Яскевич, Павел Овчинников");
    }
}
