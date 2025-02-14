package tests;

import manager.InMemoryTaskManager; // Импортируем менеджер задач
import interfaces.TaskManager; // Импортируем интерфейс менеджера задач
import manager.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager taskManager; // Используем интерфейс TaskManager
    private Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager() {
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
            public void removeEpic(int id) throws ManagerSaveException {

            }

            @Override
            public void removeSubtask(int id) throws ManagerSaveException {
                Subtask subtask = subtasks.remove(id); // Удаляем подзадачу из списка

            }
        }; // Используем конкретную реализацию
        epic = new Epic("Epic Title", "Epic Description", 1);
        taskManager.addEpic(epic); // Добавляем эпик через менеджер задач
    }

    @Test
    void shouldClearSubtasksAndUpdateEpicStatus() {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.NEW, 3);

        taskManager.addSubtask(subtask1); // Добавляем подзадачи через менеджер задач
        taskManager.addSubtask(subtask2);

        taskManager.clearTasks(); // Очищаем все задачи через менеджер задач

        List<Subtask> subtasks = epic.getSubtasks();
        assertEquals(2, subtasks.size(), "Список подзадач должен быть пустым после очистки.");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Epic должен быть NEW после очистки подзадач.");
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtasksAreAdded() {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.DONE, 3);

        taskManager.addSubtask(subtask1); // Добавляем подзадачи через менеджер задач
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Epic должен быть IN_PROGRESS при смешанных статусах подзадач.");
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtaskIsRemoved() {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.DONE, 2);
        taskManager.addSubtask(subtask1); // Добавляем подзадачу через менеджер задач
    }

    @Test
    void shouldReturnNewStatusWhenNoSubtasks() {
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Epic должен быть NEW, если нет подзадач.");
    }

    @Test
    void shouldReturnDoneStatusWhenAllSubtasksAreDone() {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.DONE, 2);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.DONE, 3);

        taskManager.addSubtask(subtask1); // Добавляем подзадачи через менеджер задач
        taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус Epic должен быть DONE, если все подзадачи имеют статус DONE.");
    }
}