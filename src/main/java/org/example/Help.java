package org.example;

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
    public AnswerHandler exec() {
        var strBuilder = new StringBuilder();
        for (var exemplar : botCommands.values())
            strBuilder.append(exemplar.getName())
                      .append(" - ")
                      .append(exemplar.getDescription())
                      .append("\n");
        return new StandartAnswerHandler(strBuilder.toString());
    } 
}
