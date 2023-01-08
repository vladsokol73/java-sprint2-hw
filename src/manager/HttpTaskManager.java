package manager;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import http.KVTaskClient;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class HttpTaskManager extends FileBackedTaskManager {
    private KVTaskClient kvTaskClient;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        try {
            kvTaskClient.save("task", json);
            json = gson.toJson(epics);
            kvTaskClient.save("epic", json);
            System.out.println("epics=: " + json);

            json = gson.toJson(subTasks);
            kvTaskClient.save("sub", json);
            System.out.println("subs=: " + json);

            int[] histId = new int[getHistory().size()];
            int i = 0;
            for (Task task : getHistory()) {
                histId[i] = task.getId();
                i++;
            }

            json = gson.toJson(histId);
            kvTaskClient.save("history", json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void load() throws IOException, InterruptedException {
        Task[] newTasks;
        Epic[] newEpics;
        SubTask[] newSubTasks;

        Gson gson = new Gson();
        String json = "";

        json = kvTaskClient.load("task");
        JsonElement jsonElement = JsonParser.parseString(json);
        if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return;
        }

        Type tasksMap = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();

        tasks = gson.fromJson(json, tasksMap);
        newTasks = new Task[tasks.size()];
        int i = 0;
        for (Integer id : tasks.keySet()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject(id.toString());
            newTasks[i] = gson.fromJson(jsonObject, Task.class);
            i++;
        }


        json = kvTaskClient.load("epic");
        jsonElement = JsonParser.parseString(json);
        if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return;
        }
        Type epicsMap = new TypeToken<HashMap<Integer, Epic>>() {
        }.getType();
        epics = gson.fromJson(json, epicsMap);
        newEpics = new Epic[epics.size()];
        i = 0;
        for (Integer id : epics.keySet()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject(id.toString());
            newEpics[i] = gson.fromJson(jsonObject, Epic.class);
            System.out.println("newEpic=: " + newEpics);
            i++;
        }

        json = kvTaskClient.load("sub");
        jsonElement = JsonParser.parseString(json);
        if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return;
        }

        Type subTasksMap = new TypeToken<HashMap<Integer, SubTask>>() {
        }.getType();
        subTasks = gson.fromJson(json, subTasksMap);
        newSubTasks = new SubTask[subTasks.size()];
        i = 0;
        for (Integer id : subTasks.keySet()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject(id.toString());
            newSubTasks[i] = gson.fromJson(jsonObject, SubTask.class);
            if (newSubTasks[i].getIdEpic() > 0) {
                epics.get(newSubTasks[i].getIdEpic()).addSubTask(newSubTasks[i]);
            }
            System.out.println("newSub=: " + newSubTasks);
        }
        System.out.println("subs_object=: " + subTasks);

        json = kvTaskClient.load("history");
        System.out.println("histJson=: " + json);
        jsonElement = JsonParser.parseString(json);
        if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON- array
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return;
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        int id = 0;
        for (int j = 0; j < jsonArray.size(); j++) {
            id = jsonArray.get(j).getAsInt();
            if (tasks.get(id) != null) {
                history.add(tasks.get(id));
            }
            if (epics.get(id) != null) {
                history.add(epics.get(id));
            }
            if (subTasks.get(id) != null) {
                history.add(subTasks.get(id));
            }
        }

    }

    @Override
    public void loadFromFile() {
    }

}
