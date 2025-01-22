package interfaces;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    List<Subtask> getSubtasksForEpic(int epicId);

    List<Task> getHistory(); // Изменено на List<Task>

    // Методы для очистки
    void clearTasks(); // Метод для очистки всех задач

    void clearSubtasks(); // Метод для очистки всех подзадач

    void clearEpics(); // Метод для очистки всех эпиков и связанных с ними подзадач
}