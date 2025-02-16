package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final List<Subtask> subtasks; // Список подзадач
    private LocalDateTime endTime;

    public Epic(String title, String descriptions, int id) {
        super(title, descriptions, TaskStatus.NEW, id); // Устанавливаем статус NEW и передаем id
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>(); // Инициализация списка
    }

    public void addSubtask(Subtask subtask) {
        if (subtask != null && !subtasks.contains(subtask)) { // Проверка на null и дубликаты
            subtasks.add(subtask);
            updateTiming(); // Автоматическое обновление времени!
        }
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks); // Возвращаем копию списка подзадач
    }

    // Удаление подзадачи по ID
    public void removeSubtask(int subtaskId) {
        subtasks.removeIf(subtask -> subtask.getId() == subtaskId);
        updateTiming();
    }

    // Очистка списка подзадач
    public void clearSubtasks() {
        subtasks.clear();
        updateTiming();
    }

    // Основная логика обновления времени эпика
    public void updateTiming() {
        if (subtasks.isEmpty()) {
            setStartTime(null);
            setDuration(null);
            endTime = null;
            return;
        }

        // Расчет времени начала (самая ранняя подзадача)
        LocalDateTime earliestStart = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        // Расчет времени окончания (самая поздняя подзадача)
        LocalDateTime latestEnd = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // Суммарная продолжительность
        Duration totalDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        setStartTime(earliestStart);
        setDuration(totalDuration);
        endTime = latestEnd;
    }

    // Переопределение метода getEndTime()
    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // new toString() для отладки
    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + (getStartTime() != null
                ? getStartTime().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "null") +
                ", duration=" + (getDuration() != null
                ? getDuration().toMinutes() + "m"
                : "null") +
                ", endTime=" + (endTime != null
                ? endTime.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "null") +
                ", subtasksCount=" + subtasks.size() +
                '}';
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }


}