package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.management.InvalidAttributeValueException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class AddRepetitiveTask extends AddTask {
    private final Map<String, Integer> mapOfDaysOfWeek;
    private final static String timeZone = "GMT+05:00";
    private LocalDate startDay;
    private Integer dayOfWeek;
    private String pushedButtonText;
    private Integer repeatPeriod = 0;
    private Integer timeUnitIndex = 0;
    private String[] timeUnits;

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
        return askRepetitiveDate(answer);
    }

    private BotRequest askRepetitiveDate(Update answer) {
        var pushedButtonText = answer.hasCallbackQuery() ? answer.getCallbackQuery().getData() : null;
        if (pushedButtonText != null){
            this.pushedButtonText = pushedButtonText;
            if (pushedButtonText.equals("repeat period"))
                repeatPeriod = (repeatPeriod + 1) % 5;
            else if (pushedButtonText.equals("time unit"))
                timeUnitIndex = (timeUnitIndex + 1) % 4;
            else if (pushedButtonText.equals("OK")){
                return new BotRequest(
                        "Write time interval of your task in format: 9:00 - 10:00",
                        this::askTaskName);
            }
            else if (pushedButtonText.startsWith("dayOfWeek")){
                var splittedButtonText = pushedButtonText.split("\\.");
                if (splittedButtonText.length == 2){
                    try{
                        dayOfWeek = Integer.parseInt(splittedButtonText[1]);
                    }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        List<List<InlineKeyboardButton>> buttonRowList = new ArrayList<>();
        var repeatPeriodButton = new InlineKeyboardButton();
        repeatPeriodButton.setText(String.format("Repeat every %d", repeatPeriod+1));
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
            var daysOfWeek = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            for (var i = 0; i < daysOfWeek.length; i+=1){
                var dayOfWeekButton = new InlineKeyboardButton();
                dayOfWeekButton.setText(daysOfWeek[i]);
                dayOfWeekButton.setCallbackData(String.format("dayOfWeek %d", i+1));
                daysOfWeekRow.add(dayOfWeekButton);
            }
            buttonRowList.add(daysOfWeekRow);
        }
        else if (timeUnitIndex == 2){
            List<InlineKeyboardButton> datePatternChoices = new ArrayList<>(2);
            var dayOfMonthButton = new InlineKeyboardButton();
            dayOfMonthButton.setText(String.format("%dth day of month", startDay.getDayOfMonth()));
            dayOfMonthButton.setCallbackData("dayOfMonth");

            var dayOfMonthAsDayOfWeek = new InlineKeyboardButton();
            var countOfPreviousMonthDaysInFirstWeek = LocalDate
                    .of(startDay.getYear(), startDay.getMonthValue(), 1)
                    .getDayOfWeek()
                    .getValue() - 1;
            var weekNumber = ((countOfPreviousMonthDaysInFirstWeek  + startDay.getDayOfMonth() - 1)/7) + 1;
            dayOfMonthAsDayOfWeek.setText(String.format("%s day %dth week", startDay.getDayOfWeek().toString(), weekNumber));
            dayOfMonthAsDayOfWeek.setCallbackData("dayOfMonthAsDayOfWeek");

            datePatternChoices.add(dayOfMonthButton);
            datePatternChoices.add(dayOfMonthAsDayOfWeek);
            buttonRowList.add(datePatternChoices);
        }
        else if (timeUnitIndex == 3){
            List<InlineKeyboardButton> datePatternChoices = new ArrayList<>(2);
            var dayOfMonthButton = new InlineKeyboardButton();
            dayOfMonthButton.setText(String.format("%d %s", startDay.getDayOfMonth(), startDay.getMonth().toString()));
            dayOfMonthButton.setCallbackData("dayAndMonth");

            var dayOfWeekAndMonth = new InlineKeyboardButton();
            var countOfPreviousMonthDaysInFirstWeek = LocalDate
                    .of(startDay.getYear(), startDay.getMonthValue(), 1)
                    .getDayOfWeek()
                    .getValue() - 1;
            var weekNumber = ((countOfPreviousMonthDaysInFirstWeek  + startDay.getDayOfMonth() - 1)/7) + 1;
            dayOfWeekAndMonth.setText(String.format("%s day of %dth week of %s",
                    startDay.getDayOfMonth(), weekNumber, startDay.getMonth().toString()));
            dayOfWeekAndMonth.setCallbackData("dayOfWeekAndMonth");

            datePatternChoices.add(dayOfWeekAndMonth);
            datePatternChoices.add(dayOfMonthButton);
            buttonRowList.add(datePatternChoices);
        }
        var okButton = new InlineKeyboardButton();
        okButton.setText("OK");
        okButton.setCallbackData("OK");
        List<InlineKeyboardButton> okButtonList = new ArrayList<>(1);
        okButtonList.add(okButton);
        buttonRowList.add(okButtonList);

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttonRowList);
        var message = new SendMessage();
        message.setText("What time you want task to be repeated?");
        message.setReplyMarkup(inlineKeyboardMarkup);

        if (answer.hasCallbackQuery()){
            EditMessageReplyMarkup editedMessage = new EditMessageReplyMarkup();
            editedMessage.setReplyMarkup((InlineKeyboardMarkup)message.getReplyMarkup());
            editedMessage.setChatId(Long.toString(
                    answer.getCallbackQuery().getMessage().getChatId()));
            editedMessage.setMessageId(answer.getCallbackQuery().getMessage().getMessageId());
            return new BotRequest(editedMessage, this::askRepetitiveDate);
        }
        return new BotRequest(message, this::askRepetitiveDate);
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
                    new RepetitiveDate(pushedButtonText, startDay, dayOfWeek, repeatPeriod, timeUnitIndex),
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
