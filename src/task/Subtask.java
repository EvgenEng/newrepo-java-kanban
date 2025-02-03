package task;

public class Subtask extends Task {
    private Epic epic;

    // Конструктор с параметром статус и ID
    public Subtask(String title, String description, Epic epic, TaskStatus status, int id) {
        super(title, description, status, id);
        this.epic = epic;
        epic.addSubtask(this); // Добавляем подзадачу в эпик
    }

    public Subtask(String name, boolean done, String description, Epic epic) {
    }

    public Epic getEpic() {

        return epic;
    }

    // Возвращение ID связанного эпика
    public Integer getEpicId() {

        return epic.getId();
    }
}