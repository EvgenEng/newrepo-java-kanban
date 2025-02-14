package tests;

import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import task.Epic;

class InMemoryTaskManagerTests extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager() {
            @Override
            public void addEpic(Epic epic) throws ManagerSaveException {

            }

            @Override
            public void removeEpic(int id) throws ManagerSaveException {

            }
        };
    }
}