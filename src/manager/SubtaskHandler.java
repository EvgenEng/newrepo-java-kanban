package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import task.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), HttpStatusCode.OK.getCode());
                break;
            case "POST":
                String body = new String(exchange.getRequestBody().readAllBytes());
                Subtask subtask = gson.fromJson(body, Subtask.class);
                try {
                    taskManager.createSubtask(subtask);
                    sendText(exchange, "", HttpStatusCode.CREATED.getCode());
                } catch (Exception e) {
                    sendNotAcceptable(exchange);
                }
                break;
            case "DELETE":
                taskManager.clearSubtasks();
                sendText(exchange, "", HttpStatusCode.OK.getCode());
                break;
            default:
                sendText(exchange, METHOD_NOT_ALLOWED_MESSAGE, HttpStatusCode.METHOD_NOT_ALLOWED.getCode());
        }
    }
}