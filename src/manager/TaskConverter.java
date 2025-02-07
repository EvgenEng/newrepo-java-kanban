package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import static manager.FileBackedTaskManager.TaskType;

public class TaskConverter {
    // Преобразование задачи в строку для сохранения в файл
    public static String taskToString(Task task) {
        if (task instanceof Subtask subtask) {
            return String.format("%d,%s,%s,%s,%s,%d",
                    subtask.getId(),
                    TaskType.SUBTASK.name(),
                    subtask.getTitle(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(),
                    TaskType.EPIC.name(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getDescription());
        } else {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(),
                    TaskType.TASK.name(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getDescription());
        }
    }
}