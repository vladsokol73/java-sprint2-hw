import http.HttpTaskClient;
import http.HttpTaskServer;
import http.KVServer;
import manager.FileBackedTaskManager;
import manager.HttpTaskManager;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import util.Managers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    private static final String PATH = "resources//dump.csv";

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TaskManager manager1 = new FileBackedTaskManager(new File( PATH));

        Task task1 = new Task(manager1.getNewId(), Status.NEW, "task1", "example"
                , LocalDateTime.of(2022, 1, 10, 9, 30), Duration.ofMinutes(30));
        Task task2 = new Task(manager1.getNewId(), Status.IN_PROGRESS, "task2", "task 2"
                , LocalDateTime.of(2022, 1, 10, 11, 30), Duration.ofMinutes(30));

        manager1.addTask(task1);
        manager1.addTask(task2);
        System.out.println("Список задач:");
        printList(manager1, 1);

        Epic epic1 = new Epic(manager1.getNewId(), Status.NEW, "epic1", "Финальное задание спринт 2");
        Epic epic2 = new Epic(manager1.getNewId(), Status.IN_PROGRESS, "epic2", "Обучение Java");
        //статус = выполняется, но т.к. пока не добавлены подзадачи вычисляемое будет NEW
        manager1.addEpic(epic1);
        manager1.addEpic(epic2);
        System.out.println("Список эпиков:");
        printList(manager1, 2);

        SubTask subTask1 = new SubTask(manager1.getNewId(), Status.DONE, "sub1"
                , "Разработка приложения"
                , LocalDateTime.of(2022, 2, 10, 9, 30), Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask(manager1.getNewId(), Status.DONE, "sub2", "Теория"
                , LocalDateTime.of(2022, 2, 11, 9, 30), Duration.ofMinutes(30));
        SubTask subTask3 = new SubTask(manager1.getNewId(), Status.DONE, "sub3", "Практические задания"
                , LocalDateTime.of(2022, 2, 11, 9, 45), Duration.ofMinutes(30));
        manager1.addSubTask(subTask1);
        manager1.addSubTask(subTask2);
        manager1.addSubTask(subTask3);

        System.out.println("Список подзадач:");
        printList(manager1, 3);

        manager1.addSubTaskToEpic(epic1, subTask1);
        manager1.addSubTaskToEpic(epic1, subTask2);
        manager1.addSubTaskToEpic(epic1, subTask3);
        System.out.println("____________Добавим подзадачи к эпику 1_____________");
        printEpic(manager1, epic1);

        System.out.println("___________________");
        task1 = manager1.getTaskById(1);

        TaskManager manager2 = new FileBackedTaskManager(new File(PATH));
        manager2.loadFromFile();

        System.out.println("Задачи из файла_________");
        printList(manager2, 1);
        printList(manager2, 2);
        printList(manager2, 3);
        printHistory(manager2);
        subTask1.setEpic(null);

        System.out.println("Сортировка по дате начала:");
        for (Task task : manager1.getPrioritizedTasks()) {
            System.out.println(task.getId() + " Start: " + task.getStartDate());
        }

        System.out.println("Проверка на пересечения по времени:");
        System.out.println(manager1.checkOnCrossDates());
        //==============================================
        //====HTTP


        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        HttpTaskClient httpTaskClient = new HttpTaskClient();

        String url = "http://localhost:8080/tasks/task/";
        httpTaskClient.sendQuery(url,"GET",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/task/?id=1";
        httpTaskClient.sendQuery(url,"GET",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/subtask/";
        httpTaskClient.sendQuery(url,"GET",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/subtask/?id=5";
        httpTaskClient.sendQuery(url,"GET",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/epic/";
        httpTaskClient.sendQuery(url,"GET",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/epic/?id=4";
        httpTaskClient.sendQuery(url,"GET",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/task/";
        httpTaskClient.sendQuery(url,"POST",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/task/?update=yes";
        httpTaskClient.sendQuery(url,"POST",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/subtask/";
        httpTaskClient.sendQuery(url,"POST",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/subtask/?update=yes";
        httpTaskClient.sendQuery(url,"POST",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/epic/";
        httpTaskClient.sendQuery(url,"POST",task1,subTask1,epic2);

        url = "http://localhost:8080/tasks/epic/?update=yes";
        httpTaskClient.sendQuery(url,"POST",task1,subTask1,epic2);

        //По времени
        url = "http://localhost:8080/tasks/";
        httpTaskClient.sendQuery(url,"GET",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/?checkCrossDate=yes";
        httpTaskClient.sendQuery(url,"GET",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/task/?id=1";
        httpTaskClient.sendQuery(url,"DELETE",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/task/?all=yes";
        httpTaskClient.sendQuery(url,"DELETE",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/subtask/?id=5";
        httpTaskClient.sendQuery(url,"DELETE",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/subtask/?all=yes";
        httpTaskClient.sendQuery(url,"DELETE",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/epic/?id=3";
        httpTaskClient.sendQuery(url,"DELETE",task1,subTask1,epic1);

        url = "http://localhost:8080/tasks/epic/?all=yes";
        httpTaskClient.sendQuery(url,"DELETE",task1,subTask1,epic1);

        //===================================
        System.out.println("_____________________<<<<<HttpTaskManager>>>>>____________________");
        KVServer kvServer = new KVServer();
        kvServer.start();

        HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault();
        httpTaskManager.addTask(task1);
        httpTaskManager.addTask(task2);
        httpTaskManager.addEpic(epic1);
        httpTaskManager.addEpic(epic2);
        httpTaskManager.addSubTask(subTask1);
        httpTaskManager.addSubTask(subTask2);
        httpTaskManager.addSubTask(subTask3);
        httpTaskManager.getTaskById(1);
        httpTaskManager.getEpicById(3);
        System.out.println("текущее состояние менеджера: " +
                "2 таска, 2 эпика, 3 саба. 2 просмотра. Далее загрузим состояние с веб");
        HttpTaskManager newHttpManager = (HttpTaskManager) Managers.getDefault();
        newHttpManager.load();
        printList(newHttpManager,1);
        printList(newHttpManager,2);
        printList(newHttpManager,3);
        printHistory(newHttpManager);

        kvServer.stop();
        httpTaskServer.stop();
    }

    public static void printList(TaskManager inMemoryTaskManager, int type) {// 1 - tasks.Task, 2 - tasks.Epic, 3 - tasks.SubTask
        Task[] list = null;
        switch (type) {
            case 1:
                list = inMemoryTaskManager.getListTasks();
                break;
            case 2:
                list = inMemoryTaskManager.getListEpics();
                break;
            case 3:
                list = inMemoryTaskManager.getListSubTasks();
        }

        for (int i = 0; i < list.length; i++) {
            System.out.println("Id = " + list[i].getId() + "; Name: " + list[i].getName()
                    + "; Description: " + list[i].getDescription() + "; Status: " + list[i].getStatus()
                    + "; StartDate: " + list[i].getStartDate() + "; EndDate: " + list[i].getEndDate());
        }
    }

    public static void printEpic(TaskManager inMemoryTaskManager, Epic epic) {
        System.out.println("Name = " + epic.getName() + "; Description = " + epic.getDescription()
                + "; Status = " + epic.getStatus() + "; StartDate: " + epic.getStartDate() + "; EndDate: " + epic.getEndDate());
        if (epic.getSubTasks().size() > 0) {
            System.out.println(("Эпик содержит подзадачи: "));
            for (SubTask sub : epic.getSubTasks()) {
                System.out.println("Id = " + sub.getId() + "; Name = " + sub.getName() + "; Status = "
                        + sub.getStatus());
            }
        }
    }

    public static void printHistory(TaskManager tm) {
        int i = 0;
        System.out.println("История просмотров: ");
        for (Task task : tm.getHistory()) {
            i++;
            System.out.println("N" + i + " " + task.getId() + " " + task.getDescription());
        }
    }

}
