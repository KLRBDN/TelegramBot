package org.example;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddRepetitiveTaskTest {
    @Test
    public void keyboardStateTest(){
        keyboardStateTest("repeat period", 2, 5, 5,
                "repeat period-2", 3, 1, 2, 1);
        keyboardStateTest("time unit", 2, 2, 2,
                "time unit-1", 4, 1, 2, 7);
        keyboardStateTest("start day", 7, 3, 7,
                "10.12.2021", 3, 1, 2, 1);
    }

    private void keyboardStateTest(String firstCallbackData, int firstRowsCount,
                                   int firstKeyboardFirstRowSize, int firstKeyboardSecondRowSize,
                                   String secondCallbackData, int secondRowsCount,
                                   int secondKeyboardFirstRowSize, int secondKeyboardSecondRowSize, int secondKeyboardThirdRowSize){
        var userAnswer = RepetitiveTasksTest.makeUserAnswer("/addrepetitive");
        var repetitiveDateRequest = new AddRepetitiveTask().exec(userAnswer);
        assert(repetitiveDateRequest.getRequestMessage() instanceof SendMessage);

        userAnswer.setCallbackQuery(new CallbackQuery(
                null, null, null, null, firstCallbackData, null, null));
        userAnswer.getCallbackQuery().setMessage(userAnswer.getMessage());
        repetitiveDateRequest = repetitiveDateRequest.handle(userAnswer, null);

        assert(repetitiveDateRequest.getRequestMessage() instanceof EditMessageReplyMarkup);
        var dateConstructorKeyboard =
                ((EditMessageReplyMarkup) repetitiveDateRequest.getRequestMessage()).getReplyMarkup().getKeyboard();
        assert(dateConstructorKeyboard.size() == firstRowsCount);
        assert(dateConstructorKeyboard.get(0).size() == firstKeyboardFirstRowSize);
        assert(dateConstructorKeyboard.get(1).size() == firstKeyboardSecondRowSize);

        userAnswer.setCallbackQuery(new CallbackQuery(
                null, null, null, null, secondCallbackData, null, null));
        userAnswer.getCallbackQuery().setMessage(userAnswer.getMessage());
        repetitiveDateRequest = repetitiveDateRequest.handle(userAnswer, null);

        assert(repetitiveDateRequest.getRequestMessage() instanceof EditMessageReplyMarkup);
        dateConstructorKeyboard = ((EditMessageReplyMarkup)repetitiveDateRequest.getRequestMessage())
                .getReplyMarkup().getKeyboard();
        assert(dateConstructorKeyboard.size() == secondRowsCount);
        assert(dateConstructorKeyboard.get(0).size() == secondKeyboardFirstRowSize);
        assert(dateConstructorKeyboard.get(1).size() == secondKeyboardSecondRowSize);
        assert(dateConstructorKeyboard.get(2).size() == secondKeyboardThirdRowSize);

        if (secondCallbackData.equals("repeat period-2")){
            assert(dateConstructorKeyboard.get(1).get(0).getText().equals("Repeat every 2"));
        }
        if (secondCallbackData.equals("time unit-1")) {
            assert(dateConstructorKeyboard.get(1).get(1).getText().equals("week"));
            pickingButtonsText(repetitiveDateRequest, userAnswer);
        }
    }

    private void pickingButtonsText(BotRequest repetitiveDateRequest, Update userAnswer) {
        repetitiveDateRequest = pickButton(repetitiveDateRequest, userAnswer, "dayOfWeek 1", 0, true);
        repetitiveDateRequest = pickButton(repetitiveDateRequest, userAnswer, "dayOfWeek 2", 1, true);
        pickButton(repetitiveDateRequest, userAnswer, "dayOfWeek 1", 0, false);
    }

    private BotRequest pickButton(BotRequest repetitiveDateRequest, Update userAnswer, String callbackData, int buttonIndex, Boolean wasPicked){
        userAnswer.setCallbackQuery(new CallbackQuery(
                null, null, null, null, callbackData, null, null));
        userAnswer.getCallbackQuery().setMessage(userAnswer.getMessage());
        repetitiveDateRequest = repetitiveDateRequest.handle(userAnswer, null);

        assert(repetitiveDateRequest.getRequestMessage() instanceof EditMessageReplyMarkup);
        var dateConstructorKeyboard = ((EditMessageReplyMarkup)repetitiveDateRequest.getRequestMessage())
                .getReplyMarkup().getKeyboard();
        Assertions.assertEquals(wasPicked, dateConstructorKeyboard.get(2).get(buttonIndex).getText().contains(" \uD83D\uDCA3"));

        return repetitiveDateRequest;
    }
}
