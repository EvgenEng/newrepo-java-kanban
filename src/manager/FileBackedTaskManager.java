package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String filePath;

    public FileBackedTaskManager(String path) {

    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        try {
            FileBackedTaskManager manager = new FileBackedTaskManager(file.getPath());
            manager.loadFromFile(); // Загрузка данных из файла
            return manager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }
    }

    @Override
    public void addTask(Task task) {
        try {
            super.addTask(task);
            saveToFile();
        } catch (ManagerSaveException e) {
            System.out.println("Ошибка при сохранении задачи в файл: " + e.getMessage());
        }
    }

    @Override
    public void removeTask(int id) throws ManagerSaveException {
        super.removeTask(id);
        saveToFile(); // Сохранение изменений после удаления задачи
    }

    // Метод для загрузки задач из файла
    private void loadFromFile() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        // Пропускаем первую строку (заголовочная)
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",");

            int id = Integer.parseInt(fields[0]);
            String type = fields[1];
            String name = fields[2];
            String status = fields[3];
            String description = fields[4];
            int epicId = fields[5].isEmpty() ? -1 : Integer.parseInt(fields[5]);

            Task task = createTask(type, id, name, status, description, epicId);
            super.addTask(task);
        }
    }

    // Метод для создания задачи на основе типа
    private Task createTask(String type, int id, String name, String status, String description, int epicId) {
        switch (type) {
            case "TASK":
                return new Task(name, status.equals("DONE"), description);
            case "SUBTASK":
                Epic epic = findEpicById(epicId); // Находим эпик по его идентификатору
                if (epic == null) {
                    throw new IllegalStateException("Эпик с id=" + epicId + " не найден.");
                }
                return new Subtask(name, status.equals("DONE"), description, epic);
            case "EPIC":
                return new Epic(name, status.equals("DONE"), description);
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }

    private Epic findEpicById(int epicId) {
        return null;
    }

    private Task parseTask(String line) {
        // Разделяем строку на части, предполагая, что она содержит название задачи и статус через разделитель
        String[] parts = line.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неверный формат строки: " + line);
        }

        String title = parts[0].trim();
        boolean isDone = Boolean.parseBoolean(parts[1].trim());

        return new Task(title, isDone);
    }


    // Метод для сохранения задач в файл
    private void saveToFile() throws ManagerSaveException {
        try {
            List<Task> tasks = getTasks();
            List<String> lines = new ArrayList<>();

            // Добавляем заголовочную строку
            lines.add("id,type,name,status,description,epic");

            // Преобразуем каждую задачу в строку в формате CSV
            for (Task task : tasks) {
                String csvLine = String.format("%d,%s,%s,%s,%s,%s",
                        task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), task.getEpicId());
                lines.add(csvLine);
            }

            Files.write(Paths.get(filePath), lines);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    public String taskToString(Task task) {
        return task.toString();
    }

    public List<Task> getTasks() {
        return List.of();
    }
}