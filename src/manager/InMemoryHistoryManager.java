package manager;

import interfaces.HistoryManager;
import task.Task;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager { //сделал класс не абстрактным
    private final Deque<Task> history;
    private static final int MAX_HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        this.history = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        history.remove(task);
        history.addFirst(task);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeLast();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    } //удалил лишний отступ. P.S. почему-то у меня некорректно работает ctrl+alt+L. IDEA не правит под стиль корректно
}