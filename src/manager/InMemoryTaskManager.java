package manager;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public abstract class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    protected  final Map<Integer, Subtask> subtasks = new HashMap<>();
    private static final Map<Integer, Epic> epics = new HashMap<>();
    private static final HistoryManager historyManager = Managers.getDefaultHistory();

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).thenComparingInt(Task::getId)
    );

    // region Основные методы для ТЗ №8
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void validateAndAddToPrioritized(Task task) {
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

    private boolean isOverlap(Task a, Task b) {
        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd = a.getEndTime();
        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd = b.getEndTime();

        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }
    // endregion

    // region Управление задачами
    @Override
    public void createTask(Task task) {
        if (task == null) return;
        task.setId(idCounter++);
        validateAndAddToPrioritized(task); // Проверка пересечений
        tasks.put(task.getId(), task);
    }

    @Override
    public void addTask(Task task) {
        if (task.getStartTime() != null) {
            for (Task existingTask : tasks.values()) {
                System.out.println("Проверяем пересечение с задачей: " + existingTask);
                if (isTimeOverlap(existingTask, task)) {
                    System.out.println("Пересечение найдено между задачами: " + existingTask + " и " + task);
                    throw new IllegalArgumentException("Задачи пересекаются по времени!");
                }
            }
            prioritizedTasks.add(task);
        }
        tasks.put(task.getId(), task);
        System.out.println("Задача добавлена: " + task);
    }

    private boolean isTimeOverlap(Task existingTask, Task newTask) {
        if (existingTask.getStartTime() == null || newTask.getStartTime() == null) {
            return false; // Если у задачи нет времени, пересечения нет
        }
        LocalDateTime existingStart = existingTask.getStartTime();
        LocalDateTime existingEnd = existingTask.getEndTime();
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        System.out.println("Проверяем пересечение: ");
        System.out.println("Существующая задача: start=" + existingStart + ", end=" + existingEnd);
        System.out.println("Новая задача: start=" + newStart + ", end=" + newEnd);

        boolean overlap = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
        System.out.println("Результат проверки пересечения: " + overlap);
        return overlap;
    }

    public abstract void addEpic(Epic epic) throws ManagerSaveException;

    public void addSubtask(Subtask subtask) throws ManagerSaveException {
        subtasks.put(subtask.getId(), subtask); // Добавляем подзадачу в список
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask); // Добавляем подзадачу в эпик
        updateEpicStatus(epic); // Обновляем статус эпика
    }

    public void removeTask(int id) throws ManagerSaveException {
    }

    public abstract void removeEpic(int id) throws ManagerSaveException;

    public void removeSubtask(int id) throws ManagerSaveException {
        Subtask subtask = subtasks.remove(id); // Удаляем подзадачу из списка
        if (subtask != null) {
            Epic epic = subtask.getEpic();
            epic.removeSubtask(id); // Удаляем подзадачу из эпика
            updateEpicStatus(epic); // Обновляем статус эпика
        }
    }

    private void updateEpicStatus(Epic epic) {
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

    public void clearAllTasks() throws ManagerSaveException {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks(); // Очищаем подзадачи эпика
            updateEpicStatus(epic); // Обновляем статус эпика
        }
        tasks.clear(); // Очищаем обычные задачи
        subtasks.clear(); // Очищаем подзадачи
        epics.clear(); // Очищаем эпики
    }



    @Override
    public void updateTask(Task task) {
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
    // endregion

    // region Управление подзадачами
    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask == null || subtask.getEpic() == null) return;
        subtask.setId(idCounter++);
        validateAndAddToPrioritized(subtask); // Проверка пересечений
        subtasks.put(subtask.getId(), subtask);
        subtask.getEpic().addSubtask(subtask);
        updateEpic(subtask.getEpicId());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
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
    // endregion

    // region Управление эпиками
    @Override
    public void createEpic(Epic epic) {
        if (epic == null) return;
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        updateEpic(epic.getId());
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;
        epics.put(epic.getId(), epic);
        updateEpic(epic.getId());
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

    public static Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
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

        // Обновление времени
        epic.updateTiming();
    }
    // endregion

    // region История
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected Epic getEpic(int epicId) {
        return null;
    }
    // endregion
}