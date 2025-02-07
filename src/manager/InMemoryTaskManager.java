package manager;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1; // Счетчик для уникальных идентификаторов задач
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
    private final HistoryManager historyManager; // Менеджер истории

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory(); // Инициализация менеджера истории
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    public void clearTasks() {
        tasks.clear(); // Очищаем мапу задач
    }

    // Метод для удаления всех подзадач (устранены замечания)
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            List<Subtask> subtasks = epic.getSubtasks();
            for (Subtask subtask: subtasks) {
                epic.removeSubtask(subtask.getEpicId());
            }
            updateEpicStatus(epic.getId()); // Исп метод updateEpicStatus
        }
        subtasks.clear(); // Очищаем мапу подзадач
    }

    // Метод для удаления всех эпиков и связанных с ними подзадач (устранены замечания)
    public void clearEpics() {
        subtasks.clear(); //удаляем подзадачи
        epics.clear(); // Очищаем мапу
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        int epicId = subtask.getEpicId();
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epicId);
    }

    @Override
    public void updateEpic(Epic epic) {

        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteTaskById(int id) {

        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubtasks();
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory()); // Возвращаем историю из менеджера истории
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task); // Обновление истории просмотров через HistoryManager
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask); // Обновление истории просмотров через HistoryManager
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic); // Обновление истории просмотров через HistoryManager
        }
        return epic;
    }

    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    private void updateEpicStatus(int epicId) { //обновление статуса эпика
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Subtask> epicSubtasks = getSubtasksForEpic(epicId);
            if (epicSubtasks.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                boolean allDone = true;
                boolean allNew = true;
                for (Subtask subtask : epicSubtasks) {
                    if (subtask.getStatus() != TaskStatus.DONE) {
                        allDone = false;
                    }
                    if (subtask.getStatus() != TaskStatus.NEW) {
                        allNew = false;
                    }
                }
                if (allDone) {
                    epic.setStatus(TaskStatus.DONE);
                } else if (allNew) {
                    epic.setStatus(TaskStatus.NEW);
                } else {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                }
            }
        }
    }

    protected void addTask(Task task) throws ManagerSaveException {
        task.setId(idCounter++); // Устанавливаем уникальный идентификатор
        tasks.put(task.getId(), task); // Добавляем задачу в Map
        historyManager.add(task); // Добавляем задачу в историю
    }

    protected void removeTask(int id) throws ManagerSaveException {
        if (tasks.containsKey(id)) {
            tasks.remove(id); // Удаляем задачу из Map
            historyManager.remove(id); // Удаляем задачу из истории
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id); // Удаляем подзадачу
            historyManager.remove(id);
        } else if (epics.containsKey(id)) {
            epics.remove(id); // Удаляем эпик
            historyManager.remove(id);
        } else {
            throw new IllegalArgumentException("Задача с id " + id + " не найдена.");
        }
    }

    public String taskToString(Task task) {
        return String.format("Task{id=%d, name='%s', status='%s'}",
                task.getId(), task.getName(), task.getStatus());
    }

    protected void addEpic(Epic epic) throws ManagerSaveException {
        if (epic == null) throw new ManagerSaveException("Epic is null");
        epic.setId(++idCounter);
        epics.put(epic.getId(), epic);
    }

    protected void addSubtask(Subtask subtask) throws ManagerSaveException {
        if (subtask == null) throw new ManagerSaveException("Subtask is null");
        subtask.setId(++idCounter);
        subtasks.put(subtask.getId(), subtask);
    }

    protected void removeEpic(int id) throws ManagerSaveException {
        if (!epics.containsKey(id)) throw new ManagerSaveException("Epic not found");
        epics.remove(id);
    }

    protected void removeSubtask(int id) throws ManagerSaveException {
        if (!subtasks.containsKey(id)) throw new ManagerSaveException("Subtask not found");
        subtasks.remove(id);
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }
}