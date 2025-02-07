package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public final String filePath;

    // 1. Добавляем enum для типов задач
    enum TaskType {
        TASK,
        EPIC,
        SUBTASK
    }

    public FileBackedTaskManager(String path) throws ManagerSaveException {
        this.filePath = path;
        createFileIfNotExists();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getPath());
        if (file.exists() && file.length() > 0) {
            try {
                manager.loadFromFile();
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка загрузки");
            }
        }
        return manager;
    }

    private void createFileIfNotExists() throws ManagerSaveException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось создать файл");
            }
        }
    }

    @Override
    public void addTask(Task task) throws ManagerSaveException {
        super.addTask(task);
        saveToFile();
    }

    @Override
    public void addEpic(Epic epic) throws ManagerSaveException {
        super.addEpic(epic);
        saveToFile();
    }

    @Override
    public void addSubtask(Subtask subtask) throws ManagerSaveException {
        super.addSubtask(subtask);
        saveToFile();
    }

    @Override
    public void removeTask(int id) throws ManagerSaveException {
        super.removeTask(id);
        saveToFile();
    }

    @Override
    public void removeEpic(int id) throws ManagerSaveException {
        super.removeEpic(id);
        saveToFile();
    }

    @Override
    public void removeSubtask(int id) throws ManagerSaveException {
        super.removeSubtask(id);
        saveToFile();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        saveToFile();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        saveToFile();
    }

    private void loadFromFile() throws IOException, ManagerSaveException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (lines.size() <= 1) return;

        // 2. Удаляем разделение на эпики и остальные задачи. Один цикл для всех типов.
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            createTaskFromLine(line);
            System.out.println("Загружена задача: " + line);
        }
    }

    private void createTaskFromLine(String line) throws ManagerSaveException {
        String[] fields = line.split(",");
        int id = Integer.parseInt(fields[0]);

        // 3. Используем enum вместо строковых констант
        TaskType type = TaskType.valueOf(fields[1].trim());

        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3].trim());
        String description = fields[4];
        int epicId = fields.length > 5 && !fields[5].isEmpty()
                ? Integer.parseInt(fields[5])
                : -1;

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                super.addTask(task);
                break;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                super.addEpic(epic);
                break;
            case SUBTASK:
                Epic epicForSubtask = super.getEpic(epicId);
                if (epicForSubtask != null) {
                    Subtask subtask = new Subtask(name, description, epicForSubtask);
                    subtask.setId(id);
                    subtask.setStatus(status);
                    super.addSubtask(subtask);
                }
                break;
            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        }
    }

    public void saveToFile() throws ManagerSaveException {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,epic");

            // Сначала эпики
            for (Epic epic : getAllEpics()) {
                lines.add(TaskConverter.taskToString(epic));
            }

            // Затем подзадачи
            for (Subtask subtask : getAllSubtasks()) {
                lines.add(TaskConverter.taskToString(subtask));
            }

            // Обычные задачи
            for (Task task : getAllTasks()) {
                lines.add(TaskConverter.taskToString(task));
            }

            Files.write(Paths.get(filePath), lines);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }
}