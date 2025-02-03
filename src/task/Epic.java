package task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtasks; // Список подзадач

    public Epic(String title, String descriptions, int id) {
        super(title, descriptions, TaskStatus.NEW, id); // Устанавливаем статус NEW и передаем id
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, boolean done, String description, List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public Epic(String name, boolean done, String description) {
        // Инициализация переменной subtasks
        this.subtasks = (List<Subtask>) new HashMap<>();
        // Остальные ваши параметры
    }

    public void addSubtask(Subtask subtask) {
        if (subtask != null && !subtasks.contains(subtask)) { // Проверка на null и дубликаты
            subtasks.add(subtask);
        }
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks); // Возвращаем копию списка подзадач
    }

    // Удаление подзадачи по ID
    public void removeSubtask(int subtaskId) {
        subtasks.removeIf(subtask -> subtask.getId() == subtaskId);
    }

    // Очистка списка подзадач
    public void clearSubtasks() {
        subtasks.clear();
    }
}