package manager;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).thenComparingInt(Task::getId)
    );

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void validateAndAddToPrioritized(Task task) throws ManagerSaveException {
        if (task.getStartTime() == null) return;

        if (hasTimeOverlap(task)) {
            throw new ManagerSaveException("Задачи пересекаются по времени: " + task);
        }
        prioritizedTasks.add(task);
    }

    private boolean hasTimeOverlap(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isOverlap(existingTask, newTask));
    }

    private boolean isOverlap(Task existingTask, Task newTask) {
        LocalDateTime aStart = existingTask.getStartTime();
        LocalDateTime aEnd = existingTask.getEndTime();
        LocalDateTime bStart = newTask.getStartTime();
        LocalDateTime bEnd = newTask.getEndTime();

        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException {
        if (task == null) return;

        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        validateAndAddToPrioritized(task); // Проверка пересечений
    }

    public void addSubtask(Subtask subtask) throws ManagerSaveException {
        subtasks.put(subtask.getId(), subtask); // Добавляем подзадачу в список
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask); // Добавляем подзадачу в эпик
        updateEpicStatus(epic.getId()); // Обновляем статус эпика
    }

    protected void removeSubtask(int id) throws ManagerSaveException {
        if (!subtasks.containsKey(id)) {
            throw new ManagerSaveException("Subtask not found");
        }
        Subtask removedSubtask = subtasks.remove(id);
        if (removedSubtask != null) {
            Epic parentEpic = removedSubtask.getEpic();
            if (parentEpic != null) {
                parentEpic.removeSubtask(id); // Удаляем подзадачу из эпика
                updateEpicStatus(parentEpic.getId()); // Обновляем статус эпика
            }
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subtasks = epic.getSubtasks();
        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);
        boolean allDone = subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        if (task == null || !tasks.containsKey(task.getId())) return;

        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask); // Удаляем старую версию
        validateAndAddToPrioritized(task); // Проверка новой версии
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTaskById(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void createSubtask(Subtask subtask) throws ManagerSaveException {
        if (subtask == null || subtask.getEpic() == null) return;
        subtask.setId(idCounter++);
        validateAndAddToPrioritized(subtask); // Проверка пересечений
        subtasks.put(subtask.getId(), subtask);
        subtask.getEpic().addSubtask(subtask);
        updateEpic(subtask.getEpicId());
    }

    @Override
    public void createEpic(Epic epic) throws ManagerSaveException {
        if (epic == null) return;

        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId()); // Обновление статуса эпика
        updateEpicTiming(epic.getId()); // Обновление времени эпика
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) return;

        Subtask oldSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(oldSubtask); // Удаляем старую версию
        validateAndAddToPrioritized(subtask); // Проверка новой версии
        subtasks.put(subtask.getId(), subtask);
        updateEpic(subtask.getEpicId());
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
            Epic epic = epics.get(removed.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpic(epic.getId());
            }
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        subtasks.values().forEach(s -> {
            prioritizedTasks.remove(s);
            s.getEpic().removeSubtask(s.getId());
        });
        subtasks.clear();
        epics.values().forEach(this::updateEpic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;

        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId()); // Обновление статуса эпика
        updateEpicTiming(epic.getId()); // Обновление времени эпика
    }

    @Override
    public void deleteEpicById(int id) {
        Epic removed = epics.remove(id);
        if (removed != null) {
            removed.getSubtasks().forEach(s -> {
                subtasks.remove(s.getId());
                prioritizedTasks.remove(s);
            });
            historyManager.remove(id);
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id); // Предполагается, что epics — это Map<Integer, Epic>
        if (epic != null) {
            historyManager.add(epic); // Добавляем эпик в историю, если он найден
        }
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubtasks() : Collections.emptyList();
    }

    @Override
    public void clearEpics() {
        epics.values().forEach(e -> e.getSubtasks().forEach(prioritizedTasks::remove));
        epics.clear();
        subtasks.clear();
    }

    private void updateEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        // Обновление статуса
        List<Subtask> subs = getSubtasksForEpic(epicId);
        boolean allNew = subs.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW);
        boolean allDone = subs.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE);

        epic.setStatus(allDone ? TaskStatus.DONE :
                allNew ? TaskStatus.NEW : TaskStatus.IN_PROGRESS);
    }

    private void updateEpicTiming(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        // Обновляем время эпика
        epic.updateTiming();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    private final List<Task> history = new ArrayList<>();

    @Override
    public void clearHistory() {
        history.clear();
    }
}