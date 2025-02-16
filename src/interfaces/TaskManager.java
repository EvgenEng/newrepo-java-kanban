package interfaces;

import manager.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {

    // Получение задач в порядке приоритета
    List<Task> getPrioritizedTasks();

    // Создание задач
    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    // Обновление задач
    void updateTask(Task task) throws ManagerSaveException;

    void updateSubtask(Subtask subtask) throws ManagerSaveException;

    void updateEpic(Epic epic);

    // Удаление задач по ID
    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    // Получение задач по ID
    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id); //добавлен метод

    // Получение всех задач
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();
    
    List<Epic> getAllEpics();

    // Получение подзадач для эпика
    List<Subtask> getSubtasksForEpic(int epicId);

    // Получение истории задач
    List<Task> getHistory();

    // Методы для очистки
    void clearTasks(); // Очистка всех задач

    void clearSubtasks(); // Очистка всех подзадач

    void clearEpics(); // Очистка всех эпиков и связанных подзадач
}