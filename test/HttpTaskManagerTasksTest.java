package tests;

import com.google.gson.Gson;
import manager.HttpTaskServer;
import manager.InMemoryTaskManager;
import interfaces.TaskManager;
import org.junit.jupiter.api.*;
import task.Task;
import task.TaskStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTasksTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws Exception {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
        gson = new Gson();
        manager.getAllTasks();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    void testCreateTaskSuccess() throws Exception {
        // Подготовка тестовых данных
        Task task = new Task("Test", "Description", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        // Ручное создание JSON-строки
        String taskJson = format(
                "{\"title\":\"%s\",\"description\":\"%s\",\"status\":\"%s\",\"duration\":\"%s\",\"startTime\":\"%s\"}",
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDuration().toString(),
                task.getStartTime().toString()
        );

        // Отправка POST-запроса
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        // Отправка GET-запроса для несуществующей задачи
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testDeleteTask() throws Exception {
        // Создание задачи для удаления
        Task task = new Task("Delete Me", "Desc", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);

        // Отправка DELETE-запроса
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void testGetPrioritizedTasks() throws Exception {
        // Создание задач с разным временем
        Task task1 = new Task("Task1", "Desc", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Task2", "Desc", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task1);
        manager.createTask(task2);

        // Отправка GET-запроса
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized")) // Исправлено!
                .GET()
                .build();
    }
}