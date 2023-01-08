package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected int id;
    protected Status status;
    protected String name;
    protected String description;
    protected LocalDateTime startDate;
    protected Duration duration;


    public Task(int id, Status status, String name, String description, LocalDateTime date, Duration duration) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
        this.startDate = date;
        this.duration = duration;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndDate() {
        return startDate.plus(duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return id + "," + TypeTask.TASK + "," + name + "," + status + "," + description + ","
                + getStartDate().format(df) + "," + getDuration();
    }

}
