package BotCommands;

import DialogueHandling.BotRequest;
import DialogueHandling.StandardBotRequest;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class Help implements BotCommand {

    private final Map<String, BotCommand> botCommands;

    public Help(Map<String, BotCommand> botCommands) {
        super();
        this.botCommands = botCommands;
    }

    @Override
    public String getDescription() {
        return "Возвращает справку по боту";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BotRequest exec(Update answer) {
        var strBuilder = new StringBuilder();
        for (var exemplar : botCommands.values()) {
            strBuilder.append(exemplar.getName())
                    .append(" - ")
                    .append(exemplar.getDescription())
                    .append("\n");
            if (exemplar.getName().equals("/getclosesttasks")) {
                strBuilder.append("[‼️В списке выведенных задач ставится перед важными задачами (TaskType.important)]\n");
            }
        }
        strBuilder.append("\nЗначение эмодзи в календаре:\n");
        strBuilder.append("\uD83D\uDD25 означает, что этот день в календаре сегодняшний\n");
        strBuilder.append("❗️означает, что на этот день у вас запланировано важное задание (TaskType.important)\n");
        strBuilder.append("\uD83D\uDCA3 означает, что этот день в календаре не только сегодняшний, но и на него запланировано важное задание (TaskType.important)\n");
        return new StandardBotRequest(strBuilder.toString());
    }
}
