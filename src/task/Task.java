package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    // Конструктор для создания с ID и статусом
    public Task(String title, String description, TaskStatus status, int id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // Конструктор по умолчанию
    public Task(String title, String description) {
        this(title, description, TaskStatus.NEW, 0);
    }

    // Конструктор с временем начала и продолжительностью
    public Task(String title, String description, TaskStatus status, int id, LocalDateTime startTime, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationMinutes); // Преобразуем минуты в Duration
    }

    public Task(String title , String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.title  = title ;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // Метод для получения времени окончания задачи
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration); // Используем Duration для вычисления времени окончания
    }

    // Переопределение equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + (duration != null ? duration.toMinutes() + "m" : "null") +
                ", startTime=" + (startTime != null ? startTime.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "null") +
                '}';
    }

    public TaskType getType() {
        return TaskType.TASK;
    }
}