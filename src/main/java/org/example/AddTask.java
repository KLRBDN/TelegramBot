package org.example;

import javax.management.InvalidAttributeValueException;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTask implements BotCommand {
    private String date;
    protected TimeInterval timeInterval;
    protected String name;
    protected String description;

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String getDescription() {
        return "Adds task for some date";
    }
    
    @Override
    public BasicAnswerHandler exec(Update answer) {
        var message = BotHelper.sendInlineKeyBoardMessage(answer.getMessage().getChatId());
        return new BasicAnswerHandler(message, this::processAnswer);
    }

    private BasicAnswerHandler processAnswer(Update answer){
        date = answer.getCallbackQuery().getData();
        var botRequest = new SendMessage();
        botRequest.setText("Write time interval of your task in format: 9:00 - 10:00");
        botRequest.setChatId(Long.toString(answer.getCallbackQuery().getMessage().getChatId()));
        return new BasicAnswerHandler(botRequest, this::askTimeOfDay);
    }

    private BasicAnswerHandler askTimeOfDay(Update answer) {
        timeInterval = processTimeInterval(answer);
        if (timeInterval != null)
            return new BasicAnswerHandler("Write name for your task", this::askTaskName);
        return new BasicAnswerHandler(
                "Error: Wrong time, please try again and write " +
                        "time interval of your task in format: 9:00 - 10:00",
                this::askTimeOfDay);
    }

    private BasicAnswerHandler askTaskName(Update answer){
        this.name = answer.getMessage().getText();
        return new BasicAnswerHandler("Write description for your task", this::askTaskDescription);
    }

    protected BasicAnswerHandler askTaskDescription(Update answer){
        this.description = answer.getMessage().getText();
        return new BasicAnswerHandler(
                "Write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::askTaskType);
    }

    protected BasicAnswerHandler askTaskType(Update answer){
        if (processAnswerForTaskType(answer)) {
            return new StandardAnswerHandler("Task was added");
        }
        return new BasicAnswerHandler(
                "Error: Wrong value for task type. Please try again and" +
                " write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::askTaskType);
    }

    private TimeInterval processTimeInterval(Update answer){
        var splitted = answer
                .getMessage()
                .getText()
                .split(" - ");
        if (splitted.length != 2)
            return null;
        var startTime = splitted[0];
        var endTime = splitted[1];

        var splStartTime = startTime.split(":");
        var splEndTime = endTime.split(":");

        if (splEndTime.length != 2 || splStartTime.length != 2)
            return null;
        try {
            return new TimeInterval(
                    new Time(Integer.parseInt(splStartTime[0]), Integer.parseInt(splStartTime[1])),
                    new Time(Integer.parseInt(splEndTime[0]), Integer.parseInt(splEndTime[1]))
            );
        } catch (InvalidAttributeValueException e) {
            return null;
        }
    }

    protected Boolean processAnswerForTaskType(Update answer){
        int taskTypeAsInt;
        try {
            taskTypeAsInt = Integer.parseInt(answer.getMessage().getText());
        } catch (NumberFormatException  e) {
            return false;
        }
        switch (taskTypeAsInt) {
            case 1:
                return addTask(TaskType.overlapping);
            case 2:
                return addTask(TaskType.nonOverlapping);
            case 3:
                return addTask(TaskType.important);
            default:
                return false;
        }
    }

    protected Boolean addTask(TaskType taskType) {
        try {
            return Day.getDay(date).tryAddTask(
                    new Task(
                            timeInterval.getStart(),
                            timeInterval.getEnd(),
                            taskType, name, description));
        } catch (ClassCastException | InvalidAttributeValueException | NullPointerException e) {
            return false;
        }
    }
}