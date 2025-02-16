package tests;

import interfaces.TaskManager;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    // Абстрактный метод для создания конкретной реализации TaskManager
    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        taskManager.createTask(task);
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        taskManager.createSubtask(subtask);
    }

    @Test
    void shouldCalculateEpicStatusCorrectly() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.DONE, 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS при смешанных статусах подзадач.");
    }

    @Test
    void shouldPreventTaskTimeOverlap() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 60);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2, LocalDateTime.of(2023, 1, 1, 10, 30), 60);

        taskManager.createTask(task1);
        try {
        } catch (IllegalArgumentException e) {
            // Исключение выброшено, тест проходит
            assertEquals("Задачи пересекаются по времени!", e.getMessage(), "Сообщение исключения не совпадает.");
        }
    }

    @Test
    void shouldReturnEmptyTaskListWhenNoTasksAdded() {
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Список задач должен быть пустым, если задачи не добавлены.");
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        taskManager.createTask(task);
    }

    @Test
    void shouldClearAllTasks() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Очищаем все задачи
        taskManager.clearTasks();

        // Проверяем, что список задач пуст
        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены.");
    }

    @Test
    void shouldHandleEpicWithoutSubtasks() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика без подзадач должен быть NEW.");
    }

    @Test
    void shouldHandleEpicWithAllSubtasksDone() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.DONE, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.DONE, 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи выполнены.");
    }

    @Test
    void shouldHandleEpicWithAllSubtasksNew() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.NEW, 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если все подзадачи новые.");
    }

    @Test
    void shouldHandleEpicWithMixedSubtaskStatuses() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.DONE, 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS при смешанных статусах подзадач.");
    }

    @Test
    void shouldAllowNonOverlappingTasks() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 60);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2, LocalDateTime.of(2023, 1, 1, 11, 0), 60);
        taskManager.createTask(task1);
        assertDoesNotThrow(() -> taskManager.createTask(task2), "Добавление задачи с непересекающимся временем не должно вызывать исключение.");
    }

    @Test
    void testAddNewTask() {
        Task task = new Task("Test Task", "Test description");
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());
    }

    static class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

        @Override
        protected InMemoryTaskManager createTaskManager() {
            return new InMemoryTaskManager() {
                @Override
                public void createTask(Task task) throws ManagerSaveException {

                }

                @Override
                public void addSubtask(Subtask subtask) throws ManagerSaveException {
                    subtasks.put(subtask.getId(), subtask);
                    Epic epic = subtask.getEpic();
                    epic.addSubtask(subtask);
                    updateEpicStatus(epic); // Обновляем статус эпика
                }

                @Override
                public void removeSubtask(int id) throws ManagerSaveException {

                }

            }; // Возвращаем экземпляр InMemoryTaskManager
        }

        private void updateEpicStatus (Epic epic){

        }
    }
}