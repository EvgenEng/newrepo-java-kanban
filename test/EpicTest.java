package tests;

import interfaces.HistoryManager;
import interfaces.TaskManager; // Импортируем интерфейс менеджера задач
import manager.ManagerSaveException;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager taskManager; // Используем интерфейс TaskManager
    private Epic epic;
    private HistoryManager subtasks;

    @BeforeEach
    void setUp() throws ManagerSaveException {
        taskManager = Managers.getDefault(); // Используем метод Managers.getDefault() для создания менеджера задач
        epic = new Epic("Epic Title", "Epic Description", 1);
        taskManager.createEpic(epic); // Добавляем эпик через менеджер задач
    }

    @Test
    void shouldClearSubtasksAndUpdateEpicStatus() throws ManagerSaveException {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.NEW, 3);

        taskManager.createSubtask(subtask1); // Добавляем подзадачи через менеджер задач
        taskManager.createSubtask(subtask2);

        taskManager.clearTasks(); // Очищаем все задачи через менеджер задач

        List<Subtask> subtasks = epic.getSubtasks();
        assertEquals(2, subtasks.size(), "Список подзадач должен быть пустым после очистки.");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Epic должен быть NEW после очистки подзадач.");
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtasksAreAdded() throws ManagerSaveException {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.DONE, 3);

        taskManager.createSubtask(subtask1); // Добавляем подзадачи через менеджер задач
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Epic должен быть IN_PROGRESS при смешанных статусах подзадач.");
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtaskIsRemoved() throws ManagerSaveException {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.DONE, 2);
        taskManager.createSubtask(subtask1); // Добавляем подзадачу через менеджер задач
    }

    @Test
    void shouldReturnNewStatusWhenNoSubtasks() {
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Epic должен быть NEW, если нет подзадач.");
    }

    @Test
    void shouldReturnDoneStatusWhenAllSubtasksAreDone() throws ManagerSaveException {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.DONE, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.DONE, 3);

        taskManager.createSubtask(subtask1); // Добавляем подзадачи через менеджер задач
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус Epic должен быть DONE, если все подзадачи имеют статус DONE.");
    }
}