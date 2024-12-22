package interfaces;

import task.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task); // Добавляет задачу в историю
    void remove(int id); // Удаляет задачу из истории по ID
    List<Task> getHistory(); // Получает список задач из истории
}