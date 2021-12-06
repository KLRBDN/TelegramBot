package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;
import java.util.*;

public class AddRepetitiveTask extends AddTask {
    private final Map<String, Integer> mapOfDaysOfWeek;
    private final static String timeZone = "GMT+05:00";
    private LocalDate startDay;
    private ArrayList<Integer> pickedDaysOfWeek;
    private Integer dayOfMonth;
    private String pushedButtonText;
    private Integer weekNumber;
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
        pickedDaysOfWeek = new ArrayList<>();
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

    private void processText(String pushedButtonText){
        if (pushedButtonText.equals("repeat period"))
            repeatPeriod = (repeatPeriod + 1) % 5;
        else if (pushedButtonText.equals("time unit"))
            timeUnitIndex = (timeUnitIndex + 1) % 4;
        else if (pushedButtonText.startsWith("dayOfWeek")){
            var splittedButtonText = pushedButtonText.split(" ");
            if (splittedButtonText.length == 2){
                try{
                    var dayOfWeek = Integer.parseInt(splittedButtonText[1]);
                    if (pickedDaysOfWeek.contains(dayOfWeek))
                        pickedDaysOfWeek.remove(dayOfWeek);
                    else
                        pickedDaysOfWeek.add(dayOfWeek);
                }
                catch (NumberFormatException ignored) {}
            }
        }
    }

    private InlineKeyboardButton makeInlineKeyboardButton(String text, String callbackData){
        var repeatPeriodButton = new InlineKeyboardButton();
        repeatPeriodButton.setText(text);
        repeatPeriodButton.setCallbackData(callbackData);
        return repeatPeriodButton;
    }

    private BotRequest askRepetitiveDate(Update answer) {
        var pushedButtonText = answer.hasCallbackQuery() ? answer.getCallbackQuery().getData() : null;
        if (pushedButtonText != null){
            this.pushedButtonText = pushedButtonText;
            if (pushedButtonText.equals("OK"))
                return new BotRequest(
                        "Write time interval of your task in format: 9:00 - 10:00",
                        this::askTaskName);
            else
                processText(pushedButtonText);
        }
        var repeatPeriodButton = makeInlineKeyboardButton(
                String.format("Repeat every %d", repeatPeriod+1), "repeat period");
        var timeUnit = makeInlineKeyboardButton(
                String.format("%s", timeUnits[timeUnitIndex]), "time unit");

        List<InlineKeyboardButton> buttonRow = new ArrayList<>(2);
        buttonRow.add(repeatPeriodButton);
        buttonRow.add(timeUnit);

        List<List<InlineKeyboardButton>> buttonRowList = new ArrayList<>();
        buttonRowList.add(buttonRow);

        if (timeUnitIndex == 1){
            List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>(7);
            var daysOfWeek = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            for (var i = 0; i < daysOfWeek.length; i+=1)
                daysOfWeekRow.add(makeInlineKeyboardButton(
                        daysOfWeek[i].charAt(0) + ((pickedDaysOfWeek.contains(i+1)) ? " \uD83D\uDCA3" : ""),
                        String.format("dayOfWeek %d", i+1)));
            buttonRowList.add(daysOfWeekRow);
        }
        else if (timeUnitIndex == 2){
            var dayOfMonthButton = makeInlineKeyboardButton(
                    String.format("%dth day of month", startDay.getDayOfMonth()), "dayOfMonth");
            dayOfMonth = startDay.getDayOfMonth();

            var weekNumber = RepetitiveDate.getWeekNumber(startDay);
            var dayOfMonthAsDayOfWeek = makeInlineKeyboardButton(
                    String.format("%s day %dth week", startDay.getDayOfWeek().toString(), weekNumber),
                    "dayOfMonthAsDayOfWeek");
            this.weekNumber = weekNumber;

            List<InlineKeyboardButton> repetitiveDateFormatChoices = new ArrayList<>(2);
            repetitiveDateFormatChoices.add(dayOfMonthButton);
            repetitiveDateFormatChoices.add(dayOfMonthAsDayOfWeek);
            buttonRowList.add(repetitiveDateFormatChoices);
        }
        else if (timeUnitIndex == 3){
            var dayOfMonthButton = makeInlineKeyboardButton(
                    String.format("%d %s", startDay.getDayOfMonth(), startDay.getMonth().toString()), "dayAndMonth");
            dayOfMonth = startDay.getDayOfMonth();

            var weekNumber = RepetitiveDate.getWeekNumber(startDay);
            this.weekNumber = weekNumber;
            var dayOfWeekAndMonth = makeInlineKeyboardButton(
                    String.format("%s day of %dth week of %s",
                            startDay.getDayOfMonth(), weekNumber, startDay.getMonth().toString()),
                    "dayOfWeekAndMonth"
            );
            List<InlineKeyboardButton> repetitiveDateFormatChoices = new ArrayList<>(2);
            repetitiveDateFormatChoices.add(dayOfWeekAndMonth);
            repetitiveDateFormatChoices.add(dayOfMonthButton);
            buttonRowList.add(repetitiveDateFormatChoices);
        }
        var okButton = makeInlineKeyboardButton("OK", "OK");
        buttonRowList.add(List.of(okButton));

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttonRowList);
        var message = new SendMessage();
        message.setText("What time you want task to be repeated?");
        message.setReplyMarkup(inlineKeyboardMarkup);

        if (!answer.hasCallbackQuery())
            return new BotRequest(message, this::askRepetitiveDate);

        EditMessageReplyMarkup editedMessage = new EditMessageReplyMarkup();
        editedMessage.setReplyMarkup(inlineKeyboardMarkup);
        var callbackQueryMessage = answer.getCallbackQuery().getMessage();
        editedMessage.setChatId(Long.toString(callbackQueryMessage.getChatId()));
        editedMessage.setMessageId(callbackQueryMessage.getMessageId());

        return new BotRequest(editedMessage, this::askRepetitiveDate);
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
                    new RepetitiveDate(pushedButtonText, startDay,
                            pickedDaysOfWeek, repeatPeriod, timeUnitIndex, dayOfMonth, weekNumber),
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
