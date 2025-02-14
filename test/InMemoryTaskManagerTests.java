package tests;

import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import manager.Managers; // Импортируем фабрику Managers
import interfaces.TaskManager; // Импортируем интерфейс TaskManager
import task.Epic;
import task.Subtask;
import task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTests {
    private TaskManager taskManager; // Используем интерфейс TaskManager

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault(); // Используем фабрику для создания экземпляра
    }

    @Test
    void testAddNewTask() {
        Task task = new Task("Test Task", "Test description");
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());
        assertEquals("Description 1", savedTask.getDescription(), "Описание задачи должно оставаться неизменным.");
    }

    static class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

        @Override
        protected InMemoryTaskManager createTaskManager() {
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
            }; // Возвращаем экземпляр InMemoryTaskManager
        }
    }
}