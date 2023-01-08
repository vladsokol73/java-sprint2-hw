package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTask extends Task {
    private transient Epic epic;
    private int idEpic = 0;

    public SubTask(int id, Status status, String name, String description, LocalDateTime dateTime, Duration duration) {

        super(id, status, name, description,dateTime,duration);
    }

    public Epic getEpic() {
        return epic;
    }

    public int getIdEpic(){
        return idEpic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
        if(epic != null){
            idEpic = epic.getId();
        } else {
            idEpic = 0;
        }

    }

    @Override
    public void setStatus(Status status){
        this.status = status;
        if(epic != null){
            epic.updateStatus();
        }
    }

    @Override
    public String toString(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String strEpicId;
        if(epic != null){
            strEpicId = String.valueOf(epic.getId());
        } else {
            strEpicId = "";
        }
        return id + "," + TypeTask.SUBTASK + "," + name + "," + status + "," + description + ","
                + getStartDate().format(df) + "," + getDuration() + "," + strEpicId;
    }
}
