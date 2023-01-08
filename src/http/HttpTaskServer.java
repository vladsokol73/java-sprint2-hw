package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTaskManager;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final String PATH = "resources//dump.csv";
    private TaskManager manager;
    private HttpServer httpServer;

    public HttpTaskServer() throws IOException, InterruptedException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        manager = new FileBackedTaskManager(new File(PATH));
        manager.loadFromFile();
        createCont();
    }

    private void createCont() {
        httpServer.createContext("/tasks", (h) -> {
            switch (h.getRequestMethod()) {
                case "POST":
                    post(h);
                    break;
                case "GET":
                    get(h);
                    break;
                case "DELETE":
                    delete(h);
                    break;
                default:
                    System.out.println("Обработка метода " + h.getRequestMethod() + " пока не реализована");
            }
        });
    }

    private void get(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        Gson gson = new Gson();
        String json = "";
        System.out.println(path);

        if (path.equals("/tasks/") && h.getRequestURI().getRawQuery() == null) {
            ArrayList<Task> sortedTasks = manager.getPrioritizedTasks();
            json = gson.toJson(sortedTasks);
            sendText(h, json);
        }

        if (path.equals("/tasks/") && h.getRequestURI().getRawQuery().equals("checkCrossDate=yes")) {
            sendText(h, manager.checkOnCrossDates());
        }

        if (path.equals("/tasks/history") && h.getRequestURI().getRawQuery() == null) {
            json = gson.toJson(manager.getHistory());
            sendText(h, json);
        }

        if (path.equals("/tasks/task/") && h.getRequestURI().getRawQuery() == null) {
            Task[] tasks = manager.getListTasks();
            json = gson.toJson(tasks);
            sendText(h, json);
        }

        if (path.equals("/tasks/task/") && h.getRequestURI().getRawQuery().contains("id=")) {
            int id = Integer.parseInt(h.getRequestURI().getRawQuery().substring("id=".length()));
            Task task = manager.getTaskById(id);
            json = gson.toJson(task);
            sendText(h, json);
        }

        if (path.equals("/tasks/subtask/") && h.getRequestURI().getRawQuery() == null) {
            Task[] subtasks = manager.getListSubTasks();
            json = gson.toJson(subtasks);
            sendText(h, json);
        }

        if (path.equals("/tasks/subtask/") && h.getRequestURI().getRawQuery().contains("id=")) {
            int id = Integer.parseInt(h.getRequestURI().getRawQuery().substring("id=".length()));
            SubTask subtask = manager.getSubTaskById(id);
            json = gson.toJson(subtask);
            sendText(h, json);
        }

        if (path.equals("/tasks/epic/") && h.getRequestURI().getRawQuery() == null) {
            Task[] epics = manager.getListEpics();
            json = gson.toJson(epics);
            sendText(h, json);
        }

        if (path.equals("/tasks/epic/") && h.getRequestURI().getRawQuery().contains("id=")) {
            int id = Integer.parseInt(h.getRequestURI().getRawQuery().substring("id=".length()));
            Epic epic = manager.getEpicById(id);
            json = gson.toJson(epic);
            sendText(h, json);
        }

    }

    private void post(HttpExchange h) throws IOException {
        String value = new String(h.getRequestBody().readAllBytes(), "UTF-8");
        String path = h.getRequestURI().getPath();
        Gson gson = new Gson();
        System.out.println("request body: " + value);

        if (path.equals("/tasks/task/")) {
            Task task = gson.fromJson(value, Task.class);
            if (h.getRequestURI().getRawQuery() == null) {
                manager.addTask(task);
                System.out.println("Test POST method: " + manager.getTaskById(1));
                sendText(h, "Task added");
            } else if (h.getRequestURI().getRawQuery().equals("update=yes")) {
                manager.updateTask(task);
                System.out.println("Test POST method: " + manager.getTaskById(1));
                sendText(h, "Task updated");
            }
        }

        if (path.equals("/tasks/subtask/")) {
            SubTask subTask = gson.fromJson(value, SubTask.class);
            if (h.getRequestURI().getRawQuery() == null) {
                manager.addTask(subTask);
                System.out.println("Test POST method: " + manager.getSubTaskById(5));
                sendText(h, "SubTask added");
            } else if (h.getRequestURI().getRawQuery().equals("update=yes")) {
                manager.updateSubTask(subTask);
                System.out.println("Test POST method: " + manager.getSubTaskById(5));
                sendText(h, "SubTask updated");
            }
        }

        if (path.equals("/tasks/epic/")) {
            Epic epic = gson.fromJson(value, Epic.class);
            if (h.getRequestURI().getRawQuery() == null) {
                manager.addEpic(epic);
                System.out.println("Test POST method: " + manager.getEpicById(3));
                sendText(h, "Epic added");
            } else if (h.getRequestURI().getRawQuery().equals("update=yes")) {
                manager.updateEpic(epic);
                System.out.println("Test POST method: " + manager.getEpicById(3));
                sendText(h, "Epic updated");
            }
        }

        if (path.equals("/tasks/epic/sub/") && h.getRequestURI().getRawQuery().contains("subId=")) {
            int idSub = Integer.parseInt(h.getRequestURI().getRawQuery().substring("subId=".length()));
            Epic epic = gson.fromJson(value, Epic.class);
            SubTask sub = manager.getSubTaskById(idSub);
            manager.addSubTaskToEpic(epic, sub);
        }
    }

    private void delete(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        if (path.equals("/tasks/task/") && h.getRequestURI().getRawQuery().contains("id=")) {
            int id = Integer.parseInt(h.getRequestURI().getRawQuery().substring("id=".length()));
            manager.removeTask(id);
            sendText(h, "Task deleted");
            for (Task task : manager.getListTasks()) {
                System.out.println(task);
            }
        } else if (path.equals("/tasks/task/") && h.getRequestURI().getRawQuery().equals("all=yes")) {
            manager.removeAllTasks();
            sendText(h, "All tasks deleted");
            System.out.println("Количество задач в менеджере: " + manager.getListTasks().length);
        }

        if (path.equals("/tasks/subtask/") && h.getRequestURI().getRawQuery().contains("id=")) {
            int id = Integer.parseInt(h.getRequestURI().getRawQuery().substring("id=".length()));
            manager.removeSubTask(id);
            sendText(h, "SubTask deleted");
            for (Task subtask : manager.getListSubTasks()) {
                System.out.println(subtask);
            }
        } else if (path.equals("/tasks/subtask/") && h.getRequestURI().getRawQuery().equals("all=yes")) {
            manager.removeAllSubTasks();
            sendText(h, "All subtasks deleted");
            System.out.println("Количество подзадач в менеджере: " + manager.getListSubTasks().length);
        }

        if (path.equals("/tasks/epic/") && h.getRequestURI().getRawQuery().contains("id=")) {
            int id = Integer.parseInt(h.getRequestURI().getRawQuery().substring("id=".length()));
            manager.removeEpic(id);
            sendText(h, "Epic deleted");
            for (Task epic : manager.getListEpics()) {
                System.out.println(epic);
            }
        } else if (path.equals("/tasks/epic/") && h.getRequestURI().getRawQuery().equals("all=yes")) {
            manager.removeAllEpics();
            sendText(h, "All epics deleted");
            System.out.println("Количество эпиков в менеджере: " + manager.getListEpics().length);
        }
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes("UTF-8");
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
