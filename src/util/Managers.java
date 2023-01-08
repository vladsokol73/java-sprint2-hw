package util;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import manager.HttpTaskManager;
import manager.TaskManager;

import java.io.IOException;

public class Managers {
    private static final String URL = "http://localhost:8081";

    public static TaskManager getDefault() {
        TaskManager tm = null;
        try {
            tm = new HttpTaskManager(URL);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tm;
    }



    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
