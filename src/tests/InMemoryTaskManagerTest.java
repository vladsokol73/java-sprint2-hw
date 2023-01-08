package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    public void createObj() {
        taskManager = new InMemoryTaskManager();
        super.createObj();

    }

}
