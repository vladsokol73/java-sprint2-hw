package tests;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    @BeforeEach
    public void createObj() {
        taskManager = new FileBackedTaskManager(new File("resources//dump.csv"));
        super.createObj();

    }

    @Test
    public void loadFromFileTest() {
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        taskManager.addSubTaskToEpic(epic1, sub1);
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task1.getId());

        FileBackedTaskManager taskManager2 = new FileBackedTaskManager(new File("resources//dump.csv"));
        taskManager2.loadFromFile();
        Assertions.assertEquals(taskManager.getTaskById(task1.getId()).getId(), taskManager2.getTaskById(task1.getId()).getId()
                , "id исходной и восстановленной задачи должны совпадать");
        Assertions.assertEquals(taskManager.getSubTaskById(sub1.getId()).getId(), taskManager2.getSubTaskById(sub1.getId()).getId()
                , "id исходной и восстановленной подзадачи должны совпадать");
        Assertions.assertEquals(taskManager.getEpicById(epic1.getId()).getId(), taskManager2.getEpicById(epic1.getId()).getId()
                , "id исходного и восстановленного эпика должны совпадать");
        Assertions.assertEquals(taskManager.getSubTasksOfEpic(epic1).size()
                , taskManager2.getSubTasksOfEpic(taskManager2.getEpicById(epic1.getId())).size()
                , "количество подзадач исходного и восстановленного эпика должны совпадать");
    }
}