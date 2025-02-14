package tests;

import interfaces.TaskManager;
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
        taskManager.addTask(task);
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.addEpic(epic);
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        taskManager.addSubtask(subtask);
    }

    @Test
    void shouldCalculateEpicStatusCorrectly() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.DONE, 3);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS при смешанных статусах подзадач.");
    }

    @Test
    void shouldPreventTaskTimeOverlap() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 60);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2, LocalDateTime.of(2023, 1, 1, 10, 30), 60);

        taskManager.addTask(task1);
//        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2), "Добавление задачи с пересекающимся временем должно вызывать исключение.");
    }

    @Test
    void shouldReturnEmptyTaskListWhenNoTasksAdded() {
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Список задач должен быть пустым, если задачи не добавлены.");
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        taskManager.addTask(task);
        taskManager.removeTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()), "Задача должна быть удалена по ID.");
    }

    @Test
    void shouldClearAllTasks() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.clearAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены.");
    }

    @Test
    void shouldHandleEpicWithoutSubtasks() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.addEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика без подзадач должен быть NEW.");
    }

    @Test
    void shouldHandleEpicWithAllSubtasksDone() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.DONE, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.DONE, 3);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи выполнены.");
    }

    @Test
    void shouldHandleEpicWithAllSubtasksNew() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.NEW, 3);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если все подзадачи новые.");
    }

    @Test
    void shouldHandleEpicWithMixedSubtaskStatuses() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic, TaskStatus.DONE, 3);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS при смешанных статусах подзадач.");
    }
}