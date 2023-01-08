package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;

public class EpicTest {
    private Epic epic;
    private SubTask sub1;
    private SubTask sub2;
    private SubTask sub3;

    @BeforeEach
    void createTasks() {
        epic = new Epic(1, Status.NEW, "epic1", "test");
        sub1 = new SubTask(2, Status.NEW, "sub1", "test"
                , LocalDateTime.of(2022, 1, 1, 0, 0), Duration.ofMinutes(30));
        sub2 = new SubTask(3, Status.NEW, "sub2", "test"
                , LocalDateTime.of(2022, 1, 2, 0, 0), Duration.ofMinutes(30));
        sub3 = new SubTask(4, Status.NEW, "sub3", "test"
                , LocalDateTime.of(2022, 1, 2, 0, 10), Duration.ofMinutes(30));
    }

    @Test
    void emptyListSubtask() {
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "неверный статус эпика");
    }

    @Test
    void allSubtasksWithNEWStatus() {
        epic.addSubTask(sub1);
        epic.addSubTask(sub2);
        epic.addSubTask(sub3);
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "неверный статус эпика");
    }

    @Test
    void allSubtasksWithDONEStatus() {
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        sub3.setStatus(Status.DONE);
        epic.addSubTask(sub1);
        epic.addSubTask(sub2);
        epic.addSubTask(sub3);
        Assertions.assertEquals(Status.DONE, epic.getStatus(), "неверный статус эпика");
    }

    @Test
    void subtasksWithDONEandNEWStatus() {
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        sub3.setStatus(Status.NEW);
        epic.addSubTask(sub1);
        epic.addSubTask(sub2);
        epic.addSubTask(sub3);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "неверный статус эпика");
    }

    @Test
    void subtasksWithDONEandINPROGRESStatus() {
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        sub3.setStatus(Status.IN_PROGRESS);
        epic.addSubTask(sub1);
        epic.addSubTask(sub2);
        epic.addSubTask(sub3);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "неверный статус эпика");
    }
}
