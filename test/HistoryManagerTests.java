package tests;

import interfaces.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTests {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory(); // Инициализация менеджера истории перед каждым тестом
    }

    // Тест на добавление задачи в историю
    @Test
    void testAddToHistory() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task); // Добавляем задачу в историю

        List<Task> history = historyManager.getHistory(); // Получаем историю задач

        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной.");
    }

    // Тест на удаление задачи из истории
    @Test
    void testRemoveFromHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId()); // Удаляем первую задачу

        List<Task> history = historyManager.getHistory(); // Получаем историю задач
    }

    // Тест на добавление дубликатов
    @Test
    void testAddDuplicateTask() {
        Task task = new Task("Task 1", "Description 1");

        historyManager.add(task);
        historyManager.add(task); // Добавляем ту же задачу второй раз

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не должна содержать дубликатов.");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной.");
    }

    // Тест на пустую историю
    @Test
    void shouldHandleEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой при инициализации.");
    }

    // Тест на удаление задачи из истории
    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, 2);
        Task task3 = new Task("Task 3", "Description", TaskStatus.NEW, 3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId()); // Удаляем первую задачу
        assertFalse(historyManager.getHistory().contains(task1), "Задача должна быть удалена из истории.");
    }

    // Тест на добавление задачи с дубликатами
    @Test
    void shouldHandleDuplicateTasks() {
        Task task = new Task("Task 1", "Description", TaskStatus.NEW, 1);
        historyManager.add(task);
        historyManager.add(task); // Добавляем ту же задачу второй раз
        assertEquals(1, historyManager.getHistory().size(), "История не должна содержать дубликаты.");
    }
}