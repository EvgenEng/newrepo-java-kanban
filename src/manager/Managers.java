package manager;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager() {
            @Override
            public void addTask(Task task) throws ManagerSaveException {

            }

            @Override
            public void addEpic(Epic epic) throws ManagerSaveException {

            }

            @Override
            public void addSubtask(Subtask subtask) throws ManagerSaveException {

            }

            @Override
            public void removeTaskById(int id) {

            }

            @Override
            public void clearAllTasks() {

            }

            @Override
            public void removeTask(int id) throws ManagerSaveException {

            }

            @Override
            public void removeEpic(int id) throws ManagerSaveException {

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