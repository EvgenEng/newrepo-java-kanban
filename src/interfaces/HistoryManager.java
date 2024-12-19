package interfaces;

import task.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task); // Добавляет задачу в историю
    List<Task> getHistory(); // Получает список задач из истории
}