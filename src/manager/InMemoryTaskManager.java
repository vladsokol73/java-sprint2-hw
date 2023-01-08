package manager;

import history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Managers;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int newId;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager history;
    protected final ArrayList<Task> listForDelete;

    private Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            Long x1 = o1.getStartDate().toEpochSecond(ZoneOffset.ofHours(0));
            Long x2 = o2.getStartDate().toEpochSecond(ZoneOffset.ofHours(0));
            return (int) (x1 - x2);
        }
    };

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);

    public InMemoryTaskManager() {
        newId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        history = Managers.getDefaultHistory();
        listForDelete = new ArrayList<>();
    }

    private int searchMaxId(Set<Integer> keySet) {
        int max = 0;
        for (Integer id : keySet) {
            if (max < id) {
                max = id;
            }
        }
        return max;
    }

    @Override
    public int getNewId() {
        int maxTask = searchMaxId(tasks.keySet());
        int maxEpic = searchMaxId(epics.keySet());
        int maxSub = searchMaxId(subTasks.keySet());
        if (newId < maxTask) {
            newId = maxTask;
        }
        if (newId < maxEpic) {
            newId = maxEpic;
        }
        if (newId < maxSub) {
            newId = maxSub;
        }

        newId++;
        return newId;
    }

    @Override
    public Task[] getListTasks() {
        int id = 0;
        Task[] list = null;
        list = new Task[tasks.size()];
        for (Integer taskKey : tasks.keySet()) {
            list[id] = new Task(taskKey, tasks.get(taskKey).getStatus()
                    , tasks.get(taskKey).getName(), tasks.get(taskKey).getDescription()
                    , tasks.get(taskKey).getStartDate(), tasks.get(taskKey).getDuration());
            id++;
        }
        return list;
    }

    @Override
    public Epic[] getListEpics() {
        int id = 0;
        Epic[] list = new Epic[epics.size()];
        for (Integer epicKey : epics.keySet()) {
            list[id] = new Epic(epicKey, epics.get(epicKey).getStatus()
                    , epics.get(epicKey).getName(), epics.get(epicKey).getDescription());
            id++;
        }
        return list;
    }

    @Override
    public SubTask[] getListSubTasks() {
        int id = 0;
        SubTask[] list = new SubTask[subTasks.size()];
        for (Integer subTaskKey : subTasks.keySet()) {
            list[id] = new SubTask(subTaskKey, subTasks.get(subTaskKey).getStatus()
                    , subTasks.get(subTaskKey).getName(), subTasks.get(subTaskKey).getDescription()
                    , subTasks.get(subTaskKey).getStartDate(), subTasks.get(subTaskKey).getDuration());
            id++;
        }
        return list;
    }

    @Override
    public void removeAllTasks() {
        for (Integer delete : tasks.keySet()) {
            listForDelete.add(tasks.get(delete));
            prioritizedTasks.remove(tasks.get(delete));
        }
        history.remove(listForDelete);
        listForDelete.clear();
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer delete : epics.keySet()) {
            listForDelete.add(epics.get(delete));
        }
        for (Integer delHistory : subTasks.keySet()) {
            listForDelete.add(subTasks.get(delHistory));
        }
        epics.clear();
        subTasks.clear();
        history.remove(listForDelete);
        listForDelete.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Integer delete : subTasks.keySet()) {
            listForDelete.add(subTasks.get(delete));
            prioritizedTasks.remove(subTasks.get(delete));
        }
        subTasks.clear();
        for (Integer idEpic : epics.keySet()) {
            epics.get(idEpic).removeAllSubTasks();
        }
        history.remove(listForDelete);
        listForDelete.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null) {
            history.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            history.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasks.get(id) != null) {
            history.add(subTasks.get(id));
        }
        return subTasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        if (idIsFree(task.getId())) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
        checkOnCrossDates();
    }

    @Override
    public void addEpic(Epic epic) {
        if (idIsFree(epic.getId())) {
            epics.put(epic.getId(), epic);
            epic.removeAllSubTasks();
        }
        checkOnCrossDates();
    }

    @Override
    public void addSubTaskToEpic(Epic epic, SubTask subTask) {
        if (epics.get(epic.getId()) != null && subTasks.get(subTask.getId()) != null) {
            epic.addSubTask(subTask);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (idIsFree(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            prioritizedTasks.add(subTask);
        }
        if (subTask.getEpic() != null && epics.get(subTask.getEpic().getId()) != null
                && subTask.getEpic().isSubTaskExist(subTask)) {
            subTask.getEpic().updateStatus();
        }
        checkOnCrossDates();
    }

    @Override
    public void updateTask(Task task) {
        if (getTaskById(task.getId()) != null) {
            tasks.put(task.getId(), task);
        }
        checkOnCrossDates();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.get(epic.getId()) != null) {
            Epic oldEpic = getEpicById(epic.getId());
            for (SubTask sub : getSubTasksOfEpic(oldEpic)) {
                sub.setEpic(null);
            }
            epics.put(epic.getId(), epic);
            for (SubTask sub : getSubTasksOfEpic(epic)) {
                sub.setEpic(epic);
            }
            epic.updateStatus();
        }
        checkOnCrossDates();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.get(subTask.getId()) != null) {
            if (getSubTaskById(subTask.getId()).getEpic() != null) {
                Epic oldEpic = getSubTaskById(subTask.getId()).getEpic();
                oldEpic.removeSubTask(getSubTaskById(subTask.getId()));
            }
            subTasks.put(subTask.getId(), subTask);
            if (subTask.getEpic() != null && subTask.getEpic().isSubTaskExist(subTask)) {
                subTask.getEpic().updateStatus();
            }
        }
        checkOnCrossDates();
    }

    @Override
    public void removeTask(int id) {
        if (tasks.get(id) != null) {
            listForDelete.add(tasks.get(id));
            history.remove(listForDelete);
            listForDelete.clear();
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.get(id) != null) {
            Epic epic = getEpicById(id);
            listForDelete.add(epic);

            for (SubTask sub : getSubTasksOfEpic(epic)) {
                listForDelete.add(sub);
            }

            epic.removeAllSubTasks();
            prioritizedTasks.remove(epic);
            epics.remove(id);
            history.remove(listForDelete);
            listForDelete.clear();
        }
    }

    @Override
    public void removeSubTask(int id) {
        if (subTasks.get(id) != null) {
            if (subTasks.get(id).getEpic() != null) {
                subTasks.get(id).getEpic().removeSubTask(subTasks.get(id));
            }
            listForDelete.add(subTasks.get(id));
            history.remove(listForDelete);
            listForDelete.clear();
            prioritizedTasks.remove(subTasks.get(id));
            subTasks.remove(id);

        }
    }

    @Override
    public boolean idIsFree(int id) {
        return (tasks.get(id) == null && epics.get(id) == null && subTasks.get(id) == null);
    }

    @Override
    public List<SubTask> getSubTasksOfEpic(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public void load() throws IOException, InterruptedException {}

    @Override
    public void loadFromFile() {}

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public String checkOnCrossDates() {
        String result = "";
        List<Task> sortList = new ArrayList<>(prioritizedTasks);

        for (int i = 0; i < sortList.size(); i++) {
            if (i > 0) {
                if (sortList.get(i).getStartDate().isBefore(sortList.get(i - 1).getEndDate())) {
                    result += "Имеются пересечения задач по времени: " + sortList.get(i - 1).getName() + ",End = "
                            + sortList.get(i - 1).getEndDate() + " пересекается с " + sortList.get(i).getName()
                            + ",Start = " + sortList.get(i).getStartDate();
                }
            }
        }
        if (result.equals("")) {
            result = "Пересечений по времени не найдено";
        }
        return result;
    }

}