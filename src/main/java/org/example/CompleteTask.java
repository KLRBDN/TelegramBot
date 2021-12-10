package org.example;

public class CompleteTask extends DeleteTask {
    public CompleteTask() {
        super();
    }

    @Override
    public String getDescription() {
        return "Выполняет задачу и затем удаляет её из списка активных задач";
    }

    @Override
    public String getName() {
        return "/complete";
    }

    @Override
    protected Boolean deleteTask(String date, String name) {
        return Day.getDay(date).completeTask(name);
    }
}
