package org.example;

import javax.management.InvalidAttributeValueException;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTask implements BotCommand {
    //protected Object[] dayAndInterval;
    //protected Update name;
    //protected Update description;
    private String date;
    private TimeInterval timeInterval;
    private String name;
    private String description;
    private TaskType taskType;

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
        return new BasicAnswerHandler(
                "", this::processAnswer, message);
    }

    private BasicAnswerHandler processAnswer(Update answer){
        date = answer.getCallbackQuery().getData();
        var message = new SendMessage();
        message.setText("Write time interval of your task in format: 9:00 - 10:00");
        message.setChatId(Long.toString(answer.getCallbackQuery().getMessage().getChatId()));
        return new BasicAnswerHandler("",
                this::askTimeOfDay, message);
    }

    private BasicAnswerHandler askTimeOfDay(Update answer) {
        timeInterval = processTimeInterval(answer);
        if (timeInterval == null) {
            return new BasicAnswerHandler(
                    "Error: Wrong time, please try again and write " +
                            "time interval of your task in format: 9:00 - 10:00",
                    this::askTimeOfDay, null);
        }
        return new BasicAnswerHandler(
                "Write name for your task",
                this::askTaskName, null);
    }

    private BasicAnswerHandler askTaskName(Update answer){
        this.name = answer.getMessage().getText();
        return new BasicAnswerHandler(
                "Write description for your task",
                this::askTaskDescription, null);
    }

    protected BasicAnswerHandler askTaskDescription(Update answer){
        this.description = answer.getMessage().getText();
        return new BasicAnswerHandler(
                "Write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::askTaskType, null);
    }

    protected BasicAnswerHandler askTaskType(Update answer){
        if (processAnswerForTaskType(answer)) {
            return new StandardAnswerHandler("Task was added");
        }
        return new BasicAnswerHandler(
                                "Error: Wrong value for task type. Please try again and" +
                " write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::askTaskType, null);
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
                taskType = TaskType.overlapping;
                break;
            case 2:
                taskType = TaskType.nonOverlapping;
                break;
            case 3:
                taskType = TaskType.important;
                break;
            default:
                return false;
        }
        // Здесь трабл (аргументы)
        if (addTask(taskType, description, name, new Object[] { timeInterval })){
            return true;
        }
        return false;
    }

    // Здесь трабл (аргументы)
    protected Boolean addTask(TaskType taskType, String description,
                            String name, Object[] timeIntervalObj) {
        try {
            var day = Day.getDay(date);
            var timeInterval = (TimeInterval)timeIntervalObj[0];
            return day.tryAddTask(
                    new Task(
                            timeInterval.getStart(),
                            timeInterval.getEnd(),
                            taskType, name, description));
        } catch (ClassCastException | InvalidAttributeValueException | NullPointerException e) {
            return false;
        }
    }
}