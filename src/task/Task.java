package task;

public class Task {

    private int id; // уник идентификатор задачи
    private String title; // название задачи
    private String descriptions; // описание задачи
    private TaskStatus status; // статус задачи

    // Конструктор для создания задачи с заданным статусом
    public Task(String title, String descriptions, TaskStatus status, int id) {

        this.id = id;
        this.title = title;
        this.descriptions = descriptions;
        this.status = status; // установка состояния
    }

    // Конструктор по умолчанию, устанавливающий статус NEW
    public Task(String title, String descriptions) {

        this.id = 0; // Идентификатор по умолчанию
        this.title = title;
        this.descriptions = descriptions;
        this.status = TaskStatus.NEW; // Статус по умолчанию
    }

    public Task() {
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    // Установка статуса задачи
    public void setStatus(TaskStatus taskStatus) {
        this.status = taskStatus; // Устанавливаем новый статус
    }

    // Установка идентификатора задачи
    public void setId(int id) {
        this.id = id; // Устанавливаем новый идентификатор
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public String getDescription() {
        return descriptions; // Возвращаем описание задачи
    }

    public Object getName() {
        return null;
    }

    public Object getEpicId() {
        return null;
    }
}