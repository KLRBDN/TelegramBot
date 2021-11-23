package org.example;

public interface BotCommand {

    String getDescription();

    String getName();
    
    BasicAnswerHandler exec();
}
