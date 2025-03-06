package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import task.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                sendText(exchange, gson.toJson(taskManager.getAllEpics()), HttpStatusCode.OK.getCode());
                break;
            case "POST":
                String body = new String(exchange.getRequestBody().readAllBytes());
                Epic epic = gson.fromJson(body, Epic.class);
                try {
                    taskManager.createEpic(epic);
                    sendText(exchange, "", HttpStatusCode.CREATED.getCode());
                } catch (Exception e) {
                    sendNotAcceptable(exchange);
                }
                break;
            case "DELETE":
                taskManager.clearEpics();
                sendText(exchange, "", HttpStatusCode.OK.getCode());
                break;
            default:
                sendText(exchange, METHOD_NOT_ALLOWED_MESSAGE, HttpStatusCode.METHOD_NOT_ALLOWED.getCode());
        }
    }
}