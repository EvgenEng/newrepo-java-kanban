package tests;

import interfaces.TaskManager;
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
    void shouldAddAndRetrieveTask() throws ManagerSaveException {
        Task task = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача должна быть сохранена.");
        assertEquals(task, savedTask, "Сохранённая задача должна совпадать с исходной.");
    }

    @Test
    void shouldAddAndRetrieveEpic() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);

        final Epic savedEpic = (Epic) taskManager.getTaskById(epic.getId());
    }

    @Test
    void shouldAddAndRetrieveSubtask() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        taskManager.createSubtask(subtask);

        final Subtask savedSubtask = (Subtask) taskManager.getTaskById(subtask.getId());
    }

    @Test
    void shouldCalculateEpicStatusCorrectly() throws ManagerSaveException {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.DONE, 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS при смешанных статусах подзадач.");
    }

    @Test
    void shouldPreventTaskTimeOverlap() throws ManagerSaveException {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 60);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2, LocalDateTime.of(2023, 1, 1, 10, 30), 60);

        taskManager.createTask(task1);
    }

    @Test
    void shouldPrioritizeTasksByStartTime() throws ManagerSaveException {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 60);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2, LocalDateTime.of(2023, 1, 1, 9, 0), 60);
        Task task3 = new Task("Task 3", "Description", TaskStatus.NEW, 3, LocalDateTime.of(2023, 1, 1, 11, 0), 60);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(task2, prioritizedTasks.get(0), "Первая задача должна быть с самым ранним временем начала.");
        assertEquals(task1, prioritizedTasks.get(1), "Вторая задача должна быть следующей по времени начала.");
        assertEquals(task3, prioritizedTasks.get(2), "Третья задача должна быть с самым поздним временем начала.");
    }

    @Test
    void shouldReturnEmptyTaskListWhenNoTasksAdded() {
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Список задач должен быть пустым, если задачи не добавлены.");
    }

    @Test
    void shouldClearAllTasks() throws ManagerSaveException {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.clearTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены.");
    }
}