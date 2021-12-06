package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class KeyboardConfiguration {
    private static int month;
    private static int year;
    private static int currentYear;
    private static int currentMonth;
    private static int currentDay;
    private static final LifeSchedulerBot botInstance = LifeSchedulerBot.getInstance();

    public KeyboardConfiguration() {
        var dateSplitted = Day.getTodayDate().split("\\.");
        currentMonth = Integer.parseInt(dateSplitted[1]);
        month = currentMonth;
        currentYear = Integer.parseInt(dateSplitted[2]);
        year = currentYear;
        currentDay = Integer.parseInt(dateSplitted[0]);
    }

    public static SendMessage createCommandKeyboard(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        var botCommands = botInstance.getBotCommands();
        for (int i = 0; i < botCommands.size(); i++) {
            var button = new KeyboardButton();
            button.setText(botCommands.get(i).getName());
            keyboardRow.add(button);
            if (keyboardRow.size() % 4 == 0) {
                keyboardRowList.add(keyboardRow);
                keyboardRow = new KeyboardRow(4);
            }
        }
        if (keyboardRow.size() > 0) {
            keyboardRowList.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        var message = new SendMessage();
        message.setChatId(Long.toString(chatId));
        message.setText("No data");
        message.setReplyMarkup(replyKeyboardMarkup);
        return message;
    }

    public static SendMessage createWeekKeyboard(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttonRow = new ArrayList<>(7);
        List<List<InlineKeyboardButton>> buttonRowList = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            var dayOfWeekButton = new InlineKeyboardButton();
            dayOfWeekButton.setText(DayOfWeek.of(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            dayOfWeekButton.setCallbackData(Integer.toString(i));
            buttonRow.add(dayOfWeekButton);
        }
        buttonRowList.add(buttonRow);
        inlineKeyboardMarkup.setKeyboard(buttonRowList);
        var message = new SendMessage();
        message.setChatId(Long.toString(chatId));
        message.setText("Choose the day of week");
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    public static SendMessage createCalendarKeyboard(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var yearsDataBase = YearsDataBase.getInstance();
        var days = yearsDataBase.getYear(year).getMonth(month).getAllDays();
        List<InlineKeyboardButton> buttonRow = new ArrayList<>(7);
        List<List<InlineKeyboardButton>> buttonRowList = new ArrayList<>();
        var managerButtonNext = new InlineKeyboardButton();
        managerButtonNext.setText("Next >");
        managerButtonNext.setCallbackData("Next");
        var managerButtonPrevious = new InlineKeyboardButton();
        managerButtonPrevious.setText("< Previous");
        managerButtonPrevious.setCallbackData("Previous");
        var monthButton = new InlineKeyboardButton();
        monthButton.setText(Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ", " + year);
        monthButton.setCallbackData("No data");
        buttonRow.add(managerButtonPrevious);
        buttonRow.add(monthButton);
        buttonRow.add(managerButtonNext);
        buttonRowList.add(buttonRow);
        buttonRow = new ArrayList<>(7);
        var monthFirstDayOfWeek = LocalDate.of(year, month, 1).getDayOfWeek().getValue();
        trySwitchMonth("Previous", true);
        var daysCount = yearsDataBase.getYear(year).getMonth(month).getAllDays().length;
        for (int i = daysCount - monthFirstDayOfWeek + 2; i <= daysCount; i++) {
            var button = new InlineKeyboardButton();
            button.setText(generateTextToButton(i));
            if ((year == currentYear && month == currentMonth - 1) ||
                    (year == currentYear - 1 && currentMonth == 1 && month == 12))
                button.setCallbackData("Past");
            else
                button.setCallbackData(i + "." + month + "." + year);
            buttonRow.add(button);
        }
        trySwitchMonth("Next", true);
        for (int i = 1; i <= days.length; i++) {
            var button = new InlineKeyboardButton();
            button.setText(generateTextToButton(i));
            if (year == currentYear && month == currentMonth && i < currentDay)
                button.setCallbackData("Past");
            else
                button.setCallbackData(i + "." + month + "." + year);
            buttonRow.add(button);
            if (buttonRow.size() % 7 == 0) {
                buttonRowList.add(buttonRow);
                buttonRow = new ArrayList<>(7);
            }
        }
        var rowSize = buttonRow.size();
        trySwitchMonth("Next", false);
        for (int i = 1; i <= 7 - rowSize; i++) {
            var button = new InlineKeyboardButton();
            button.setText(generateTextToButton(i));
            button.setCallbackData(i + "." + month + "." + year);
            buttonRow.add(button);
        }
        buttonRowList.add(buttonRow);
        buttonRow = new ArrayList<>();
        if (buttonRowList.size() < 7) {
            for (int i = 7 - rowSize + 1; i < 14 - rowSize + 1; i++) {
                var button = new InlineKeyboardButton();
                button.setText(generateTextToButton(i));
                button.setCallbackData(i + "." + month + "." + year);
                buttonRow.add(button);
            }
        }
        buttonRowList.add(buttonRow);
        trySwitchMonth("Previous", false);
        inlineKeyboardMarkup.setKeyboard(buttonRowList);
        var message = new SendMessage();
        message.setChatId(Long.toString(chatId));
        message.setText("Choose the date");
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    public static Boolean trySwitchMonth(String action, Boolean ignoreErrors) {
        if (action.equals("Next")) {
            month++;
            if (month == 13) {
                month = 1;
                year++;
            }
            return true;
        }
        else if (action.equals("Previous")) {
            if (ignoreErrors || (month > currentMonth || year > currentYear)) {
                month--;
                if (month == 0) {
                    month = 12;
                    year--;
                }
                return true;
            }
        }
        return false;
    }

    private static String generateTextToButton(int dayNumber) {
        var day = Day.getDay(dayNumber, month, year);
        if (dayNumber == currentDay && month == currentMonth && year == currentYear && day != null && day.hasImportantTasks())
            return dayNumber + " \uD83D\uDCA3";
        else if (dayNumber == currentDay && month == currentMonth && year == currentYear)
            return dayNumber + " \uD83D\uDD25";
        else if (day != null && day.hasImportantTasks())
            return dayNumber + "❗️";
        else
            return Integer.toString(dayNumber);
    }
}
