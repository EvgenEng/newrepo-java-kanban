package tests;

import manager.InMemoryTaskManager;
import manager.Managers;

class InMemoryTaskManagerTests extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return (InMemoryTaskManager) Managers.getDefault(); // Использую метод Managers.getDefault()
            };
        };