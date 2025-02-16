package tests;

import manager.InMemoryTaskManager;

class InMemoryTaskManagerTests extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager() {
            };
        };
    }