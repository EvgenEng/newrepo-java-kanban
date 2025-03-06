package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import task.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                sendText(exchange, gson.toJson(taskManager.getAllTasks()), HttpStatusCode.OK.getCode());
                break;
            case "POST":
                String body = new String(exchange.getRequestBody().readAllBytes());
                Task task = gson.fromJson(body, Task.class);
                try {
                    taskManager.createTask(task);
                    sendText(exchange, "", HttpStatusCode.CREATED.getCode());
                } catch (Exception e) {
                    sendNotAcceptable(exchange);
                }
                break;
            case "DELETE":
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/tasks")) {
                    taskManager.clearTasks();
                    sendText(exchange, "", HttpStatusCode.OK.getCode());
                } else if (path.matches("/tasks/\\d+")) {
                    int id = Integer.parseInt(path.split("/")[2]);
                    if (taskManager.getTaskById(id) == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    taskManager.deleteTaskById(id);
                    sendText(exchange, "", HttpStatusCode.OK.getCode());
                }
                break;
            default:
                sendText(exchange, METHOD_NOT_ALLOWED_MESSAGE, HttpStatusCode.METHOD_NOT_ALLOWED.getCode());
        }
    }
}