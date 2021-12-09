package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class AddRepetitiveTask extends AddTask {
    private final Map<String, Integer> mapOfDaysOfWeek;
    private final static String timeZone = "GMT+05:00";
    private LocalDate startDay;
    private Boolean[] pickedDaysOfWeek;
    private Integer dayOfMonth;
    private Boolean[] pickedDaysInMonthAndYearFormat;
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
        pickedDaysInMonthAndYearFormat = new Boolean[] {false, false, false, false};
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

    private EditMessageReplyMarkup processButtonText(String pushedButtonText, Message messageToEdit){
        if (pushedButtonText.split("\\.").length == 3)
            startDay = Day.getDay(pushedButtonText).getDate();
        else if (pushedButtonText.equals("start day"))
            return showCalendarKeyboard(messageToEdit);
        if (pushedButtonText.equals("repeat period"))
            return showAllRepeatPeriods(messageToEdit);
        else if (pushedButtonText.startsWith("repeat period-")){
            try{
                repeatPeriod = Integer.parseInt(pushedButtonText.split("-")[1])-1;
            }
            catch (NumberFormatException ignored) {}
        }
        else if (pushedButtonText.equals("time unit"))
            return showAllTimeUnits(messageToEdit);
        else if (pushedButtonText.startsWith("time unit-")){
            try{
                timeUnitIndex = Integer.parseInt(pushedButtonText.split("-")[1]);
            }
            catch (NumberFormatException ignored) {}
        }
        else if (pushedButtonText.equals("dayOfMonth"))
            pickedDaysInMonthAndYearFormat[0] = !pickedDaysInMonthAndYearFormat[0];
        else if (pushedButtonText.equals("dayOfMonthAsDayOfWeek"))
            pickedDaysInMonthAndYearFormat[1] = !pickedDaysInMonthAndYearFormat[1];
        else if (pushedButtonText.equals("dayAndMonth"))
            pickedDaysInMonthAndYearFormat[2] = !pickedDaysInMonthAndYearFormat[2];
        else if (pushedButtonText.equals("dayOfWeekAndMonth"))
            pickedDaysInMonthAndYearFormat[3] = !pickedDaysInMonthAndYearFormat[3];
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
        return null;
    }

    private EditMessageReplyMarkup showCalendarKeyboard(Message messageToEdit) {
        var repetitiveDateConstructor = new InlineKeyboardMarkup();
        repetitiveDateConstructor.setKeyboard(KeyboardConfiguration.createCalendarKeyboard());
        return getEditedDateConstructorMessage(repetitiveDateConstructor, messageToEdit);
    }

    private EditMessageReplyMarkup showAllTimeUnits(Message messageToEdit) {
        var repetitiveDateConstructor = new InlineKeyboardMarkup();
        repetitiveDateConstructor.setKeyboard(new ArrayList<>(Arrays.asList(
                        new ArrayList<>(Arrays.asList(
                                makeInlineKeyboardButton(timeUnits[0], "time unit-" + 0),
                                makeInlineKeyboardButton(timeUnits[1], "time unit-" + 1))),
                        new ArrayList<>(Arrays.asList(
                                makeInlineKeyboardButton(timeUnits[2], "time unit-" + 2),
                                makeInlineKeyboardButton(timeUnits[3], "time unit-" + 3)))
        )));
        return getEditedDateConstructorMessage(repetitiveDateConstructor, messageToEdit);
    }

    private EditMessageReplyMarkup showAllRepeatPeriods(Message messageToEdit) {
        List<InlineKeyboardButton> periodsFromOneToFive = new ArrayList<>(5);
        List<InlineKeyboardButton> periodsFromSixToTen = new ArrayList<>(5);
        for (var i = 1; i < 6; i++){
            var j = i;
            periodsFromOneToFive.add(makeInlineKeyboardButton(
                    Integer.toString(j), "repeat period-" + j));
            periodsFromSixToTen.add(makeInlineKeyboardButton(
                    Integer.toString(j+5), "repeat period-" + (j+5)));
        }
        var repetitiveDateConstructor = new InlineKeyboardMarkup();
        repetitiveDateConstructor.setKeyboard(new ArrayList<>(
                Arrays.asList(periodsFromOneToFive, periodsFromSixToTen)));

        return getEditedDateConstructorMessage(repetitiveDateConstructor, messageToEdit);
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
                        String.format("%s%dth day",
                                pickedDaysInMonthAndYearFormat[0] ? " \uD83D\uDCA3" : "", dayOfMonth),
                        "dayOfMonth"),
                makeInlineKeyboardButton(
                        String.format("%s%s, %dth week",
                                pickedDaysInMonthAndYearFormat[1] ? " \uD83D\uDCA3" : "",
                                startDay.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                                weekNumber),
                        "dayOfMonthAsDayOfWeek")));
    }

    private List<InlineKeyboardButton> makeDayOfYearButtonsRow() {
        this.dayOfMonth = startDay.getDayOfMonth();
        this.weekNumber = RepetitiveDate.getWeekNumber(startDay);

        return new ArrayList<>(Arrays.asList(
                makeInlineKeyboardButton(
                        String.format("%s%d %s", pickedDaysInMonthAndYearFormat[2] ? " \uD83D\uDCA3" : "",
                                startDay.getDayOfMonth(),
                                startDay.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)),
                        "dayAndMonth"),
                makeInlineKeyboardButton(
                        String.format("%s%s,%d week,%s",
                                pickedDaysInMonthAndYearFormat[3] ? " \uD83D\uDCA3" : "",
                                startDay.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                                weekNumber,
                                startDay.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)),
                        "dayOfWeekAndMonth")));
    }

    private List<InlineKeyboardButton> makeAppropriateButtonsRow(){
        var buttonsListMakers = new buttonsListMaker[]{
                this::makeDaysOfWeekButtonsRow,
                this::makeDayOfMonthButtonsRow,
                this::makeDayOfYearButtonsRow
        };
        return timeUnitIndex > 0 && timeUnitIndex-1 < buttonsListMakers.length
                ? buttonsListMakers[timeUnitIndex-1].makeButtonsList()
                : null;
    }

    private BotRequest askRepetitiveDate(Update answer) {
        if (answer != null){
            pushedButtonText = answer.hasCallbackQuery() ? answer.getCallbackQuery().getData() : null;
            if (pushedButtonText != null){
                if (pushedButtonText.equals("OK")) {
                    return new BotRequest(
                            "Write time interval of your task in format: 9:00 - 10:00",
                            this::askTaskName);
                }
                var editedDateConstructorMessage =
                        processButtonText(pushedButtonText, answer.getCallbackQuery().getMessage());
                if (editedDateConstructorMessage != null)
                    return new BotRequest(editedDateConstructorMessage, this::askRepetitiveDate);
            }
        }
        var dateConstructorKeyboard = new ArrayList<>(List.of(List.of(
                        makeInlineKeyboardButton("Start day: " + startDay.toString(), "start day"))));
        dateConstructorKeyboard.add(new ArrayList<>(Arrays.asList(
                makeInlineKeyboardButton(
                        String.format("Repeat every %d", repeatPeriod+1), "repeat period"),
                makeInlineKeyboardButton(
                        String.format("%s", timeUnits[timeUnitIndex]), "time unit")
        )));
        if (timeUnitIndex > 0)
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
