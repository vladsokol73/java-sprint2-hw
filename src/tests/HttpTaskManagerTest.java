package tests;

import http.KVServer;
import manager.HttpTaskManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpTaskManagerTest extends TaskManagerTest {
    private static final String URL = "http://localhost:8081";
    private KVServer kvServer;

    @Override
    @BeforeEach
    public void createObj() {
        taskManager = new InMemoryTaskManager();
        super.createObj();
    }

    @Test
    public void putAndLoadTest() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            HttpTaskManager taskManager1 = new HttpTaskManager(URL);
            taskManager1.addTask(task1);
            taskManager1.addEpic(epic1);
            taskManager1.addSubTask(sub1);
            taskManager1.addSubTask(sub2);
            taskManager1.addSubTaskToEpic(epic1, sub1);
            taskManager1.getEpicById(epic1.getId());
            taskManager1.getTaskById(task1.getId());


            HttpTaskManager taskManager2 = new HttpTaskManager(URL);
            taskManager2.load();
            Assertions.assertEquals(taskManager2.getListTasks().length, taskManager1.getListTasks().length
                    , "количества задач в исходном и восстановленном менеджерах должны совпадать");
            Assertions.assertEquals(taskManager2.getListEpics().length, taskManager1.getListEpics().length
                    , "количества эпиков в исходном и восстановленном менеджерах должны совпадать");
            Assertions.assertEquals(taskManager2.getListSubTasks().length, taskManager1.getListSubTasks().length
                    , "количества подзадач в исходном и восстановленном менеджерах должны совпадать");
            Assertions.assertEquals(taskManager2.getHistory().size(), taskManager1.getHistory().size()
                    , "количества просмотров в исходном и восстановленном менеджерах должны совпадать");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        kvServer.stop();
    }
}
