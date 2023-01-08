package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    public Epic(int id, Status status, String name, String description) {
        super(id, status, name, description, null, null);
        subTasks = new ArrayList<>();
        updateStatus();
    }

    @Override
    public LocalDateTime getStartDate() {
        LocalDateTime minDt = LocalDateTime.of(2100, 1, 1, 0, 0);
        if (subTasks.size() > 0) {
            for (SubTask sub : subTasks) {
                if (sub.getStartDate().isBefore(minDt)) {
                    minDt = sub.getStartDate();
                }
            }
        }
        return minDt;
    }

    @Override
    public LocalDateTime getEndDate() {
        LocalDateTime maxDt = LocalDateTime.of(1970, 1, 1, 0, 0);
        for (SubTask sub : subTasks) {
            if (sub.getEndDate().isAfter(maxDt)) {
                maxDt = sub.getEndDate();
            }
        }
        return maxDt;
    }

    @Override
    public Duration getDuration() {
        Duration duration = Duration.ofMinutes(0);
        for (SubTask sub : subTasks) {
            duration = duration.plus(sub.getDuration());
        }
        return duration;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public void updateStatus() {
        if (subTasks.size() == 0) {
            status = Status.NEW;
            return;
        }
        int countNew = 0;
        int countDone = 0;
        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case NEW:
                    countNew++;
                    break;
                case DONE:
                    countDone++;
            }
        }
        if (countNew == subTasks.size()) {
            status = Status.NEW;
        } else if (countDone == subTasks.size()) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    public void removeSubTask(SubTask subTask) {
        if (isSubTaskExist(subTask)) {
            subTasks.remove(subTask);
            subTask.setEpic(null);
            updateStatus();
        }
    }

    public void removeAllSubTasks() {
        subTasks.clear();
        updateStatus();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        if (!isSubTaskExist(subTask)) {
            subTasks.add(subTask);
            subTask.setEpic(this);
            updateStatus();
        }
    }

    public boolean isSubTaskExist(SubTask subTask) {
        boolean result = false;
        for (SubTask sub : subTasks) {
            if (sub == subTask)
                result = true;
        }
        return result;
    }

    @Override
    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime dt = getStartDate();
        return id + "," + TypeTask.EPIC + "," + name + "," + status + "," + description + ","
                + dt.format(df) + "," + getDuration();
    }

}
