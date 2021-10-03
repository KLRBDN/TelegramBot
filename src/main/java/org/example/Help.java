package org.example;

import java.util.Map;

public class Help implements BotCommand {

    private final Map<String, BotCommand> bot_commands;

    public Help(Map<String, BotCommand> bot_commands) {
        super();
        this.bot_commands = bot_commands;
    }

    @Override
    public String getDescription() {
        return "Возвращает справку по боту";
    }

    @Override
    public String exec() {
        var strBuilder = new StringBuilder();
        for (var exemplaire : bot_commands.values())
            strBuilder.append(exemplaire.getName() + " : " + exemplaire.getDescription() + "\n");
        return strBuilder.toString();
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }
    
}
