package manager;

import task.Epic;
import task.Subtask;
import task.Task;

public class TaskConverter {

    // Преобразование задачи в строку для сохранения в файл
    public static String taskToString(Task task) {
        if (task instanceof Subtask subtask) {
            int epicId = subtask.getEpicId(); // Получаем ID эпика
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    subtask.getId(),
                    subtask.getTitle(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    epicId);
        } else if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s",
                    task.getId(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s",
                    task.getId(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getDescription());
        }
    }
}