package tests;

import interfaces.HistoryManager;
import task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import manager.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTests {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory(); // Инициализация менеджера истории перед каждым тестом
    }

    @Test
    void testAddToHistory() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task); // Добавляем задачу в историю

        List<Task> history = historyManager.getHistory(); // Получаем историю задач

        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной.");
    }

    @Test
    void testRemoveFromHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory(); // Получаем историю задач

        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления.");
        assertEquals(task2, history.get(0), "Осталась только вторая задача.");
    }
}