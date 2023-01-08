package history;

import java.util.List;

import tasks.Task;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(List<Task> listForDelete);

}
