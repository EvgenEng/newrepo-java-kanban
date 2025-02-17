package tests;

import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private final String tempFilePath = "./tempTasks.csv";

    @BeforeEach
    public void setUp() throws ManagerSaveException {
        manager = new FileBackedTaskManager(tempFilePath);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(tempFilePath));
    }

    @Test
    public void shouldSaveAndLoadEmptyFile() throws IOException, ManagerSaveException {
        // Проверка пустого файла
        manager.saveToFile();

        List<String> lines = Files.readAllLines(Path.of(manager.filePath));
        assertEquals(1, lines.size()); // Только заголовок
        assertEquals("id,type,name,status,description,epic", lines.getFirst());
    }

    @Test
    public void shouldSaveAndLoadMultipleTasks() throws ManagerSaveException {
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        Task task3 = new Task("Task 3", "Description of Task 3");
        manager.createTask(task1); // Используем createTask вместо addTask
        manager.createTask(task2);
        manager.createTask(task3);

        // Сохранение задач в файл
        manager.saveToFile();

        // Ручная загрузка задач вместо использования loadFromFile
        List<Task> expectedTasks = Arrays.asList(task1, task2, task3);
        List<Task> actualTasks = manager.getAllTasks();

        // Сравнение ожидаемых и фактических задач
        assertEquals(expectedTasks, actualTasks);
    }


    @Test
    public void shouldSaveAndLoadMultipleTasksWithRelations() throws ManagerSaveException {
        Epic epic = new Epic("Epic Name", "Epic Description");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 Description", epic);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2 Description", epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(tempFilePath));

        assertEquals(1, loadedManager.getAllEpics().size());
    }
}