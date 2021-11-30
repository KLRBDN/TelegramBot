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
    public BotRequest exec(Update answer) {
        var message = BotHelper.sendInlineKeyBoardMessage(answer.getMessage().getChatId());
        return new BotRequest(message, this::askTimeInterval);
    }

    private BotRequest askTimeInterval(Update answer){
        date = answer.getCallbackQuery().getData();
        var botRequest = new SendMessage();
        botRequest.setText("Write time interval of your task in format: 9:00 - 10:00");
        botRequest.setChatId(Long.toString(answer.getCallbackQuery().getMessage().getChatId()));
        return new BotRequest(botRequest, this::askTaskName);
    }

    private BotRequest askTaskName(Update answerWithTimeInterval) {
        timeInterval = processTimeInterval(answerWithTimeInterval);
        if (timeInterval != null)
            return new BotRequest("Write name for your task", this::askTaskDescription);
        return new BotRequest(
                "Error: Wrong time, please try again and write " +
                        "time interval of your task in format: 9:00 - 10:00",
                this::askTaskName);
    }

    protected BotRequest askTaskDescription(Update answerWithName){
        this.name = answerWithName.getMessage().getText();
        return new BotRequest("Write description for your task", this::askTaskType);
    }

    protected BotRequest askTaskType(Update answerWithDescription){
        this.description = answerWithDescription.getMessage().getText();
        return new BotRequest(
                "Write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::processAnswer);
    }

    protected BotRequest processAnswer(Update answerWithTaskType){
        if (processAnswerForTaskType(answerWithTaskType)) {
            return new StandardBotRequest("Task was added");
        }
        return new BotRequest(
                "Error: Wrong value for task type. Please try again and" +
                " write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::processAnswer);
    }

    private TimeInterval processTimeInterval(Update answer){
        var splitted = answer
                .getMessage()
                .getText()
                .split(" - ");
        if (splitted.length != 2)
            return null;

        return makeTimeInterval(splitted[0], splitted[1]);
    }

    protected TimeInterval makeTimeInterval(String start, String end){
        var splStart = start.split(":");
        var splEnd = end.split(":");
        if (splStart.length != 2 || splEnd.length != 2)
            return  null;
        try{
            return new TimeInterval(
                    new Time(Integer.parseInt(splStart[0]), Integer.parseInt(splStart[1])),
                    new Time(Integer.parseInt(splEnd[0]), Integer.parseInt(splEnd[1]))
            );
        }
        catch (InvalidAttributeValueException e){
            return null;
        }
    }

    protected Boolean processAnswerForTaskType(Update answerWithTaskType){
        int taskTypeAsInt;
        try {
            taskTypeAsInt = Integer.parseInt(answerWithTaskType.getMessage().getText());
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