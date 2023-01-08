package tests;

import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Epic epic1;
    protected Epic epic2;
    protected SubTask sub1;
    protected SubTask sub2;
    protected SubTask sub3;
    protected Task task1;
    protected Task task2;

    @BeforeEach
    protected void createObj() {
        epic1 = new Epic(taskManager.getNewId(), Status.NEW, "epic1", "test");
        epic2 = new Epic(taskManager.getNewId(), Status.NEW, "epic2", "test");
        sub1 = new SubTask(taskManager.getNewId(), Status.NEW, "sub1", "test"
                , LocalDateTime.of(2022, 1, 1, 0, 0), Duration.ofMinutes(30));
        sub2 = new SubTask(taskManager.getNewId(), Status.NEW, "sub2", "test"
                , LocalDateTime.of(2022, 1, 2, 0, 0), Duration.ofMinutes(30));
        sub3 = new SubTask(taskManager.getNewId(), Status.NEW, "sub3", "test"
                , LocalDateTime.of(2022, 1, 2, 0, 10), Duration.ofMinutes(30));
        task1 = new Task(taskManager.getNewId(), Status.NEW, "task1", "test"
                , LocalDateTime.of(2022, 1, 3, 0, 10), Duration.ofMinutes(30));
        task2 = new Task(taskManager.getNewId(), Status.NEW, "task1", "test"
                , LocalDateTime.of(2022, 1, 4, 0, 10), Duration.ofMinutes(30));
    }

    @AfterEach
    protected void clearHistory() {
        taskManager.removeAllEpics();
        taskManager.removeAllSubTasks();
        taskManager.removeAllTasks();
    }

    @Test
    protected void getNewIdTest() {
        int i = taskManager.getNewId();
        Assertions.assertEquals(task2.getId() + 1, i, "неверная генерация уникального id");
    }

    @Test
    protected void removeAllTasksTest() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.removeAllTasks();
        Assertions.assertEquals(0, taskManager.getListTasks().length, "неправильная работа метода removeAllTasks()");
    }

    @Test
    protected void removeAllEpicsTest() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.removeAllEpics();
        Assertions.assertEquals(0, taskManager.getListEpics().length, "неправильная работа метода removeAllEpics()");
    }

    @Test
    protected void removeAllSubTasksTest() {
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        taskManager.removeAllSubTasks();
        Assertions.assertEquals(0, taskManager.getListSubTasks().length, "неправильная работа метода removeAllSubTasks()");
    }

    @Test
    protected void getTaskByIdTest() {
        taskManager.addTask(task1);
        task2 = taskManager.getTaskById(task1.getId());
        Assertions.assertEquals(task1.getId(), task2.getId(), "неправильная работа метода getTaskById");
    }

    @Test
    protected void getEpicByIdTest() {
        taskManager.addEpic(epic1);
        epic2 = taskManager.getEpicById(epic1.getId());
        Assertions.assertEquals(epic1.getId(), epic2.getId(), "неправильная работа метода getEpicById");
    }

    @Test
    protected void getSubTaskByIdTest() {
        taskManager.addSubTask(sub1);
        sub2 = taskManager.getSubTaskById(sub1.getId());
        Assertions.assertEquals(sub1.getId(), sub2.getId(), "неправильная работа метода getSubTaskById");
    }

    @Test
    protected void addTaskTest() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Assertions.assertEquals(2, taskManager.getListTasks().length, "количество задач должно быть = 2");
        taskManager.addTask(task2);
        Assertions.assertEquals(2, taskManager.getListTasks().length
                , "количество задач должно быть = 2, проверка с добавлением уже существующего id");
    }

    @Test
    protected void addEpicTest() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Assertions.assertEquals(2, taskManager.getListEpics().length, "количество epics должно быть = 2");
        taskManager.addEpic(epic2);
        Assertions.assertEquals(2, taskManager.getListEpics().length
                , "количество epics должно быть = 2, проверка с добавлением уже существующего id");
    }

    @Test
    protected void addSubTaskToEpicTest() {
        taskManager.addEpic(epic1);
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        taskManager.addSubTaskToEpic(epic1, sub1);
        taskManager.addSubTaskToEpic(epic1, sub2);
        Assertions.assertEquals(2, taskManager.getSubTasksOfEpic(epic1).size()
                , "количество подзадач эпика должно быть = 2");
        Assertions.assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).getStatus()
                , "статус эпика должен быть = NEW");
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        Assertions.assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).getStatus()
                , "статус эпика должен быть = DONE");
        taskManager.addSubTask(sub3);
        taskManager.addSubTaskToEpic(epic1, sub3);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus()
                , "статус эпика должен быть = IN_PROGRESS");
    }

    @Test
    protected void addSubTaskTest() {
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        Assertions.assertEquals(2, taskManager.getListSubTasks().length, "количество подзадач должно быть = 2");
        taskManager.addSubTask(sub2);
        Assertions.assertEquals(2, taskManager.getListSubTasks().length
                , "количество подзадач должно быть = 2, проверка с добавлением уже существующего id");
    }

    @Test
    protected void updateTaskTest() {
        Task task4 = new Task(task1.getId(), Status.NEW, "task4", "update"
                , LocalDateTime.of(2022, 1, 10, 10, 30), Duration.ofMinutes(30));
        taskManager.addTask(task1);
        taskManager.updateTask(task4);
        Assertions.assertEquals("task4", taskManager.getTaskById(task1.getId()).getName()
                , "имя задачи должно быть 'task4'");
    }

    @Test
    protected void updateEpicTest() {
        Epic epic3 = new Epic(epic1.getId(), Status.NEW, "epic3", "update");
        taskManager.addEpic(epic1);
        taskManager.updateEpic(epic3);
        Assertions.assertEquals("epic3", taskManager.getEpicById(epic1.getId()).getName(), "имя эпика должно быть 'epic3'");
    }

    @Test
    protected void updateSubTaskTest() {
        SubTask sub4 = new SubTask(sub1.getId(), Status.NEW, "subtask4", "update"
                , LocalDateTime.of(2022, 1, 10, 10, 30), Duration.ofMinutes(30));
        taskManager.addSubTask(sub1);
        taskManager.updateSubTask(sub4);
        Assertions.assertEquals("subtask4", taskManager.getSubTaskById(sub1.getId()).getName()
                , "имя задачи должно быть 'subtask4'");
    }

    @Test
    protected void removeTaskTest() {
        taskManager.addTask(task1);
        taskManager.removeTask(task1.getId());
        Assertions.assertEquals(0, taskManager.getListTasks().length, "размер списка задач должен быть = 0");
    }

    @Test
    protected void removeEpicTest() {
        taskManager.addEpic(epic1);
        taskManager.removeEpic(epic1.getId());
        Assertions.assertEquals(0, taskManager.getListEpics().length, "размер списка эпиков должен быть = 0");
    }

    @Test
    protected void removeSubTaskTest() {
        taskManager.addSubTask(sub1);
        taskManager.removeSubTask(sub1.getId());
        Assertions.assertEquals(0, taskManager.getListSubTasks().length, "размер списка подзадач должен быть = 0");
    }


    @Test
    protected void idIsFreeTest() {
        Assertions.assertEquals(true, taskManager.idIsFree(task1.getId()), "id задачи должно быть доступно");
    }

    @Test
    protected void getSubTasksOfEpicTest() {
        taskManager.addEpic(epic1);
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        taskManager.addSubTaskToEpic(epic1, sub1);
        taskManager.addSubTaskToEpic(epic1, sub2);
        Assertions.assertEquals(2, taskManager.getSubTasksOfEpic(epic1).size()
                , "размер списка подзадач эпика должен быть = 2");
    }

    @Test
    protected void getHistoryTest() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(2, taskManager.getHistory().size(), "размер истории должен быть = 2");
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(2, taskManager.getHistory().size()
                , "размер истории должен быть = 2 - проверка на повторный просмотр");
        taskManager.removeTask(task2.getId());
        Assertions.assertEquals(1, taskManager.getHistory().size(), "размер истории должен быть = 1");
    }

    @Test
    protected void getListTasksTest() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Assertions.assertEquals(2, taskManager.getListTasks().length, "размер списка задач должен быть = 2");
    }

    @Test
    protected void getListSubTasksTest() {
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        Assertions.assertEquals(2, taskManager.getListSubTasks().length, "размер списка подзадач должен быть = 2");
    }

    @Test
    protected void getListEpicsTest() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Assertions.assertEquals(2, taskManager.getListEpics().length, "размер списка эпиков должен быть = 2");
    }

    @Test
    public void getPrioritizedTasksTest() {
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        taskManager.addSubTask(sub3);
        ArrayList<Task> list = new ArrayList<>(taskManager.getPrioritizedTasks());
        Assertions.assertEquals(sub1, list.get(0), "правильная сортировка по дате: sub1,sub2,sub3");
        Assertions.assertEquals(sub2, list.get(1), "правильная сортировка по дате: sub1,sub2,sub3");
        Assertions.assertEquals(sub3, list.get(2), "правильная сортировка по дате: sub1,sub2,sub3");
    }

    @Test
    public void checkOnCrossDatesTest() {
        taskManager.addSubTask(sub2);
        taskManager.addSubTask(sub3);
        Assertions.assertEquals(true, taskManager.checkOnCrossDates().length() > 40
                , "если найдено пересечение по времени длина сообщения > 40 символов");
    }
}
