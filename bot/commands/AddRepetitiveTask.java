package commands;

import date.Day;
import date.RepetitiveDate;
import date.TimeUnit;
import dialogue.BotRequest;
import dialogue.KeyboardConfiguration;
import TaskConfiguration.RepetitiveTasks;
import TaskConfiguration.TaskType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class AddRepetitiveTask extends AddTask {
    private final static String timeZone = "GMT+05:00";
    private LocalDate startDay;
    private final Boolean[] pickedDaysOfWeek;
    private final Boolean[] pickedDaysInMonthAndYearFormat;
    private String pushedButtonText;
    private Integer repeatPeriod = 0;
    private TimeUnit timeUnit;

    public AddRepetitiveTask(){
        pickedDaysInMonthAndYearFormat = new Boolean[] {false, false, false, false};
        pickedDaysOfWeek = new Boolean[7];
        for (var i = 0; i < 7; i++)
            pickedDaysOfWeek[i] = false;
        startDay = LocalDate.now(
                TimeZone.getTimeZone(timeZone).toZoneId());
        timeUnit = TimeUnit.day;
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

    public LocalDate getStartDay(){
        return startDay;
    }

    public Boolean[] getPickedDaysOfWeek(){
        return pickedDaysOfWeek;
    }

    public Integer getRepeatPeriod(){
        return repeatPeriod;
    }

    public Boolean[] getPickedDaysInMonthAndYearFormat(){
        return pickedDaysInMonthAndYearFormat;
    }

    public TimeUnit getTimeUnit(){
        return timeUnit;
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
                timeUnit = TimeUnit.valueOf(pushedButtonText.split("-")[1]);
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
                                makeInlineKeyboardButton(TimeUnit.day.name(), "time unit-day"),
                                makeInlineKeyboardButton(TimeUnit.week.name(), "time unit-week"))),
                        new ArrayList<>(Arrays.asList(
                                makeInlineKeyboardButton(TimeUnit.month.name(), "time unit-month"),
                                makeInlineKeyboardButton(TimeUnit.year.name(), "time unit-year")))
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
        return new ArrayList<>(Arrays.asList(
                makeInlineKeyboardButton(
                        String.format("%s%dth day",
                                pickedDaysInMonthAndYearFormat[0] ? " \uD83D\uDCA3" : "", startDay.getDayOfMonth()),
                        "dayOfMonth"),
                makeInlineKeyboardButton(
                        String.format("%s%s, %dth week",
                                pickedDaysInMonthAndYearFormat[1] ? " \uD83D\uDCA3" : "",
                                startDay.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                                RepetitiveDate.getWeekNumber(startDay)),
                        "dayOfMonthAsDayOfWeek")));
    }

    private List<InlineKeyboardButton> makeDayOfYearButtonsRow() {
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
                                RepetitiveDate.getWeekNumber(startDay),
                                startDay.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)),
                        "dayOfWeekAndMonth")));
    }

    private List<InlineKeyboardButton> makeAppropriateButtonsRow(){
        if (timeUnit.equals(TimeUnit.week))
            return makeDaysOfWeekButtonsRow();
        if (timeUnit.equals(TimeUnit.month))
            return makeDayOfMonthButtonsRow();
        return timeUnit.equals(TimeUnit.year) ? makeDayOfYearButtonsRow() : null;
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
                        String.format("%s", timeUnit.name()), "time unit")
        )));
        var appropriateButtonsRow = makeAppropriateButtonsRow();
        if (appropriateButtonsRow != null)
            dateConstructorKeyboard.add(appropriateButtonsRow);
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
        var task = makeTask(taskType);
        return task != null && RepetitiveTasks.tryAddTask(
                new RepetitiveDate(this), task);
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
