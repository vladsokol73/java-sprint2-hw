package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskClient {
    public void sendQuery(String uri, String method, Task task, SubTask subTask, Epic epic) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(uri);
        Gson gson = new Gson();
        String json = "";

        if (method.equals("POST")) {
            if (uri.contains("/task/")) {
                json = gson.toJson(task);
            } else if (uri.contains("/subtask/")) {
                json = gson.toJson(subTask);
            } else if (uri.contains("/epic/")) {
                json = gson.toJson(epic);
            }
            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        if (method.equals("GET")) {
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            if (!request.uri().getPath().equals("/tasks/epic/") && request.uri().getRawQuery() == null) {
                Type listType = new TypeToken<ArrayList<Task>>() {
                }.getType();
                List<Task> taskList = new Gson().fromJson(response.body(), listType);
                for (Task task$ : taskList) {
                    System.out.println(task$);
                }
            } else if (request.uri().getPath().equals("/tasks/epic/") && request.uri().getRawQuery() == null) {
                Type listType = new TypeToken<ArrayList<Epic>>() {
                }.getType();
                List<Epic> epicList = new Gson().fromJson(response.body(), listType);
                for (Epic epic$ : epicList) {
                    System.out.println(epic$);
                }
            } else if (request.uri().getPath().equals("/tasks/task/") && request.uri().getRawQuery().contains("id=")) {
                Task task1 = gson.fromJson(response.body(), Task.class);
                System.out.println(task1);
            } else if (request.uri().getPath().equals("/tasks/") && request.uri().getRawQuery().equals("checkCrossDate=yes")) {
                System.out.println(response.body());
            } else if (request.uri().getPath().equals("/tasks/subtask/") && request.uri().getRawQuery().contains("id=")) {
                SubTask subTask$ = gson.fromJson(response.body(), SubTask.class);
                System.out.println(subTask$);
            } else if (request.uri().getPath().equals("/tasks/epic/") && request.uri().getRawQuery().contains("id=")) {
                Epic epic$ = gson.fromJson(response.body(), Epic.class);
            }


        }

        if (method.equals("DELETE")) {
            HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json").DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

        }
    }
}
