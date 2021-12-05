package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.management.InvalidAttributeValueException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class AddRepetitiveTask extends AddTask {
    private final Map<String, Integer> mapOfDaysOfWeek;
    private final static String timeZone = "GMT+05:00";
    private LocalDate startDay;
    private DayOfWeek dayOfWeek;
    private Integer repeatPeriod = 0;
    private String[] timeUnits;
    private Integer timeUnitIndex = 0;

    public AddRepetitiveTask(){
        mapOfDaysOfWeek = new HashMap<String, Integer>() {{
            put("M", 1);
            put("T1", 2);
            put("W", 3);
            put("T2", 4);
            put("F", 5);
            put("S1", 6);
            put("S2", 7);
        }};
        timeUnits = new String[] {"day", "week", "month", "year"};
        startDay = LocalDate.now(
                TimeZone.getTimeZone(timeZone).toZoneId());
    }

    @Override
    public String getDescription() {
        return "Добавляет повторяющуюся задачу на выбранный день недели";
    }

    @Override
    public String getName() {
        return "/" + this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public BotRequest exec(Update answer) {
        return askDatePattern(answer);
    }

    private BotRequest askDatePattern(Update answer) {
        var callData = answer.hasCallbackQuery() ? answer.getCallbackQuery().getData() : null;
        if (callData != null){
            if (callData.equals("repeat period"))
                repeatPeriod = (repeatPeriod + 1) % 5;
            if (callData.equals("time unit"))
                timeUnitIndex = (timeUnitIndex + 1) % 4;
        }
        List<List<InlineKeyboardButton>> buttonRowList = new ArrayList<>();
        var repeatPeriodButton = new InlineKeyboardButton();
        repeatPeriodButton.setText(String.format("Повтор каждый(ую) %d", repeatPeriod+1));
        repeatPeriodButton.setCallbackData("repeat period");
        var timeUnit = new InlineKeyboardButton();
        timeUnit.setText(String.format("%s", timeUnits[timeUnitIndex]));
        timeUnit.setCallbackData("time unit");

        List<InlineKeyboardButton> buttonRow = new ArrayList<>(2);
        buttonRow.add(repeatPeriodButton);
        buttonRow.add(timeUnit);
        buttonRowList.add(buttonRow);

        if (timeUnitIndex == 1){
            List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>(7);
            var daysOfWeek = new String[] {"M", "T", "W", "T", "F", "S", "S"};
            for (var i = 0; i < daysOfWeek.length; i+=1){
                var dayOfWeekButton = new InlineKeyboardButton();
                dayOfWeekButton.setText(daysOfWeek[i]);
                dayOfWeekButton.setCallbackData(String.format("dayOfWeek %d", i));
                daysOfWeekRow.add(dayOfWeekButton);
            }
            buttonRowList.add(daysOfWeekRow);
        }
        else if (timeUnitIndex == 2){
            List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>(2);
            var dayOfMonthButton = new InlineKeyboardButton();
            dayOfMonthButton.setText(String.format("%d-ый день месяца", startDay.getDayOfMonth()));
            dayOfMonthButton.setCallbackData(String.format("dayOfMonth"));
            daysOfWeekRow.add(dayOfMonthButton);
            buttonRowList.add(daysOfWeekRow);

//            var dayOfMonthButton = new InlineKeyboardButton();
//            var weekNumber = (startDay.getDayOfMonth() - startDay.getDayOfWeek().getValue())/7;
//            dayOfMonthButton.setText(String.format("%s", startDay.getDayOfWeek().toString(), startDay.);
//            dayOfMonthButton.setCallbackData(String.format("dayOfMonth"));
//            daysOfWeekRow.add(dayOfMonthButton);
        }
        else if (timeUnitIndex == 3){
            List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>(2);
            var dayOfMonthButton = new InlineKeyboardButton();
            dayOfMonthButton.setText(String.format("%d %s", startDay.getDayOfMonth(), startDay.getMonth().toString()));
            dayOfMonthButton.setCallbackData(String.format("dayAndMonth"));
            daysOfWeekRow.add(dayOfMonthButton);
            buttonRowList.add(daysOfWeekRow);
        }

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttonRowList);
        var message = new SendMessage();
        message.setChatId(Long.toString(answer.getMessage().getChatId()));
        message.setText("What time you want task to be repeated?");
        message.setReplyMarkup(inlineKeyboardMarkup);

        if (answer.hasCallbackQuery()){
            EditMessageReplyMarkup editedMessage = new EditMessageReplyMarkup();
            editedMessage.setReplyMarkup((InlineKeyboardMarkup)message.getReplyMarkup());
            editedMessage.setChatId(message.getChatId());
            editedMessage.setMessageId(answer.getCallbackQuery().getMessage().getMessageId());
            return new BotRequest(editedMessage, this::askTimeInterval);
        }
        return new BotRequest(message, this::askDatePattern);
    }

    private BotRequest askTimeInterval(Update answerWithDayOfWeek){
        var dayOfWeekAsInt = mapOfDaysOfWeek.get(answerWithDayOfWeek.getMessage().getText());
        if (dayOfWeekAsInt == null)
            return new BotRequest(
                    "Write day of week to add repetitive task (M, T1, W, T2, F, S1, S2)",
                    this::askTimeInterval);
        this.dayOfWeek = DayOfWeek.of(dayOfWeekAsInt);
        return new BotRequest("Write time interval of your task in format: 9:00 - 10:00", this::askTaskName);
    }

    private BotRequest askTaskName(Update time){
        if (!tryProcessDateTime(time))
            return exec(null);
        return new BotRequest("Write name for your task", this::askTaskDescription);
    }

    @Override
    protected Boolean addTask(TaskType taskType) {
        try {
            return RepetitiveTasks.tryAddTask(
                    dayOfWeek,
                    new Task(
                            timeInterval.getStart(),
                            timeInterval.getEnd(),
                            taskType, name, description));
        } catch (InvalidAttributeValueException | NullPointerException e) {
            return false;
        }
    }

    private Boolean tryProcessDateTime(Update dateTime){
        var splTime = dateTime
                .getMessage()
                .getText()
                .split(" - ");
        if (splTime.length != 2)
            return false;

        var interval = makeTimeInterval(splTime[0], splTime[1]);
        if (interval == null)
            return false;

        this.timeInterval = interval;

        return true;
    }
}
