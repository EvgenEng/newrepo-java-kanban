package task;

public class Subtask extends Task {
    private final Epic epic;

    // Конструктор с параметром статус и ID
    public Subtask(String title, String description, Epic epic, TaskStatus status, int id) {
        super(title, description, status, id);
        this.epic = epic;
        epic.addSubtask(this); // Добавляем подзадачу в эпик
    }

    public Epic getEpic() {

        return epic;
    }

    // Возвращение ID связанного эпика
    public int getEpicId() {

        return epic.getId();
    }
}