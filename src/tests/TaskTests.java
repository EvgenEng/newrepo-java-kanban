package tests;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTests {

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 1", "Description 1");
        task1.setId(1); // Устанавливаем id для сравнения
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны по id.");
    }

    @Test
    void testSubtaskEqualityById() {
        Epic epic = new Epic("Epic 1", "Epic description", 1); // Создаем эпик
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic, TaskStatus.NEW, 2); // Передаем эпик и статус
        Subtask subtask2 = new Subtask("Subtask 1", "Description 1", epic, TaskStatus.NEW, 3); // Передаем эпик и статус
        subtask1.setId(2);
        subtask2.setId(2);

        assertEquals(subtask1, subtask2, "Подзадачи должны быть равны по id.");
    }

    @Test
    void testTaskHashCode() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 1", "Description 1");
        task1.setId(1); // Устанавливаем id для сравнения
        task2.setId(1);

        assertEquals(task1.hashCode(), task2.hashCode(), "Hash codes should be equal for equal tasks");
    }
}