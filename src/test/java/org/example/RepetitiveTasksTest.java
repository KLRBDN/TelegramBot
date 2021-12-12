package org.example;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;

public class RepetitiveTasksTest {
    @Test
    public void tryAddTaskTest(){
        var task = createTask();
        assert task != null;
        RepetitiveTasks.tryAddTask(makeRepetitiveDate(), task);

        var day = new Day(LocalDate.of(2021, 12, 10));
        assert day.getTasks().contains(task);

        day = new Day(LocalDate.of(2021, 12, 11));
        assert day.getTasks().contains(task);

        day = new Day(LocalDate.of(2022, 1, 1));
        assert day.getTasks().contains(task);
    }

    @Test
    public void completeTaskTest(){
        var task = createTask();
        assert task != null;
        RepetitiveTasks.tryAddTask(makeRepetitiveDate(), task);

        var day = new Day(LocalDate.of(2021, 12, 10));
        day.completeTask(task.name);

        assert !day.getTasks().contains(task);
        var wasFound = false;
        for (Object[] taskObject :
                YearsDataBase.completedTasks) {
            var completedTask = (Task) taskObject[0];
            if (task == completedTask){
                wasFound = true;
                break;
            }
        }
        assert wasFound;

        day = new Day(LocalDate.of(2021, 12, 11));
        assert day.getTasks().contains(task);

        day = new Day(LocalDate.of(2022, 1, 1));
        assert day.getTasks().contains(task);
    }

    @Test
    public void tryCancelTaskTest(){
        RepetitiveTasks.tryAddTask(makeRepetitiveDate(), createTask());

        var botRequest = new CancelRepetitiveTask()
                .exec(null)
                .handle(makeUserAnswer("testName"), null);
        assert botRequest instanceof StandardBotRequest;
        assert ((SendMessage)botRequest.getRequestMessage())
                .getText().equals("Repetitive task was deleted successfully!");

        botRequest = new CancelRepetitiveTask()
                .exec(null)
                .handle(makeUserAnswer("task name which do not exist"), null);
        assert !(botRequest instanceof StandardBotRequest);
        assert ((SendMessage)botRequest.getRequestMessage())
                .getText().equals("Repetitive task was not found, please write task name again");
    }

    private Task createTask(){
        try {
            return new Task(new Time(0, 0), new Time(1, 0),
                    TaskType.overlapping, "testName", "this is test task");
        } catch (InvalidAttributeValueException e) {
            return null;
        }
    }

    private RepetitiveDate makeRepetitiveDate(){
        return new RepetitiveDate(
                new Boolean[] {false, false, false, false, false, false, false},
                new Boolean[] {false, false, false, false},
                LocalDate.of(2021, 12, 10),
                1, TimeUnit.day);
    }

    public static Update makeUserAnswer(String text){
        var currentChat = new Chat();
        currentChat.setId(1L);

        var messageForUserAnswer = new Message();
        messageForUserAnswer.setText(text);
        messageForUserAnswer.setChat(currentChat);

        var userAnswer = new Update();
        userAnswer.setMessage(messageForUserAnswer);

        return userAnswer;
    }
}
