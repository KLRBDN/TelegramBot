package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

public class CancelRepetitiveTask implements BotCommand{
    @Override
    public String getDescription() {
        return "cancel repetitive task by name";
    }

    @Override
    public String getName() {
        return "/cancelrepetitive";
    }

    @Override
    public BotRequest exec(Update answer) {
        return new BotRequest("Write name of task you want to cancel", this::cancelTask);
    }

    private BotRequest cancelTask(Update answerWithName) {
        var name = answerWithName.getMessage().getText();
        if (RepetitiveTasks.tryDeleteTask(name))
            return new StandardBotRequest("Repetitive task was deleted successfully!");
        return new BotRequest("Repetitive task was not found, please write task name again", this::cancelTask);
    }
}
