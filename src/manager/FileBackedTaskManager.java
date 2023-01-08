package manager;

import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public FileBackedTaskManager() {
        super();
    }

    @Override
    public void loadFromFile() {
        ArrayList<String> stringList = new ArrayList<>();
        try {
            Reader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                stringList.add(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            throw new ManagerSaveException("файл не найден");
        }

        if (stringList.size() < 2) {
            return;
        }

        for (int i = 1; i < stringList.size(); i++) {
            if (stringList.get(i).equals("")) {
                break;
            }
            String[] line = stringList.get(i).split(",");
            int id = Integer.parseInt(line[0]);
            Status status = Status.valueOf(line[3]);
            String name = line[2];
            String desc = line[4];
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime start = LocalDateTime.parse(line[5], df);
            Duration duration = Duration.parse(line[6]);
            int idEpic = 0;
            if (line.length == 8 && (!line[7].equals(""))) {
                idEpic = Integer.parseInt(line[7]);
            }
            switch (TypeTask.valueOf(line[1])) {
                case TASK:
                    Task task = new Task(id, status, name, desc, start, duration);
                    tasks.put(task.getId(), task);
                    break;
                case EPIC:
                    Epic epic = new Epic(id, status, name, desc);
                    epics.put(epic.getId(), epic);
                    break;
                case SUBTASK:
                    SubTask subTask = new SubTask(id, status, name, desc, start, duration);
                    if (idEpic > 0 && epics.get(idEpic) != null) {
                        subTask.setEpic(epics.get(idEpic));
                    }
                    subTasks.put(subTask.getId(), subTask);
            }
        }

        for (Integer idSub : subTasks.keySet()) {
            SubTask sub = subTasks.get(idSub);
            if (sub.getEpic() != null) {
                sub.getEpic().addSubTask(sub);
            }
        }

        if (stringList.get(stringList.size() - 2).equals("")) {
            Task[] tasks = getListTasks();
            Epic[] epics = getListEpics();
            SubTask[] subTasks = getListSubTasks();
            String[] line = stringList.get(stringList.size() - 1).split(",");
            for (int i = 0; i < line.length; i++) {
                if (!line[i].equals("")) {
                    int id = Integer.parseInt(line[i]);
                    for (Task task : tasks) {
                        if (task.getId() == id) {
                            history.add(task);
                        }
                    }

                    for (Epic epic : epics) {
                        if (epic.getId() == id) {
                            history.add(epic);
                        }
                    }

                    for (SubTask subTask : subTasks) {
                        if (subTask.getId() == id) {
                            history.add(subTask);
                        }
                    }
                }
            }
        }

    }

    protected void save() {
        String dump = "id,type,name,status,description,startDate,duration,epic\n";

        if (tasks.size() > 0) {
            for (Integer i : tasks.keySet()) {
                dump += tasks.get(i).toString() + "\n";
            }
        }

        if (epics.size() > 0) {
            for (Integer i : epics.keySet()) {
                dump += epics.get(i).toString() + "\n";
            }
        }

        if (subTasks.size() > 0) {
            for (Integer i : subTasks.keySet()) {
                dump += subTasks.get(i).toString() + "\n";
            }
        }

        dump += "\n";

        if (getHistory().size() > 0) {
            for (Task task : getHistory()) {
                dump += task.getId() + ",";
            }
        }

        try {
            Writer fileWriter = new FileWriter(file);
            fileWriter.write(dump);
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("файл не найден");
        }

    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addSubTaskToEpic(Epic epic, SubTask subTask) {
        super.addSubTaskToEpic(epic, subTask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null) {
            history.add(tasks.get(id));
        }
        save();
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            history.add(epics.get(id));
        }
        save();
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasks.get(id) != null) {
            history.add(subTasks.get(id));
        }
        save();
        return subTasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

}
