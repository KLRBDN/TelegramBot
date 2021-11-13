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

    @Override
    public AnswerHandler exec() {
        var strBuilder = new StringBuilder();
        for (var exemplaire : botCommands.values())
            strBuilder.append(exemplaire.getName() + " - " + exemplaire.getDescription() + "\n");
        return new StandartAnswerHandler(strBuilder.toString());
    } 
}
