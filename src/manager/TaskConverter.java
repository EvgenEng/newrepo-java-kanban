package manager;

import interfaces.TaskManager;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TaskConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String CSV_HEADER = "id,type,title,status,description,epic,start_time,duration";

    // Сериализация задачи в CSV строку
    public static String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();

        // Общие поля для всех типов задач
        sb.append(task.getId()).append(",")
                .append(getTaskType(task)).append(",")
                .append(escapeCommas(task.getTitle())).append(",")
                .append(task.getStatus()).append(",")
                .append(escapeCommas(task.getDescription())).append(",");

        // Специфичные поля для подзадач
        if (task instanceof Subtask subtask) {
            sb.append(subtask.getEpicId()).append(",");
        } else {
            sb.append(","); // Пустое поле для эпиков и обычных задач
        }

        // Обработка времени и продолжительности
        String startTime = task.getStartTime() != null ?
                task.getStartTime().format(DATE_TIME_FORMATTER) : "";
        String duration = task.getDuration() != null ?
                String.valueOf(task.getDuration().toMinutes()) : "";

        sb.append(startTime).append(",")
                .append(duration);

        return sb.toString();
    }

    // Десериализация строки CSV в задачу
    public static Task taskFromString(String line, TaskManager manager) {
        String[] fields = line.split(",", -1); // Важно: используем -1 для пустых полей
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]); // Используем TaskType
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        String epicIdField = fields[5];

        LocalDateTime startTime = parseDateTime(fields[6]);
        Duration duration = parseDuration(fields[7]);

        switch (type) {
            case TASK:
                Task task = new Task(title, description);
                task.setId(id);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;

            case EPIC:
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return epic;

            case SUBTASK:
                int epicId = Integer.parseInt(epicIdField);
                Epic parentEpic = manager.getEpicById(epicId);
                Subtask subtask = new Subtask(title, description, parentEpic);
                subtask.setId(id);
                subtask.setStatus(status);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                return subtask;

            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    // Вспомогательные методы
    private static String getTaskType(Task task) {
        return task.getType().name(); // Возвращаем имя типа задачи
    }

    private static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return null;
        try {
            return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException parseException) {
            return null;
        }
    }

    private static Duration parseDuration(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            return Duration.ofMinutes(Long.parseLong(str));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String escapeCommas(String text) {
        return text.replace(",", "\\,");
    }

    private static String unescapeCommas(String text) {
        return text.replace("\\,", ",");
    }

    public static String getCsvHeader() {
        return CSV_HEADER;
    }
}