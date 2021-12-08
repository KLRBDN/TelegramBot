package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
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
    private Boolean[] pickedDaysOfWeek;
    private Integer dayOfMonth;
    private String pushedButtonText;
    private Integer weekNumber;
    private Integer repeatPeriod = 0;
    private Integer maxRepeatPeriod = 9;
    private Integer timeUnitIndex = 0;
    private String[] timeUnits;

    public AddRepetitiveTask(){
        mapOfDaysOfWeek = new HashMap<>() {{
            put("M", 1);
            put("T1", 2);
            put("W", 3);
            put("T2", 4);
            put("F", 5);
            put("S1", 6);
            put("S2", 7);
        }};
        pickedDaysOfWeek = new Boolean[7];
        for (var i = 0; i < 7; i++)
            pickedDaysOfWeek[i] = false;
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
        return "/addrepetitive";
    }

    @Override
    public BotRequest exec(Update answer) {
        return askRepetitiveDate(answer);
    }

    private void processText(String pushedButtonText){
        if (pushedButtonText.equals("repeat period")
                || pushedButtonText.equals("inc repeat period"))
            repeatPeriod = ++repeatPeriod % (maxRepeatPeriod+1);
        else if (pushedButtonText.equals("dec repeat period")){
            if (--repeatPeriod == -1)
                repeatPeriod = maxRepeatPeriod;
        }
        else if (pushedButtonText.equals("time unit")
                || pushedButtonText.equals("inc time unit index"))
            timeUnitIndex = ++timeUnitIndex % timeUnits.length;
        else if (pushedButtonText.equals("dec time unit index")){
            if (--timeUnitIndex == -1)
                timeUnitIndex = timeUnits.length-1;
        }
        else if (pushedButtonText.startsWith("dayOfWeek")){
            var splittedButtonText = pushedButtonText.split(" ");
            if (splittedButtonText.length == 2){
                try{
                    var dayOfWeek = Integer.parseInt(splittedButtonText[1]);
                    pickedDaysOfWeek[dayOfWeek-1] = !pickedDaysOfWeek[dayOfWeek-1];
                }
                catch (NumberFormatException ignored) {}
            }
        }
    }

    private InlineKeyboardButton makeInlineKeyboardButton(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    @FunctionalInterface
    private interface buttonsListMaker {
        List<InlineKeyboardButton> makeButtonsList();
    }

    private List<InlineKeyboardButton> makeDaysOfWeekButtonsRow() {
        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>(7);
        var daysOfWeek = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (var i = 0; i < daysOfWeek.length; i+=1)
            daysOfWeekRow.add(makeInlineKeyboardButton(
                    daysOfWeek[i].charAt(0) + ((pickedDaysOfWeek[i]) ? " \uD83D\uDCA3" : ""),
                    String.format("dayOfWeek %d", i+1)));
        return daysOfWeekRow;
    }

    private List<InlineKeyboardButton> makeDayOfMonthButtonsRow() {
        this.dayOfMonth = startDay.getDayOfMonth();
        this.weekNumber = RepetitiveDate.getWeekNumber(startDay);

        return new ArrayList<>(Arrays.asList(
                makeInlineKeyboardButton(
                        String.format("%dth day of month", dayOfMonth), "dayOfMonth"),
                makeInlineKeyboardButton(
                        String.format("%s day %dth week", startDay.getDayOfWeek().toString(), weekNumber),
                        "dayOfMonthAsDayOfWeek")));
    }

    private List<InlineKeyboardButton> makeDayOfYearButtonsRow() {
        this.dayOfMonth = startDay.getDayOfMonth();
        this.weekNumber = RepetitiveDate.getWeekNumber(startDay);

        return new ArrayList<>(Arrays.asList(
                makeInlineKeyboardButton(
                        String.format("%d %s", startDay.getDayOfMonth(), startDay.getMonth().toString()),
                        "dayAndMonth"),
                makeInlineKeyboardButton(
                        String.format("%s day of %dth week of %s",
                                startDay.getDayOfMonth(), weekNumber, startDay.getMonth().toString()),
                        "dayOfWeekAndMonth")));
    }

    private List<InlineKeyboardButton> makeAppropriateButtonsRow(){
        return new buttonsListMaker[]{
                this::makeDaysOfWeekButtonsRow,
                this::makeDayOfMonthButtonsRow,
                this::makeDayOfYearButtonsRow
        }[timeUnitIndex-1].makeButtonsList();
    }

    private BotRequest askRepetitiveDate(Update answer) {
        if (answer != null)
            pushedButtonText = answer.hasCallbackQuery() ? answer.getCallbackQuery().getData() : null;
        if (pushedButtonText != null){
            if (pushedButtonText.equals("OK"))
                return new BotRequest(
                        "Write time interval of your task in format: 9:00 - 10:00",
                        this::askTaskName);
            else
                processText(pushedButtonText);
        }

        List<List<InlineKeyboardButton>> dateConstructorKeyboard = new ArrayList<>();
        dateConstructorKeyboard.add(new ArrayList<>(Arrays.asList(
                makeInlineKeyboardButton(
                        String.format("Repeat every %d", repeatPeriod+1), "repeat period"),
                makeInlineKeyboardButton(
                        String.format("%s", timeUnits[timeUnitIndex]), "time unit")
        )));
        var nextRepeatPeriod = repeatPeriod+1;
        var prevRepeatPeriod = repeatPeriod-1 >= 0 ? repeatPeriod-1 : maxRepeatPeriod;
        var nextTimeUnit = timeUnits[(timeUnitIndex+1)%timeUnits.length];
        var prevTimeUnit = timeUnits[timeUnitIndex-1 >= 0 ? timeUnitIndex-1 : timeUnits.length-1];
        dateConstructorKeyboard.add(new ArrayList<>(Arrays.asList(
                makeInlineKeyboardButton(
                        String.format("every %d", prevRepeatPeriod+1), "dec repeat period"),
                makeInlineKeyboardButton(
                        String.format("every %d", nextRepeatPeriod+1), "inc repeat period"),
                makeInlineKeyboardButton(
                        String.format("%s", prevTimeUnit), "dec time unit index"),
                makeInlineKeyboardButton(
                        String.format("%s", nextTimeUnit),"inc time unit index")
        )));
        dateConstructorKeyboard.add(makeAppropriateButtonsRow());
        dateConstructorKeyboard.add(List.of(
                makeInlineKeyboardButton("OK", "OK")));

        var repetitiveDateConstructor = new InlineKeyboardMarkup();
        repetitiveDateConstructor.setKeyboard(dateConstructorKeyboard);

        var botQuestionAboutTime = new SendMessage();
        botQuestionAboutTime.setText("What time you want task to be repeated?");
        botQuestionAboutTime.setReplyMarkup(repetitiveDateConstructor);

        if (answer == null || !answer.hasCallbackQuery())
            return new BotRequest(botQuestionAboutTime, this::askRepetitiveDate);

        return new BotRequest(
                getEditedDateConstructorMessage(
                        repetitiveDateConstructor, answer.getCallbackQuery().getMessage()),
                this::askRepetitiveDate);
    }

    private EditMessageReplyMarkup getEditedDateConstructorMessage(InlineKeyboardMarkup repetitiveDateConstructor, Message messageToEdit){
        EditMessageReplyMarkup editedDateCtorMessage = new EditMessageReplyMarkup();

        editedDateCtorMessage.setReplyMarkup(repetitiveDateConstructor);
        editedDateCtorMessage.setChatId(Long.toString(messageToEdit.getChatId()));
        editedDateCtorMessage.setMessageId(messageToEdit.getMessageId());

        return editedDateCtorMessage;
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
