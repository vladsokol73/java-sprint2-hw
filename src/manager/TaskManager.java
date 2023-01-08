package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int getNewId();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTaskToEpic(Epic epic, SubTask subTask);

    void addSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    boolean idIsFree(int id);

    List<SubTask> getSubTasksOfEpic(Epic epic);

    List<Task> getHistory();

    Task[] getListTasks();

    Task[] getListSubTasks();

    Task[] getListEpics();

    void load() throws IOException, InterruptedException;

    void loadFromFile();

    ArrayList<Task> getPrioritizedTasks();

    String checkOnCrossDates();
}
