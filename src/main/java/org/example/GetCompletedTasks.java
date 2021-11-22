package org.example;

public class GetCompletedTasks implements BotCommand{

    @Override
    public String getDescription() {
        return "Returns all completed tasks of some date";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public AnswerHandler exec() {
        var strBuilder = new StringBuilder();
        for (var pair : YearsDataBase.completedTasks) {
            var task = (Task) pair[0];
            var date = (String)pair[1];
            strBuilder.append(task.name)
                    .append(": ")
                    .append("completed on ")
                    .append(date)
                    .append("\n");
        }
        if (strBuilder.length() == 0)
            return new StandartAnswerHandler("No tasks completed");
        return new StandartAnswerHandler(strBuilder.toString());
    }
}
