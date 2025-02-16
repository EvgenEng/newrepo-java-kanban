package task;

public class Subtask extends Task {
    private Epic epic;

    // Исправленный конструктор
    public Subtask(String title, String description, Epic epic) {
        super(title, description);
        this.epic = epic;
        if (epic != null) {
            epic.addSubtask(this);
        }
    }

    // Конструктор с параметрами статус и ID
    public Subtask(String title, String description, Epic epic, TaskStatus status, int id) {
        super(title, description, status, id);
        this.epic = epic;
        if (epic != null) {
            epic.addSubtask(this);
        }
    }

    public Integer getEpicId() {
        return (epic != null) ? epic.getId() : -1;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}