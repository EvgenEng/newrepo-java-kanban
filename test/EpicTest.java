package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic("Epic Title", "Epic Description", 1);
    }

    @Test
    void addSubtask() {
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 1);
        epic.addSubtask(subtask);
        List<Subtask> subtasks = epic.getSubtasks();
        assertEquals(1, subtasks.size(), "Должна быть одна подзадача.");
        assertEquals(subtask, subtasks.get(0), "Добавленная подзадача должна совпадать с ожидаемой.");
    }

    @Test
    void getSubtasks() {
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.NEW, 2);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        List<Subtask> subtasks = epic.getSubtasks();
        assertEquals(2, subtasks.size(), "Должно быть две подзадачи.");
        assertTrue(subtasks.contains(subtask1), "Список подзадач должен содержать Subtask 1.");
        assertTrue(subtasks.contains(subtask2), "Список подзадач должен содержать Subtask 2.");
    }

    @Test
    void removeSubtask() {
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 1);
        epic.addSubtask(subtask);
        epic.removeSubtask(subtask.getId()); // Передаем ID подзадачи
        List<Subtask> subtasks = epic.getSubtasks();
        assertEquals(0, subtasks.size(), "Список подзадач должен быть пустым после удаления.");
    }

    @Test
    void clearSubtasks() {
        epic.addSubtask(new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 1));
        epic.addSubtask(new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.NEW, 2));
        epic.clearSubtasks();
        List<Subtask> subtasks = epic.getSubtasks();
        assertEquals(0, subtasks.size(), "Список подзадач должен быть пустым после очистки.");
    }

    // Тесты для расчёта статуса Epic
    @Test
    void shouldReturnNewStatusWhenNoSubtasks() {
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Epic должен быть NEW, если нет подзадач.");
    }

    @Test
    void shouldReturnNewStatusWhenAllSubtasksAreNew() {
        epic.addSubtask(new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 1));
        epic.addSubtask(new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.NEW, 2));
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Epic должен быть NEW, если все подзадачи имеют статус NEW.");
    }

    @Test
    void shouldReturnDoneStatusWhenAllSubtasksAreDone() {
        epic.addSubtask(new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.DONE, 1));
        epic.addSubtask(new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.DONE, 2));
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус Epic должен быть DONE, если все подзадачи имеют статус DONE.");
    }

    @Test
    void shouldReturnInProgressStatusWhenSubtasksHaveMixedStatuses() {
        epic.addSubtask(new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.NEW, 1));
        epic.addSubtask(new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.DONE, 2));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Epic должен быть IN_PROGRESS, если подзадачи имеют смешанные статусы.");
    }

    @Test
    void shouldReturnInProgressStatusWhenAllSubtasksAreInProgress() {
        epic.addSubtask(new Subtask("Subtask 1", "Subtask Description", epic, TaskStatus.IN_PROGRESS, 1));
        epic.addSubtask(new Subtask("Subtask 2", "Subtask Description", epic, TaskStatus.IN_PROGRESS, 2));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Epic должен быть IN_PROGRESS, если все подзадачи имеют статус IN_PROGRESS.");
    }
}