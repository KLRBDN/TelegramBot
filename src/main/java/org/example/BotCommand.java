package org.example;

public interface BotCommand {

    public String getDescription();

    public String getName();
    
    public String exec();
}
