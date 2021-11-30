package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KeyboardConfiguration {
    private static int monthNumber;
    private static int year;
    private static int currentYear;
    private static int currentMonthNumber;

    public KeyboardConfiguration() {
        var dateSplitted = Day.getTodayDate().split("\\.");
        currentMonthNumber = Integer.parseInt(dateSplitted[1]);
        monthNumber = currentMonthNumber;
        currentYear = Integer.parseInt(dateSplitted[2]);
        year = currentYear;
    }

    // Это переписать под новую реализацию
    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var yearsDataBase = YearsDataBase.getInstance();
        var days = yearsDataBase.getYear(year).getMonth(monthNumber).getAllDays();
        List<InlineKeyboardButton> buttonRow = new ArrayList<>(7);
        List<List<InlineKeyboardButton>> buttonRowList = new ArrayList<>();
        var managerButtonNext = new InlineKeyboardButton();
        managerButtonNext.setText("Next month >");
        managerButtonNext.setCallbackData("Next");
        var managerButtonPrevious = new InlineKeyboardButton();
        managerButtonPrevious.setText("< Previous Month");
        managerButtonPrevious.setCallbackData("Previous");
        var monthButton = new InlineKeyboardButton();
        monthButton.setText(Month.of(monthNumber).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + year);
        monthButton.setCallbackData("null");
        buttonRow.add(managerButtonPrevious);
        buttonRow.add(monthButton);
        buttonRow.add(managerButtonNext);
        buttonRowList.add(buttonRow);
        buttonRow = new ArrayList<>(7);
        for (int i = 1; i <= days.length; i++) {
            var button = new InlineKeyboardButton();
            button.setText(Integer.toString(i));
            button.setCallbackData(i + "." + monthNumber + "." + year);
            buttonRow.add(button);
            if (i % 7 == 0) {
                buttonRowList.add(buttonRow);
                buttonRow = new ArrayList<>(7);
            }
        }
        if (!buttonRow.isEmpty()) {
            for (int i = buttonRow.size(); i < 7; i++) {
                var emptyButton = new InlineKeyboardButton();
                emptyButton.setText(" ");
                emptyButton.setCallbackData("null");
                buttonRow.add(emptyButton);
            }
            buttonRowList.add(buttonRow);
        }
        inlineKeyboardMarkup.setKeyboard(buttonRowList);
        var message = new SendMessage();
        message.setChatId(Long.toString(chatId));
        message.setText("Choose the date of task to complete");
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    public Boolean SwitchMonth(String action) {
        if (action.equals("Next")) {
            monthNumber++;
            if (monthNumber == 13) {
                monthNumber = 1;
                year++;
            }
            return true;
        }
        else if (action.equals("Previous")) {
            if (monthNumber > currentMonthNumber || year > currentYear) {
                monthNumber--;
                if (monthNumber == 0) {
                    monthNumber = 12;
                    year--;
                }
                return true;
            }
        }
        return false;
    }
}
