package interfaces;

import task.Subtask;
import task.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);// Добавляет задачу в историю

    Subtask remove(int id); // Удаляет задачу из истории по ID

    List<Task> getHistory(); // Получает список задач из истории
}