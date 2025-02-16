package manager;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import task.Subtask;
import task.Task;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager() {

            @Override
            public void createTask(Task task) throws ManagerSaveException {

            }

            @Override
            public void createSubtask(Subtask subtask) throws ManagerSaveException {

            }

            @Override
            public void removeSubtask(int id) throws ManagerSaveException {

            }
        };
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}