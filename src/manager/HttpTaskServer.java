package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import interfaces.TaskManager;
import task.Task;
import task.Subtask;
import task.Epic;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer extends BaseHttpHandler {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager; // Исп. переданный менеджер
        this.gson = new Gson(); //JSON
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        configureHandlers();
    }

    public static Gson getGson() {
        return null;
    }

    private void configureHandlers() {
        server.createContext("/tasks", new TaskHandler());
        server.createContext("/subtasks", new SubtaskHandler());
        server.createContext("/epics", new EpicHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }

    private class TaskHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
                    //или sendText(exchange, "{\"message\":\"No tasks found\"}", 200);
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    Task task = gson.fromJson(body, Task.class);
                    try {
                        taskManager.createTask(task);
                        sendText(exchange, "", 201);
                    } catch (Exception e) {
                        sendNotAcceptable(exchange);
                    }
                    break;
                case "DELETE":
                    String path = exchange.getRequestURI().getPath();
                    if (path.equals("/tasks")) {
                        taskManager.clearTasks();
                        sendText(exchange, "", 200);
                    } else if (path.matches("/tasks/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        if (taskManager.getTaskById(id) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        taskManager.deleteTaskById(id);
                        sendText(exchange, "", 200);
                    }
                    break;
                default:
                    sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        }
    }

    private class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    try {
                        taskManager.createSubtask(subtask);
                        sendText(exchange, "", 201);
                    } catch (Exception e) {
                        sendNotAcceptable(exchange);
                    }
                    break;
                case "DELETE":
                    taskManager.clearSubtasks();
                    sendText(exchange, "", 200);
                    break;
                default:
                    sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        }
    }

    private class EpicHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    Epic epic = gson.fromJson(body, Epic.class);
                    try {
                        taskManager.createEpic(epic);
                        sendText(exchange, "", 201);
                    } catch (Exception e) {
                        sendNotAcceptable(exchange);
                    }
                    break;
                case "DELETE":
                    taskManager.clearEpics();
                    sendText(exchange, "", 200);
                    break;
                default:
                    sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        }
    }

    private class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
                    break;
                case "DELETE":
                    taskManager.clearHistory();
                    sendText(exchange, "", 200);
                    break;
                default:
                    sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        }
    }

    private class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
                    break;
                default:
                    sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        }
    }

    public class Main {
        public static void main(String[] args) {
            try {
                // Создаём TaskManager
                TaskManager taskManager = new FileBackedTaskManager("tasks.csv");

                // Передаём TaskManager в HttpTaskServer
                HttpTaskServer server;
                server = new HttpTaskServer(taskManager);
                server.start();
            } catch (IOException | ManagerSaveException e) {
                System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            }
        }
    }
}