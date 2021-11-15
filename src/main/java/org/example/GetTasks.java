package org.example;

public class GetTasks implements BotCommand {

    @Override
    public String getDescription() {
        return "Возвращает все активные задачи";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public AnswerHandler exec() {
        var strBuilder = new StringBuilder();
        for (Task task : YearsDateBase.getToday().getTasks())
            strBuilder.append(task.name + ": " + task.timeInterval.toString() + "\n");
        return new StandartAnswerHandler(strBuilder.toString());    
    }
}
